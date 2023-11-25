package com.example.financify.ui.dashboard

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.financify.ui.savings.savingsDB.ioThread

@Database(entities = [Category::class, Expense::class], version = 3, exportSchema = false)
abstract class BudgetDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: BudgetDatabase? = null

        fun getDatabase(context: Context): BudgetDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                BudgetDatabase::class.java, "Sample.db")
                // prepopulate the database after onCreate was called
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // insert the data on the IO Thread
                        ioThread {
                            getDatabase(context).categoryDao().insertData(PREPOPULATE_CATEGORIES)
                            getDatabase(context).expenseDao().insertData(PREPOPULATE_EXPENSES)
                        }
                    }
                })
                .fallbackToDestructiveMigration().build()

        val PREPOPULATE_CATEGORIES = listOf(Category(id=1, name="Rent", amount=1200), Category(id=2, name="Entertainment", amount=100))
        val PREPOPULATE_EXPENSES = listOf(Expense(categoryId=1, name="Rent Payment", amount=1000), Expense(categoryId=2, name="Netflix subscription", amount=20))
    }
}