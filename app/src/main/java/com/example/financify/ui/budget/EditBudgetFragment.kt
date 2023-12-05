package com.example.financify.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.transition.Transition
import com.example.financify.R
import com.example.financify.ui.budget.DBs.BudgetDatabase
import com.example.financify.ui.budget.DBs.CategoryAdapter
import com.example.financify.ui.budget.DBs.CategoryDao
import com.example.financify.ui.budget.DBs.CategoryRepository
import com.example.financify.ui.budget.DBs.CategoryViewModel
import com.example.financify.ui.budget.DBs.CategoryViewModelFactory

class EditBudgetFragment: Fragment() {
    private lateinit var binding: EditBudgetFragment

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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View? = layoutInflater.inflate(R.layout.fragment_edit_budget, container, false)
        return view


    }
}