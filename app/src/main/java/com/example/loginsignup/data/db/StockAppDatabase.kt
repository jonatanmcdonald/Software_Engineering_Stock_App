package com.example.loginsignup.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.loginsignup.R
import com.example.loginsignup.data.db.dao.AlertDao
import com.example.loginsignup.data.db.dao.NoteDao
import com.example.loginsignup.data.db.dao.PortfolioDao
import com.example.loginsignup.data.db.dao.StockDao
import com.example.loginsignup.data.db.dao.TransactionDao
import com.example.loginsignup.data.db.dao.UserDao
import com.example.loginsignup.data.db.dao.WatchListDao
import com.example.loginsignup.data.db.entity.Alert
import com.example.loginsignup.data.db.entity.Note
import com.example.loginsignup.data.db.entity.Portfolio
import com.example.loginsignup.data.db.entity.Stock
import com.example.loginsignup.data.db.entity.Transaction
import com.example.loginsignup.data.db.entity.User
import com.example.loginsignup.data.db.entity.WatchList
import com.example.loginsignup.data.db.view.WatchListWithSymbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ---------- CSV import helpers ----------

suspend fun StockAppDatabase.populateFromCsv(context: Context) {
    val items = parseStocksCsv(context)
    stockDao().insertChunked(items)
}

suspend fun parseStocksCsv(context: Context): List<Stock> = withContext(Dispatchers.IO) {
    val list = ArrayList<Stock>(10_000)
    context.resources.openRawResource(R.raw.stocks).bufferedReader().useLines { lines ->
        var first = true               // FIX: start true so we skip the header once
        lines.forEach { line ->
            if (first) { first = false; return@forEach }  // skip header row
            if (line.isBlank()) return@forEach

            // NOTE: simple splitter; switch to a CSV lib if you have quoted commas in names
            val parts = line.split(',')

            // Adjust indices to your CSV layout
            // [0]=id (unused), [1]=ticker, [2]=name, [3]=market, [4]=locale, [5]=primaryExchange, [6]=type, [7]=currencyName
            val ticker          = parts.getOrNull(1)?.trim().orEmpty()
            val name            = parts.getOrNull(2)?.trim().orEmpty()
            val market          = parts.getOrNull(3)?.trim().takeUnless { it.isNullOrEmpty() }
            val locale          = parts.getOrNull(4)?.trim().takeUnless { it.isNullOrEmpty() }
            val primaryExchange = parts.getOrNull(5)?.trim().takeUnless { it.isNullOrEmpty() }
            val type            = parts.getOrNull(6)?.trim().takeUnless { it.isNullOrEmpty() }
            val currencyName    = parts.getOrNull(7)?.trim().takeUnless { it.isNullOrEmpty() }

            if (ticker.isNotEmpty() && name.isNotEmpty()) {
                list += Stock(
                    ticker = ticker,
                    name = name,
                    market = market,
                    locale = locale,
                    primaryExchange = primaryExchange,
                    type = type,
                    currencyName = currencyName
                )
            }
        }
    }
    list
}

