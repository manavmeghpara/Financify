package com.example.financify.ui.visualization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
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
import com.example.financify.ui.budget.DBs.Purchase
import com.example.financify.ui.budget.DBs.PurchaseDao
import com.example.financify.ui.budget.DBs.PurchaseRepository
import com.example.financify.ui.budget.DBs.PurchaseViewModel
import com.example.financify.ui.budget.DBs.PurchaseViewModelFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.DecimalFormat

class VisualizeActivity : AppCompatActivity() {
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var purchaseViewModel: PurchaseViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var database: BudgetDatabase
    private lateinit var purchaseDao: PurchaseDao
    private lateinit var expenseDao: ExpenseDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var expenseRepository: ExpenseRepository
    private lateinit var purchaseRepository: PurchaseRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var expenseVmFactory: ExpenseViewModelFactory
    private lateinit var purchaseVmFactory: PurchaseViewModelFactory
    private lateinit var categoryVmFactory: CategoryViewModelFactory

    private lateinit var budgetTotal: HashMap<String, Int>
    private lateinit var pieChart : PieChart
    private lateinit var barChart: LinearLayout
    private var categories: List<Category>? = null
    private var expenses: List<Expense>? = null
    private var purchases: List<Purchase>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualize)
        pieChart = findViewById(R.id.pieChart)
        barChart = findViewById(R.id.horizontal_barchart)

        database = BudgetDatabase.getDatabase(this)
        expenseDao = database.expenseDao()
        purchaseDao = database.purchaseDao()
        categoryDao = database.categoryDao()
        expenseRepository = ExpenseRepository(expenseDao)
        purchaseRepository = PurchaseRepository(purchaseDao)
        categoryRepository = CategoryRepository(categoryDao)
        expenseVmFactory = ExpenseViewModelFactory(expenseRepository)
        purchaseVmFactory = PurchaseViewModelFactory(purchaseRepository)
        categoryVmFactory = CategoryViewModelFactory(categoryRepository)
        expenseViewModel = ViewModelProvider(this, expenseVmFactory).get(ExpenseViewModel::class.java)
        purchaseViewModel =ViewModelProvider(this, purchaseVmFactory).get(PurchaseViewModel::class.java)
        categoryViewModel =ViewModelProvider(this, categoryVmFactory).get(CategoryViewModel::class.java)

        budgetTotal = HashMap()
        categories =  categoryViewModel.allCategoriesLiveData.value
        purchases = purchaseViewModel.allPurchasesLiveData.value
        expenses = expenseViewModel.allExpensesLiveData.value


        purchaseViewModel.allPurchasesLiveData.observe(this, Observer { it ->
            purchases = it
            calculateBudget()
            initPieChart()
            initBarChart()
        })

        expenseViewModel.allExpensesLiveData.observe(this, Observer { it ->
            expenses = it
            calculateBudget()
            initPieChart()
            initBarChart()
        })
        categoryViewModel.allCategoriesLiveData.observe(this, Observer { it ->
            categories = it
            initBarChart()
        })

    }

    fun calculateBudget(){
        budgetTotal.clear()
        if(purchases != null)
            for(p in purchases!!)
                budgetTotal.put(p.categoryName, budgetTotal.getOrDefault(p.categoryName, 0)+ p.amount)

        if(expenses != null)
            for(e in expenses!!)
                budgetTotal.put(e.categoryName, budgetTotal.getOrDefault(e.categoryName, 0)+ e.amount)

        println("Budget :: ${budgetTotal}")
    }

    fun initPieChart(){
        val entries = ArrayList<PieEntry>()
        var total = 0
        for ((category, amount) in budgetTotal) {
            entries.add(PieEntry(amount.toFloat(), category))
            total += amount
        }
        // Create a PieDataSet
        val dataSet = PieDataSet(entries, "Budget Distribution")

        // Set colors using ColorTemplate from MPAndroidChart
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
        dataSet.valueTextSize = 15f
        dataSet.setAutomaticallyDisableSliceSpacing(true)
        // Create a PieData object
        val data = PieData(dataSet)
        // Set the data to the chart
        pieChart.data = data
        data.setValueFormatter(PercentFormatter(pieChart))

        // Customize the chart as needed
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = true
        pieChart.centerText = "Budget Total $$total"
        pieChart.setEntryLabelColor(android.R.color.black)
        pieChart.setEntryLabelTextSize(20f)

        //enabling the user to rotate the chart, default true
        pieChart.setRotationEnabled(true);
        //adding friction when rotating the pie chart
        pieChart.setDragDecelerationFrictionCoef(0.9f)
        //setting the first entry start from right hand side, default starting from top
        pieChart.setRotationAngle(0F)

        //highlight the entry when it is tapped, default true if not set
        pieChart.setHighlightPerTapEnabled(true)
        //adding animation so the entries pop up from 0 degree
        pieChart.animateY(1400, Easing.EaseInOutQuad);

        // Refresh the chart
        pieChart.invalidate()
    }

    fun initBarChart(){
        barChart.removeAllViews()
        if(categories!=null && budgetTotal.isNotEmpty()) {
            for (c in categories!!) {
                var frame = LayoutInflater.from(this).inflate(R.layout.progress_budget, null)
                var content = frame.findViewById<TextView>(R.id.content)
                var bar = frame.findViewById<ProgressBar>(R.id.bar)
                var textView = frame.findViewById<TextView>(R.id.barContent)

                content.text = c.name

                val percent = DecimalFormat("#.##").format(
                    (budgetTotal.getOrDefault(
                        c.name,
                        0
                    ).toDouble() / c.amount.toDouble()) * 100
                )
                println("category::: ${(Math.round(percent.toDouble())).toInt()}")

                textView.text = "${percent} %"
                bar.progress = (Math.round(percent.toDouble())).toInt()

                barChart.addView(frame)
            }
        }
    }
}