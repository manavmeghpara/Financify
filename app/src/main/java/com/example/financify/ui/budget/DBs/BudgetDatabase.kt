package com.example.financify.ui.budget.DBs

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.financify.ui.savings.savingsDB.ioThread

@Database(entities = [Category::class, Expense::class, Purchase::class], version = 4, exportSchema = false)
@TypeConverters(CalendarTypeConverter::class)
abstract class BudgetDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun purchaseDao(): PurchaseDao

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
                            getDatabase(context).purchaseDao().insertData(PREPOPULATE_PURCHASES)
                        }
                    }
                })
                .fallbackToDestructiveMigration().build()

        val PREPOPULATE_CATEGORIES = listOf(Category(name="Rent", amount=1200), Category(name="Entertainment", amount=100), Category(name="Food", amount=200))
        val PREPOPULATE_PURCHASES = listOf(Purchase(categoryName="Entertainment", name="Movie ticket", amount=15), Purchase(categoryName="Food", name="Pizza", amount=15))
        val PREPOPULATE_EXPENSES = listOf(Expense(categoryName="Rent", name="Rent Payment", amount=1000), Expense(categoryName="Entertainment", name="Netflix subscription", amount=20))
    }
}