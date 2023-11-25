package com.example.financify.ui.stocks.stockDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StockEntity::class], version = 1)
abstract class StockDatabase : RoomDatabase(){
    abstract val stockDatabaseDao : StockDao

    companion object{

        @Volatile
        private var dbInstance: StockDatabase ?= null

        fun getInstance(context : Context): StockDatabase{
            synchronized(this){
                var instance = dbInstance
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        StockDatabase::class.java, "stock_data")
                        .build()
                    dbInstance = instance
                }
                return instance
            }
        }
    }
}