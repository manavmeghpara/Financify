package com.example.financify.ui.budget

import android.app.DatePickerDialog
import android.icu.util.Calendar
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
import com.example.financify.ui.budget.DBs.BudgetDatabase
import com.example.financify.ui.budget.DBs.Category
import com.example.financify.ui.budget.DBs.CategoryDao
import com.example.financify.ui.budget.DBs.CategoryRepository
import com.example.financify.ui.budget.DBs.CategoryViewModel
import com.example.financify.ui.budget.DBs.CategoryViewModelFactory
import com.example.financify.ui.budget.DBs.Purchase
import com.example.financify.ui.budget.DBs.PurchaseDao
import com.example.financify.ui.budget.DBs.PurchaseRepository
import com.example.financify.ui.budget.DBs.PurchaseViewModel
import com.example.financify.ui.budget.DBs.PurchaseViewModelFactory
import com.example.financify.ui.budget.DBs.PurchasesAdapter

class EditPurchases : AppCompatActivity() {
    private lateinit var purchaseListView: ListView
    private lateinit var addPurchaseButton: Button
    private lateinit var finishButton: Button
    private lateinit var purchaseAdapter: PurchasesAdapter

    private lateinit var database: BudgetDatabase

    private lateinit var categoryDao: CategoryDao
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var categoryViewModelFactory: CategoryViewModelFactory
    private lateinit var categoryViewModel: CategoryViewModel

    private lateinit var purchaseDao: PurchaseDao
    private lateinit var purchaseRepository: PurchaseRepository
    private lateinit var purchaseViewModelFactory: PurchaseViewModelFactory
    private lateinit var purchaseViewModel: PurchaseViewModel

    private lateinit var categoryAdapter: ArrayAdapter<Category>

    private lateinit var dateTime: Calendar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_purchases)

        purchaseListView = findViewById(R.id.purchaseListView)
        addPurchaseButton = findViewById(R.id.addPurchaseButton)
        finishButton = findViewById(R.id.finishButton)

        database = BudgetDatabase.getDatabase(this)

        categoryDao = database.categoryDao()
        categoryRepository = CategoryRepository(categoryDao)
        categoryViewModelFactory = CategoryViewModelFactory(categoryRepository)
        categoryViewModel = ViewModelProvider(this, categoryViewModelFactory)[CategoryViewModel::class.java]

        purchaseDao = database.purchaseDao()
        purchaseRepository = PurchaseRepository(purchaseDao)
        purchaseViewModelFactory = PurchaseViewModelFactory(purchaseRepository)
        purchaseViewModel = ViewModelProvider(this, purchaseViewModelFactory)[PurchaseViewModel::class.java]

        // Initialize empty adapter while waiting for DB
        purchaseAdapter = PurchasesAdapter(this, mutableListOf())
        purchaseViewModel.allPurchasesLiveData.observe(this, Observer { purchases ->
            purchaseAdapter.updateData(purchases)
        })
        purchaseListView.adapter = purchaseAdapter

        // Set up the spinner adapter for dialogs
        categoryAdapter = ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoryViewModel.allCategoriesLiveData.observe(this) { categories ->
            categoryAdapter.clear()
            categoryAdapter.addAll(categories)
            categoryAdapter.notifyDataSetChanged()
        }

        purchaseListView.setOnItemClickListener { _, _, position, _ ->
            showDeletePurchaseDialog(position)
        }

        addPurchaseButton.setOnClickListener {
            showAddPurchaseDialog()
        }

        finishButton.setOnClickListener {
            finish()
        }
    }

    private fun showAddPurchaseDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_purchase, null)
        val categorySpinner: Spinner = dialogView.findViewById(R.id.purchaseCategorySpinner)
        val editPurchaseNameEditText: EditText = dialogView.findViewById(R.id.editPurchaseNameEditText)
        val editPurchaseAmountEditText: EditText = dialogView.findViewById(R.id.editPurchaseAmountEditText)
        val editPurchaseDateButton: Button = dialogView.findViewById(R.id.editPurchaseDateButton)
        val editPurchaseButton: Button = dialogView.findViewById(R.id.editPurchaseButton)
        val deletePurchaseButton: Button = dialogView.findViewById(R.id.deletePurchaseButton)
        deletePurchaseButton.visibility = View.GONE

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Add New Purchase")
        val dialog = dialogBuilder.show()

        categorySpinner.adapter = categoryAdapter

        dateTime = Calendar.getInstance()
        editPurchaseDateButton.setOnClickListener {
            openDatePickerDialog()
        }

        editPurchaseButton.setOnClickListener {
            var purchaseName = editPurchaseNameEditText.text.toString()
            val purchaseAmount = editPurchaseAmountEditText.text.toString()
            val purchaseCategory = categorySpinner.selectedItem.toString()

            // Validate input and add the purchase
            if (purchaseAmount.isNotEmpty()) {
                if (purchaseName.isEmpty()) {
                    purchaseName = "Unnamed purchase"
                }
                val newPurchase = Purchase(categoryName = purchaseCategory, name = purchaseName, amount = purchaseAmount.toInt(), dateTime = dateTime)
                purchaseViewModel.insertPurchase(newPurchase)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Name and amount are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeletePurchaseDialog(position: Int) {
        val currentPurchase = purchaseAdapter.getItem(position)

        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete the purchase '${currentPurchase?.name}' ?")
            .setPositiveButton("Delete") { _, _ ->
                if (currentPurchase != null) {
                    purchaseViewModel.deletePurchase(currentPurchase.id)
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
            }
            .show()
    }

    private fun openDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                dateTime.set(selectedYear, selectedMonth, selectedDay)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.setTitle("Select Date")
        datePickerDialog.show()
    }
}