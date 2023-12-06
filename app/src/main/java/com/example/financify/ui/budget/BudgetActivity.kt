package com.example.financify.ui.budget

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.financify.R
import com.example.financify.ui.budget.DBs.BudgetDatabase
import com.example.financify.ui.budget.DBs.Category
import com.example.financify.ui.budget.DBs.CategoryAdapter
import com.example.financify.ui.budget.DBs.CategoryDao
import com.example.financify.ui.budget.DBs.CategoryRepository
import com.example.financify.ui.budget.DBs.CategoryViewModel
import com.example.financify.ui.budget.DBs.CategoryViewModelFactory
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialElevationScale

class BudgetActivity: AppCompatActivity() {

    private lateinit var budgetListView: ListView
    private lateinit var addCategoryButton: FloatingActionButton
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var database: BudgetDatabase
    private lateinit var databaseDao: CategoryDao
    private lateinit var repository: CategoryRepository
    private lateinit var viewModelFactory: CategoryViewModelFactory
    private lateinit var categoryViewModel: CategoryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        val expenseButton: Button = findViewById(R.id.launch_expenses)
        expenseButton.setOnClickListener() {
            val intent = Intent(this, EditExpenses::class.java)
            startActivity(intent)
            findViewById<MaterialButtonToggleGroup>(R.id.toggleButton).clearChecked()
        }
        val purchaseButton: Button = findViewById(R.id.launch_purchases)
        purchaseButton.setOnClickListener() {
            val intent = Intent(this, EditPurchases::class.java)
            startActivity(intent)
            findViewById<MaterialButtonToggleGroup>(R.id.toggleButton).clearChecked()
        }

        budgetListView = findViewById(R.id.budgetListView)
        addCategoryButton = findViewById(R.id.addCategoryFAB)

        database = BudgetDatabase.getDatabase(this)
        databaseDao = database.categoryDao()
        repository = CategoryRepository(databaseDao)
        viewModelFactory = CategoryViewModelFactory(repository)
        categoryViewModel = ViewModelProvider(this, viewModelFactory)[CategoryViewModel::class.java]

        // Initialize empty adapter while waiting for
        categoryAdapter = CategoryAdapter(this, mutableListOf())
        categoryViewModel.allCategoriesLiveData.observe(this, Observer { categories ->
            categoryAdapter.updateData(categories)
        })
        budgetListView.adapter = categoryAdapter

        addCategoryButton.setOnClickListener() {
            supportFragmentManager.beginTransaction()
                .add(com.example.financify.R.id.fragEditBudget, EditBudgetFragment()).commit()

//            this.apply {
//                var exitTransition = MaterialElevationScale(false).apply {
//                    duration = resources.getInteger(com.example.financify.R.integer.reply_motion_duration_large).toLong()
//                }
//                var reenterTransition = MaterialElevationScale(true).apply {
//                    duration = resources.getInteger(com.example.financify.R.integer.reply_motion_duration_large).toLong()
//                }
//            }
        }

        budgetListView.setOnItemClickListener { _, _, position, _ ->
            showEditCategoryDialog(position)
        }



    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_budget_category, null)
        val editCategoryAutoCompleteTextView: AutoCompleteTextView = dialogView.findViewById(R.id.editCategoryNameEditText)
        val editCategoryAmountEditText: EditText = dialogView.findViewById(R.id.editCategoryAmountEditText)
        val editCategoryDialogButton: Button = dialogView.findViewById(R.id.editCategoryDialogButton)
        val deleteCategoryDialogButton: Button = dialogView.findViewById(R.id.deleteCategoryDialogButton)
        deleteCategoryDialogButton.visibility = View.GONE

        val autoCompleteOptions = arrayOf("Rent", "Transportation", "Food", "Utilities", "Insurance", "Saving", "Entertainment", "Other")
        val autoCompleteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, autoCompleteOptions)
        editCategoryAutoCompleteTextView.setAdapter(autoCompleteAdapter)
        editCategoryAutoCompleteTextView.threshold = 1

        editCategoryAutoCompleteTextView.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                // Show dropdown when the view gains focus
                editCategoryAutoCompleteTextView.showDropDown()
            }
        })
        editCategoryAutoCompleteTextView.dropDownHeight = 500

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Add New Budget Category")

        val dialog = dialogBuilder.show()


        editCategoryDialogButton.setOnClickListener {
            val categoryName = editCategoryAutoCompleteTextView.text.toString()

            val categoryAmount = editCategoryAmountEditText.text.toString()

            // Validate input and add the category
            if (categoryName.isNotEmpty() && categoryAmount.isNotEmpty()) {
                // TODO: Check if unique name
                if (true) {
                    val newCategory = Category(name=categoryName, amount=categoryAmount.toInt())
                    categoryViewModel.insertCategory(newCategory)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Category name must be unique.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Category name and amount are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditCategoryDialog(position: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_budget_category, null)
        val editCategoryNameEditText: EditText = dialogView.findViewById(R.id.editCategoryNameEditText)
        val editCategoryAmountEditText: EditText = dialogView.findViewById(R.id.editCategoryAmountEditText)
        val editCategoryDialogButton: Button = dialogView.findViewById(R.id.editCategoryDialogButton)
        val deleteCategoryDialogButton: Button = dialogView.findViewById(R.id.deleteCategoryDialogButton)

        // Extract current category details
        val currentCategory = categoryAdapter.getItem(position)
        val categoryName = currentCategory?.name
        val categoryAmount = currentCategory?.amount

        // Populate dialog fields with current category details
        editCategoryNameEditText.setText(categoryName)
        if (categoryAmount != null) {
            editCategoryAmountEditText.setText(categoryAmount.toString())
        }

        editCategoryNameEditText.isEnabled = false

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Edit Category")

        val dialog = dialogBuilder.show()

        editCategoryDialogButton.setOnClickListener {
            // Validate input and update the category
            val newCategoryName = editCategoryNameEditText.text.toString()
            val newCategoryAmount = editCategoryAmountEditText.text.toString()

            if (newCategoryName.isNotEmpty() && newCategoryAmount.isNotEmpty()) {
                if (currentCategory != null) {
                    currentCategory.name = newCategoryName
                    currentCategory.amount = newCategoryAmount.toInt()
                    categoryViewModel.updateCategory(currentCategory)
                }
                dialog.dismiss()
            }
        }

        deleteCategoryDialogButton.setOnClickListener {
            // Delete the category
            if (currentCategory != null) {
                categoryViewModel.deleteCategory(currentCategory.name)
            }
            dialog.dismiss()
        }
    }
}