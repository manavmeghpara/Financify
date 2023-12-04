package com.example.financify.ui.budget

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.financify.R
import com.example.financify.databinding.FragmentBudgetBinding
import com.example.financify.ui.budget.DBs.BudgetDatabase
import com.example.financify.ui.budget.DBs.CategoryAdapter
import com.example.financify.ui.budget.DBs.CategoryDao
import com.example.financify.ui.budget.DBs.CategoryRepository
import com.example.financify.ui.budget.DBs.CategoryViewModel
import com.example.financify.ui.budget.DBs.CategoryViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialElevationScale

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var budgetListView: ListView
    private lateinit var addCategoryButton: FloatingActionButton
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var database: BudgetDatabase
    private lateinit var databaseDao: CategoryDao
    private lateinit var repository: CategoryRepository
    private lateinit var viewModelFactory: CategoryViewModelFactory
    private lateinit var categoryViewModel: CategoryViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val budgetViewModel =
            ViewModelProvider(this)[BudgetViewModel::class.java]

        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textBudget
        budgetViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val expenseButton: Button = root.findViewById(R.id.launch_expenses)
        expenseButton.setOnClickListener() {
            val intent = Intent(requireActivity(), EditExpenses::class.java)
            startActivity(intent)
        }
        val purchaseButton: Button = root.findViewById(R.id.launch_purchases)
        purchaseButton.setOnClickListener() {
            val intent = Intent(requireActivity(), EditPurchases::class.java)
            startActivity(intent)
        }


        budgetListView = root.findViewById(R.id.budgetListView)
        addCategoryButton = root.findViewById(R.id.addCategoryFAB)

        database = BudgetDatabase.getDatabase(requireContext())
        databaseDao = database.categoryDao()
        repository = CategoryRepository(databaseDao)
        viewModelFactory = CategoryViewModelFactory(repository)
        categoryViewModel = ViewModelProvider(this, viewModelFactory)[CategoryViewModel::class.java]

        // Initialize empty adapter while waiting for
        categoryAdapter = CategoryAdapter(requireContext(), mutableListOf())
        categoryViewModel.allCategoriesLiveData.observe(requireActivity(), Observer { categories ->
            categoryAdapter.updateData(categories)
        })
        budgetListView.adapter = categoryAdapter

        addCategoryButton.setOnClickListener(){
            this.apply {
                exitTransition = MaterialElevationScale(false).apply {
                    duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
                }
                reenterTransition = MaterialElevationScale(true).apply {
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


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}