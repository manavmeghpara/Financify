package com.example.financify.ui.budget.DBs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.financify.R
import com.github.mikephil.charting.utils.ColorTemplate

class ExpensesAdapter(
    context: Context,
    private val expenses: MutableList<Expense>
) : ArrayAdapter<Expense>(context, android.R.layout.simple_list_item_2, expenses) {

    private val colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
    private lateinit var categoryColorMap: Map<String, Int>
    init {
        initializeColorMap()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.listview_expense, parent, false)

        val text1: TextView = view.findViewById(android.R.id.text1)
        val text2: TextView = view.findViewById(android.R.id.text2)

        val expense = getItem(position)

        text1.text = "${expense?.name}: $${expense?.amount}"
        text2.text = "${expense?.categoryName}"

        // Set color based on category
        if (expense != null) {
            categoryColorMap[expense.categoryName]?.let { text2.setTextColor(it) }
        }

        return view
    }

    // Custom function to update the data in the adapter
    fun updateData(newExpenses: List<Expense>) {
        expenses.clear()
        expenses.addAll(newExpenses)
        initializeColorMap()
        notifyDataSetChanged()
    }
    fun initializeColorMap() {
        // Sort categories by name
        val sortedCategories = expenses.map {it.categoryName}.distinct().sorted()

        // Create a map from categoryName -> Color
        categoryColorMap = sortedCategories.zip(colors).toMap()
    }
}