package com.example.financify.ui.budget

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.financify.R
import com.example.financify.ui.budget.DBs.BudgetDatabase
import com.example.financify.ui.budget.DBs.Category
import com.example.financify.ui.budget.DBs.CategoryDao
import com.example.financify.ui.budget.DBs.CategoryRepository
import com.example.financify.ui.budget.DBs.CategoryViewModel
import com.example.financify.ui.budget.DBs.CategoryViewModelFactory
import com.example.financify.ui.budget.DBs.Expense
import com.example.financify.ui.budget.DBs.ExpenseDao
import com.example.financify.ui.budget.DBs.ExpenseRepository
import com.example.financify.ui.budget.DBs.ExpenseViewModel
import com.example.financify.ui.budget.DBs.ExpenseViewModelFactory
import com.example.financify.ui.budget.DBs.ExpensesAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

        expenseListView.setOnItemClickListener { _, _, position, _ ->
            showEditExpenseDialog(position)
        }

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
        val addReminderButton: Button = dialogView.findViewById(R.id.addReminderButton)
        val deleteExpenseButton: Button = dialogView.findViewById(R.id.deleteExpenseButton)
        addReminderButton.visibility = View.GONE
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

    private fun showEditExpenseDialog(position: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_expense, null)
        val categorySpinner: Spinner = dialogView.findViewById(R.id.expenseCategorySpinner)
        val editExpenseNameEditText: EditText = dialogView.findViewById(R.id.editExpenseNameEditText)
        val editExpenseAmountEditText: EditText = dialogView.findViewById(R.id.editExpenseAmountEditText)
        val editExpenseDialogButton: Button = dialogView.findViewById(R.id.editExpenseButton)
        val addReminderButton: Button = dialogView.findViewById(R.id.addReminderButton)
        val deleteExpenseDialogButton: Button = dialogView.findViewById(R.id.deleteExpenseButton)

        val currentExpense = expenseAdapter.getItem(position)
        val expenseName = currentExpense?.name
        val expenseAmount = currentExpense?.amount
        val expenseCategoryName = currentExpense?.categoryName


        CoroutineScope(Dispatchers.IO).launch {
            if (expenseCategoryName != null) {
                val expenseCategory = categoryViewModel.getCategoryByName(expenseCategoryName)

                withContext(Dispatchers.Main) {
                    val categoryPosition = categoryAdapter.getPosition(expenseCategory)
                    categorySpinner.setSelection(categoryPosition)
                }
            }
        }

        editExpenseNameEditText.setText(expenseName)
        if (expenseAmount != null) {
            editExpenseAmountEditText.setText(expenseAmount.toString())
        }
        categorySpinner.adapter = categoryAdapter

        editExpenseNameEditText.isEnabled = false

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Edit Expense")

        val dialog = dialogBuilder.show()

        editExpenseDialogButton.setOnClickListener {
            val newExpenseName = editExpenseNameEditText.text.toString()
            val newExpenseAmount = editExpenseAmountEditText.text.toString()

            if (newExpenseName.isNotEmpty() && newExpenseAmount.isNotEmpty()) {
                if (currentExpense != null) {
                    currentExpense.name = newExpenseName
                    currentExpense.amount = newExpenseAmount.toInt()
                    expenseViewModel.updateExpense(currentExpense)
                }
                dialog.dismiss()
            }
        }

        addReminderButton.setOnClickListener {
            val appName: String = getString(R.string.app_name)

            val intent = Intent(Intent.ACTION_INSERT)
            intent.data = CalendarContract.Events.CONTENT_URI
            intent.putExtra(CalendarContract.Events.ALL_DAY, true);
            intent.putExtra(CalendarContract.Events.TITLE, expenseName)
            intent.putExtra(CalendarContract.Events.RRULE, "FREQ=MONTHLY")
            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Reminder added by ${appName}")

            startActivity(intent);
        }

        deleteExpenseDialogButton.setOnClickListener {
            // Delete the expense
            if (currentExpense != null) {
                expenseViewModel.deleteExpense(currentExpense.id)
            }
            dialog.dismiss()
        }
    }

}