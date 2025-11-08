package com.example.loginsignup.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginsignup.App
import com.example.loginsignup.data.db.StockAppDatabase
import com.example.loginsignup.data.db.StockAppRepository
import com.example.loginsignup.data.db.entity.Portfolio
import com.example.loginsignup.data.db.entity.Transaction
import com.example.loginsignup.screens.LivePortfolio
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

private const val CALLS_PER_MINUTE = 60              // your real limit
private const val GAP_MS = 60_000L / CALLS_PER_MINUTE
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class PortfolioViewModel(application: Application) : AndroidViewModel(application) {
    private val limiter = (application as App).rateLimiter
    private var priceJob: Job? = null
    private val debounceDuration = 500L

    private val _portfolioRows = MutableStateFlow<List<LivePortfolio>>(emptyList())
    val portfolioRows: StateFlow<List<LivePortfolio>> = _portfolioRows.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val repository: StockAppRepository

    init {
        val db = StockAppDatabase.getDatabase(application)
        val userDao = db.userDao()
        val watchListDao = db.watchListDao()
        val stockDao = db.stockDao()
        val transactionDao = db.transactionDao()
        val portfolioDao = db.portfolioDao()

        repository = StockAppRepository(userDao, watchListDao, stockDao, transactionDao, portfolioDao)

    }


    private fun reconcileWithPortfolioList(portfolio: List<Portfolio>) {
        _portfolioRows.update { current ->
            val prevById = current.associateBy { it.id }

            // Build list exactly from DB rows; anything missing is dropped (handles deletions)
            portfolio.map { p ->
                val prev = prevById[p.id]
                if (prev != null) {
                    // Keep live fields; refresh the static labels/quantities from DB
                    prev.copy(
                        ticker      = p.symbol,          // Portfolio.symbol is non-null
                        qty         = p.qty,
                        avgCost     = p.avg_cost,
                        costBasis   = p.cost_basis,
                        realizedPnl = p.realized_pnl
                    )
                } else {
                    // New row: seed placeholders; live fields will be filled by the price loop
                    LivePortfolio(
                        id          = p.id,
                        ticker      = p.symbol,
                        qty         = p.qty,
                        avgCost     = p.avg_cost,
                        costBasis   = p.cost_basis,
                        realizedPnl = p.realized_pnl
                    )
                }
            }
        }
    }


    /** Call this once when the screen opens (pass the signed-in user's id). */
    fun startPortfolioValueUpdate(userId: Int) {
        priceJob?.cancel()
        priceJob = viewModelScope.launch {
            // 1) Keep a live, mutable snapshot of the current watchlist for rotation
            var rotation: List<Portfolio> = emptyList()

            // 2) Listen to watchlist changes and reconcile UI labels immediately
            val portfolioCollector = launch {
                repository.observeUserPortfolio(userId)
                    .distinctUntilChanged() // avoid noisy re-emits
                    .collectLatest { list ->
                        rotation = list
                        reconcileWithPortfolioList(list) // update names/tickers/notes instantly
                    }
            }

            // 3) Continuous drip loop: one API call every GAP_MS, cycling through rotation
            val existingIndex = AtomicInteger(0)
            val index = existingIndex // (or keep a local var if you prefer)
            while (isActive) {
                val snapshot = rotation // read current list
                if (snapshot.isNotEmpty()) {
                    val i = index.getAndIncrement()
                    val target = snapshot[i % snapshot.size]

                    try {
                        val row = updateOneRow(target) // one API call
                        mergeRow(row)                  // atomic UI update
                    } catch (t: Throwable) {
                        // swallow; we'll try again next cycle
                        Log.w("PortfolioViewModel", "fetch failed for ${target.symbol}: ${t.message}")
                    }
                }
                delay(GAP_MS)
            }
            // cancel child collector if loop exits
            portfolioCollector.cancel()
        }
    }

    /** Upsert a single row atomically, preserving watchlist order. */
    private fun mergeRow(row: LivePortfolio) {
        _portfolioRows.update { current ->
            val byId = current.associateBy { it.id }.toMutableMap()
            byId[row.id] = row
            current.map { byId[it.id]!! }
        }
    }

    private suspend fun updateOneRow(p: Portfolio): LivePortfolio {
        val existing = _portfolioRows.value.find { it.id == p.id }

        return try {
            val resp = limiter.run { repository.fetchPrice(p.symbol) } // network gated globally

            val last: Double?        = resp.price
            val changePerShare: Double? = resp.change            // absolute per-share
            // Normalize percent: if API sends 0.0123 as fraction, convert to 1.23%
            val pct: Double? = resp.percentChange?.let { if (kotlin.math.abs(it) <= 1.0) it * 100.0 else it }

            val prevClose: Double? = when {
                changePerShare != null -> last?.minus(changePerShare)
                else -> existing?.prevClose ?: existing?.last
            }

            val marketValue: Double? = last?.let { it * p.qty }
            val unrealized: Double?  = marketValue?.let { (it - p.avg_cost) * p.qty }

            val dayChange: Double?   = changePerShare?.let { it * p.qty }
            val totalPnl: Double?    = unrealized?.let { it + p.realized_pnl }

            // If we already had a live row, keep stable fields from DB + update live numbers
            (existing ?: LivePortfolio(
                id          = p.id,
                ticker      = p.symbol,
                qty         = p.qty,
                avgCost     = p.avg_cost,
                costBasis   = p.cost_basis,
                realizedPnl = p.realized_pnl
            )).copy(
                // refresh DB-backed static fields in case they changed
                ticker      = p.symbol,
                qty         = p.qty,
                avgCost     = p.avg_cost,
                costBasis   = p.cost_basis,
                realizedPnl = p.realized_pnl,

                // live-calculated fields
                last          = last,
                prevClose     = prevClose,
                unrealizedPnl = unrealized,
                dayChangePct  = pct,
                marketValue   = marketValue,
                totalPnl      = totalPnl,
                dayChange     = dayChange
            )

        } catch (t: Throwable) {
            // Keep whatever we had; if none, return a placeholder using DB values
            existing ?: LivePortfolio(
                id          = p.id,
                ticker      = p.symbol,
                qty         = p.qty,
                avgCost     = p.avg_cost,
                costBasis   = p.cost_basis,
                realizedPnl = p.realized_pnl
            )
        }
    }

    fun sellStock(stockId: Long, qty: Double) = viewModelScope.launch {
        try {
            repository.sellStock(stockId, qty)

        } catch (e: Throwable) {
            Log.e("PortfolioViewModel", "Failed to sell stock: ${e.message}")
        }
    }

    fun getTransForUser(userId: Int): Flow<List<Transaction>> {
        return repository.getTransForUser(userId)
    }

}