// ---------- Triggers (align table names to your @Entity(tableName=...)) ----------
private fun installTriggers(db: SupportSQLiteDatabase) {
    // 1) Make sure we have the right uniqueness
    //    (If you previously had a UNIQUE(symbol) index, drop it in a migration.)
    db.execSQL("""
        CREATE UNIQUE INDEX IF NOT EXISTS ux_portfolio_user_symbol
        ON portfolio(userId, symbol)
    """.trimIndent())

    // ---------- BUY: upsert & recompute moving average ----------
    db.execSQL("""
        CREATE TRIGGER IF NOT EXISTS trg_apply_buy
        AFTER INSERT ON transactions
        WHEN UPPER(NEW.side) = 'BUY'
        BEGIN
          -- One-shot upsert: insert first position, or update existing
          INSERT INTO portfolio(symbol, userId, qty, cost_basis, avg_cost, realized_pnl)
          VALUES(
            NEW.symbol,
            NEW.userId,
            NEW.qty,
            (NEW.qty * NEW.price) + IFNULL(NEW.fees, 0),
           ((NEW.qty * NEW.price) + IFNULL(NEW.fees, 0)) / NEW.qty,
            0
          )
          ON CONFLICT(userId, symbol) DO UPDATE SET
            qty        = qty + NEW.qty,
            cost_basis = cost_basis + (NEW.qty * NEW.price) + IFNULL(NEW.fees, 0),
            avg_cost   = CASE
                           WHEN (qty + NEW.qty) = 0 THEN 0
                           ELSE (cost_basis + (NEW.qty * NEW.price) + IFNULL(NEW.fees, 0))
                                / (qty + NEW.qty)
                         END;
        END;
    """.trimIndent())

    db.execSQL("""
        CREATE TRIGGER IF NOT EXISTS trg_block_oversell
        BEFORE INSERT ON transactions
        WHEN UPPER(NEW.side)='SELL' AND
             (SELECT IFNULL(qty,0) FROM portfolio WHERE userId=NEW.userId AND symbol=NEW.symbol) < NEW.qty
        BEGIN
          SELECT RAISE(ABORT, 'Insufficient shares to sell');
        END;
        """.trimIndent())

    // ---------- SELL: upsert (ensures row exists), then reduce qty/cost & realize PnL ----------
    db.execSQL("""
        CREATE TRIGGER IF NOT EXISTS trg_apply_sell
        AFTER INSERT ON transactions
        WHEN UPPER(NEW.side) = 'SELL' 
        BEGIN
          -- Ensure a row exists (if somehow selling first; qty will go negative)
          INSERT INTO portfolio(symbol, userId, qty, cost_basis, avg_cost, realized_pnl)
          VALUES(NEW.symbol, NEW.userId, 0, 0, 0, 0)
          ON CONFLICT(userId, symbol) DO NOTHING;

          -- Realize P&L vs current average, reduce qty and cost basis, recompute avg_cost
          UPDATE portfolio
          SET
            realized_pnl = realized_pnl + ((NEW.price - avg_cost) * NEW.qty) - IFNULL(NEW.fees, 0),
            qty          = qty - NEW.qty,
            cost_basis   = cost_basis - (avg_cost * NEW.qty),
            avg_cost     = CASE
                             WHEN (qty - NEW.qty) = 0 THEN 0
                             ELSE (cost_basis - (avg_cost * NEW.qty)) / (qty - NEW.qty)
                           END
          WHERE symbol = NEW.symbol AND userId = NEW.userId;

          -- If qty ~ 0 after the sale, zero out basis/avg for a clean closed position
          DELETE FROM portfolio
          WHERE symbol = NEW.symbol AND userId = NEW.userId AND ABS(qty) < 1e-9;
        END;
    """.trimIndent())

    Log.d("StockAppDatabase", "Triggers installed")
}
val MIGRATION_X_Y = object : Migration(1, 31) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // Drop any old unique-on-symbol index (name may vary by Room version)
        db.execSQL("DROP INDEX IF EXISTS index_portfolio_symbol")

        // Re-create non-unique symbol index
        db.execSQL("CREATE INDEX IF NOT EXISTS index_portfolio_symbol ON portfolio(symbol)")

        // Ensure composite unique index exists
        db.execSQL("""
      CREATE UNIQUE INDEX IF NOT EXISTS ux_portfolio_user_symbol
      ON portfolio(userId, symbol)
    """.trimIndent())

        installTriggers(db)
    }
}

// ---------- Room DB ----------

@Database(
    entities = [User::class, WatchList::class, Stock::class, Portfolio::class, Transaction::class, Alert::class, Note::class],
    views = [WatchListWithSymbol::class],
    version = 31,
    exportSchema = false
)
abstract class StockAppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun alertDao(): AlertDao

    abstract fun noteDao(): NoteDao
    abstract fun watchListDao(): WatchListDao
    abstract fun stockDao(): StockDao

    abstract fun portfolioDao(): PortfolioDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile private var INSTANCE: StockAppDatabase? = null

        fun getDatabase(context: Context): StockAppDatabase {
            val existing = INSTANCE
            if (existing != null) return existing

            return synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        StockAppDatabase::class.java,
                        "stock_app_database"
                    )
                        // If you want to preserve prod data, add real migrations 2→6 (keep MIGRATION_1_2 if relevant)
                        .addMigrations(MIGRATION_X_Y)
                        .fallbackToDestructiveMigration(true) // FIX: no boolean parameter
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                // Fresh install -> create triggers now
                                installTriggers(db)
                            }

                            // (Optional) onOpen: keep idempotent in case a prior build missed them
                            override fun onOpen(db: SupportSQLiteDatabase) {
                                super.onOpen(db)
                                installTriggers(db) // uncomment if you want to ensure on every open
                            }
                        })
                        .build()
                        .also { db ->
                            INSTANCE = db

                            // Prepopulate stocks table once (safe after build)
                            CoroutineScope(Dispatchers.IO).launch {
                                // Only import if empty
                                if (db.stockDao().countRows() == 0L) {
                                    db.populateFromCsv(context.applicationContext)
                                }

                            }
                        }

                instance
            }
        }
    }
}
