package com.example.financify.ui.budget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.infiniteRepeatable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.financify.R
import com.example.financify.databinding.FragmentBudgetBinding
import com.example.financify.ui.budget.DBs.BudgetDatabase
import com.example.financify.ui.budget.DBs.CategoryAdapter
import com.example.financify.ui.budget.DBs.CategoryDao
import com.example.financify.ui.budget.DBs.CategoryRepository
import com.example.financify.ui.budget.DBs.CategoryViewModel
import com.example.financify.ui.budget.DBs.CategoryViewModelFactory
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
        layoutInflater.inflate(R.id.activity_budget)
        val budgetViewModel =
            ViewModelProvider(this)[BudgetViewModel::class.java]

        val expenseButton: Button = findViewById(R.id.launch_expenses)
        expenseButton.setOnClickListener() {
            val intent = Intent(this, EditExpenses::class.java)
            startActivity(intent)
        }
        val purchaseButton: Button = findViewById(R.id.launch_purchases)
        purchaseButton.setOnClickListener() {
            val intent = Intent(this, EditPurchases::class.java)
            startActivity(intent)
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
            this.apply {
                var exitTransition = MaterialElevationScale(false).apply {
                    duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
                }
                var reenterTransition = MaterialElevationScale(true).apply {
                    duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
                }
            }
        }

//        budgetListView.setOnItemClickListener { _, _, position, _ ->
//            showEditCategoryDialog(position)
//        }
//
//        addCategoryButton.setOnClickListener {
//            showAddCategoryDialog()
//        }


    }
}