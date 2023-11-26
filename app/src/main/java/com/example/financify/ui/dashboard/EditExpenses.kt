package com.example.financify.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

    private lateinit var categoryAdapter: ArrayAdapter<Category>

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

        // Set up the spinner adapter for dialogs
        categoryAdapter = ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoryViewModel.allCategoriesLiveData.observe(this) { categories ->
            categoryAdapter.clear()
            categoryAdapter.addAll(categories)
            categoryAdapter.notifyDataSetChanged()
        }

        // TODO: Add/edit expenses
//        expenseListView.setOnItemClickListener { _, _, position, _ ->
//            showEditExpenseDialog(position)
//        }

        addExpenseButton.setOnClickListener {
            showAddExpenseDialog()
        }

        finishButton.setOnClickListener {
            finish()
        }
    }

    private fun showAddExpenseDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_expense, null)
        val categorySpinner: Spinner = dialogView.findViewById(R.id.expenseCategorySpinner)
        val editExpenseNameEditText: EditText = dialogView.findViewById(R.id.editExpenseNameEditText)
        val editExpenseAmountEditText: EditText = dialogView.findViewById(R.id.editExpenseAmountEditText)
        val editExpenseButton: Button = dialogView.findViewById(R.id.editExpenseButton)
        val deleteExpenseButton: Button = dialogView.findViewById(R.id.deleteExpenseButton)
        deleteExpenseButton.visibility = View.GONE

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Add New Expense")
        val dialog = dialogBuilder.show()

        categorySpinner.adapter = categoryAdapter

        editExpenseButton.setOnClickListener {
            var expenseName = editExpenseNameEditText.text.toString()
            val expenseAmount = editExpenseAmountEditText.text.toString()
            val expenseCategory = categorySpinner.selectedItem.toString()

            // Validate input and add the expense
            if (expenseName.isNotEmpty() && expenseAmount.isNotEmpty()) {
                val newExpense = Expense(categoryName = expenseCategory, name = expenseName, amount = expenseAmount.toInt())
                expenseViewModel.insertExpense(newExpense)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Name and amount are required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}