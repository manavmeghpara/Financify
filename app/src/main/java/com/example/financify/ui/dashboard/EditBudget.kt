package com.example.financify.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.GONE
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.financify.R
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider


class EditBudget : AppCompatActivity() {

    private lateinit var budgetListView: ListView
    private lateinit var addCategoryButton: Button
    private lateinit var saveBudgetButton: Button
    private lateinit var cancelBudgetButton: Button
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var database: BudgetDatabase
    private lateinit var databaseDao: CategoryDao
    private lateinit var repository: CategoryRepository
    private lateinit var viewModelFactory: CategoryViewModelFactory
    private lateinit var categoryViewModel: CategoryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_budget)

        budgetListView = findViewById(R.id.budgetListView)
        addCategoryButton = findViewById(R.id.addCategoryButton)
        saveBudgetButton = findViewById(R.id.saveBudgetButton)
        cancelBudgetButton = findViewById(R.id.cancelBudgetButton)

        database = BudgetDatabase.getDatabase(this)
        databaseDao = database.categoryDao()
        repository = CategoryRepository(databaseDao)
        viewModelFactory = CategoryViewModelFactory(repository)
        categoryViewModel = ViewModelProvider(this, viewModelFactory)[CategoryViewModel::class.java]

        categoryAdapter = CategoryAdapter(this, mutableListOf())

        // Observe the LiveData from the ViewModel
        categoryViewModel.allCategoriesLiveData.observe(this, Observer { categories ->
            categoryAdapter.updateData(categories)
        })

        budgetListView.adapter = categoryAdapter

        budgetListView.setOnItemClickListener { _, _, position, _ ->
            showEditCategoryDialog(position)
        }

        addCategoryButton.setOnClickListener {
            showAddCategoryDialog()
        }

        saveBudgetButton.setOnClickListener {
            // TODO: Save budget to database
            finish()
        }

        cancelBudgetButton.setOnClickListener {
            finish()
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_budget_category, null)
        val editCategoryNameEditText: EditText = dialogView.findViewById(R.id.editCategoryNameEditText)
        val editCategoryAmountEditText: EditText = dialogView.findViewById(R.id.editCategoryAmountEditText)
        val editCategoryDialogButton: Button = dialogView.findViewById(R.id.editCategoryDialogButton)
        val deleteCategoryDialogButton: Button = dialogView.findViewById(R.id.deleteCategoryDialogButton)
        deleteCategoryDialogButton.visibility = GONE

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Add New Budget Category")

        val dialog = dialogBuilder.show()

        editCategoryDialogButton.setOnClickListener {
            val categoryName = editCategoryNameEditText.text.toString()
            val categoryAmount = editCategoryAmountEditText.text.toString()

            // Validate input and add the category
            if (categoryName.isNotEmpty() && categoryAmount.isNotEmpty()) {
                // Check if the category name is unique before adding
                // TODO: Check if unique name
                if (true) {
                    val newCategory = Category(name=categoryName, amount=categoryAmount.toInt())
                    categoryViewModel.insertCategory(newCategory)
                    dialog.dismiss()
                } else {
                    // Show an error message for non-unique category name
                    Toast.makeText(this, "Category name must be unique.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Show an error message for empty inputs
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
            categoryAdapter.remove(currentCategory)
            categoryAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
    }

    private fun parseCategory(category: String?): Pair<String, String> {
        val parts = category?.split(": $") ?: emptyList()
        return if (parts.size == 2) {
            Pair(parts[0], parts[1])
        } else {
            Pair("", "")
        }
    }
}