package com.example.financify.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.GONE
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Transition
import com.example.financify.R
import com.example.financify.ui.budget.DBs.BudgetDatabase
import com.example.financify.ui.budget.DBs.Category
import com.example.financify.ui.budget.DBs.CategoryAdapter
import com.example.financify.ui.budget.DBs.CategoryDao
import com.example.financify.ui.budget.DBs.CategoryRepository
import com.example.financify.ui.budget.DBs.CategoryViewModel
import com.example.financify.ui.budget.DBs.CategoryViewModelFactory


class EditBudgetActivity : AppCompatActivity() {
    private lateinit var budgetListView: ListView
    private lateinit var addCategoryButton: Button
    private lateinit var saveBudgetButton: Button
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var database: BudgetDatabase
    private lateinit var databaseDao: CategoryDao
    private lateinit var repository: CategoryRepository
    private lateinit var viewModelFactory: CategoryViewModelFactory
    private lateinit var categoryViewModel: CategoryViewModel

    private lateinit var enterTransition: Transition
    private lateinit var returnTransition: Transition
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_category)
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