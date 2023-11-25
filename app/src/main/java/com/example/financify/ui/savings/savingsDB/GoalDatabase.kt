package com.example.financify.ui.savings.savingsDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.financify.ui.stocks.stockDB.StockDao
import com.example.financify.ui.stocks.stockDB.StockEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Database(entities = [GoalEntity::class], version = 1)
abstract class GoalDatabase : RoomDatabase(){
    abstract val goalDatabaseDao : GoalDao

    companion object{

        @Volatile
        private var dbInstance: GoalDatabase ?= null

        fun getInstance(context : Context): GoalDatabase{
            synchronized(this){
                var instance = dbInstance
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        GoalDatabase::class.java, "goal_data")
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                // insert the data on the IO Thread

                                GlobalScope.launch {
                                    getInstance(context).goalDatabaseDao.insert(PREPOPULATE_DATA[0])
                                    getInstance(context).goalDatabaseDao.insert(PREPOPULATE_DATA[1])
                                }
                            }
                        })
                        .build()
                    dbInstance = instance
                }
                return instance
            }
        }

        val PREPOPULATE_DATA = listOf(GoalEntity(0,"Electronic", "IPhone 15", 1500.0, 200.0),
            GoalEntity(0,"Vehicle", "Bike", 300.0, 50.0))
    }
}

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun ioThread(f : () -> Unit) {
    IO_EXECUTOR.execute(f)
}