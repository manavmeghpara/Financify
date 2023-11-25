package com.example.financify.ui.dashboard

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.financify.R
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

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

        // TODO: Add/edit purchases
//        expenseListView.setOnItemClickListener { _, _, position, _ ->
//            showEditExpenseDialog(position)
//        }

//        addExpenseButton.setOnClickListener {
//            showAddExpenseDialog()
//        }

        finishButton.setOnClickListener {
            finish()
        }
    }
}