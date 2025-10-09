package com.example.loginsignup.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class StockAppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao

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
                    "user_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}