package com.example.financify.ui.dashboard

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.financify.R
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider


class EditExpenses : AppCompatActivity() {
    private lateinit var expenseListView: ListView
    private lateinit var addExpenseButton: Button
    private lateinit var finishButton: Button
    private lateinit var expenseAdapter: ExpensesAdapter

    private lateinit var database: BudgetDatabase

    private lateinit var categoryDao: CategoryDao
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var categoryViewModelFactory: CategoryViewModelFactory
    private lateinit var categoryViewModel: CategoryViewModel

    private lateinit var expenseDao: ExpenseDao
    private lateinit var expenseRepository: ExpenseRepository
    private lateinit var expenseViewModelFactory: ExpenseViewModelFactory
    private lateinit var expenseViewModel: ExpenseViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_expenses)

        expenseListView = findViewById(R.id.expenseListView)
        addExpenseButton = findViewById(R.id.addExpenseButton)
        finishButton = findViewById(R.id.finishButton)

        database = BudgetDatabase.getDatabase(this)

        categoryDao = database.categoryDao()
        categoryRepository = CategoryRepository(categoryDao)
        categoryViewModelFactory = CategoryViewModelFactory(categoryRepository)
        categoryViewModel = ViewModelProvider(this, categoryViewModelFactory)[CategoryViewModel::class.java]

        expenseDao = database.expenseDao()
        expenseRepository = ExpenseRepository(expenseDao)
        expenseViewModelFactory = ExpenseViewModelFactory(expenseRepository)
        expenseViewModel = ViewModelProvider(this, expenseViewModelFactory)[ExpenseViewModel::class.java]

        // Initialize empty adapter while waiting for DB
        expenseAdapter = ExpensesAdapter(this, mutableListOf())
        expenseViewModel.allExpensesLiveData.observe(this, Observer { expenses ->
            expenseAdapter.updateData(expenses)
        })
        expenseListView.adapter = expenseAdapter
    }
}