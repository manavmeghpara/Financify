package com.example.financify

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuInflater
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.financify.databinding.ActivityMainBinding
import com.example.financify.databinding.FragmentDashboardBinding
import com.example.financify.ui.budget.BudgetActivity
import com.example.financify.ui.budget.BudgetFragment
import com.example.financify.ui.budget.EditBudgetActivity
import com.example.financify.ui.budget.EditExpenses
import com.example.financify.ui.budget.EditPurchases
import com.example.financify.ui.savings.SavingActivity
import com.example.financify.ui.visualization.VisualizeActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var saving_btn: Button
    private var _binding: FragmentDashboardBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_budget, R.id.navigation_savings, R.id.navigation_stocks
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        saving_btn = findViewById(R.id.savings_btn)
        saving_btn.setOnClickListener{
            val intent = Intent(this, SavingActivity::class.java)
            startActivity(intent)
        }
        val button: Button = findViewById(R.id.launch_budget)
        button.setOnClickListener() {
            val intent = Intent(this, BudgetActivity::class.java)
            startActivity(intent)
        }
//        val textView: TextView = binding.textDashboard
//        dashboardViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
//        var expenseButton: Button = findViewById(R.id.launch_expenses)
//        expenseButton.setOnClickListener() {
//            val intent = Intent(this, EditExpenses::class.java)
//            startActivity(intent)
//        }
//        var purchaseButton: Button = findViewById(R.id.launch_purchases)
//        purchaseButton.setOnClickListener() {
//            val intent = Intent(this, EditPurchases::class.java)
//            startActivity(intent)
//        }
        var visualizationButton: Button = findViewById(R.id.launch_visualization)
        visualizationButton.setOnClickListener() {
            val intent = Intent(this, VisualizeActivity::class.java)
            startActivity(intent)
        }
        var logoutButton: Button = findViewById(R.id.logout_btn)
        logoutButton.setOnClickListener() {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logout successfully!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        //  menu pop-up button
        val menu_btn: Button = findViewById(R.id.menu_budget)
        // Initializing the popup menu and giving the reference as current context
        menu_btn.setOnClickListener(){
            val popupMenu: PopupMenu = PopupMenu(this, it, Gravity.FILL_VERTICAL)
            val inflater: MenuInflater = popupMenu.menuInflater
            inflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.show()
        }

    }
}