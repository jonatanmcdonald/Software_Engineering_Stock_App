package com.example.loginsignup.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.loginsignup.data.db.dao.StockDao
import com.example.loginsignup.data.db.entity.User
import com.example.loginsignup.data.db.dao.UserDao
import com.example.loginsignup.data.db.entity.WatchList
import com.example.loginsignup.data.db.dao.WatchListDao
import com.example.loginsignup.data.db.entity.Stock
import com.example.loginsignup.data.db.view.WatchListWithSymbol



@Database(entities = [User::class, WatchList::class, Stock::class], views = [WatchListWithSymbol::class], version = 5, exportSchema = false)
abstract class StockAppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun watchListDao(): WatchListDao

    abstract fun stockDao(): StockDao


    companion object{
        @Volatile
        private var INSTANCE: StockAppDatabase? = null

        fun getDatabase(context: Context): StockAppDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return  tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                StockAppDatabase::class.java,
                                "stock_app_database"
                            ).createFromAsset("stock_app_database").fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}