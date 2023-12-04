package com.example.financify.ui.budget.DBs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.financify.R

class ExpensesAdapter(
    context: Context,
    private val categories: MutableList<Expense>
) : ArrayAdapter<Expense>(context, android.R.layout.simple_list_item_2, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.listview_expense, parent, false)

        val text1: TextView = view.findViewById(android.R.id.text1)
        val text2: TextView = view.findViewById(android.R.id.text2)

        text1.text = "${getItem(position)?.name}: $${getItem(position)?.amount}"
        text2.text = "${getItem(position)?.categoryName}"

        return view
    }

    // Custom function to update the data in the adapter
    fun updateData(newExpenses: List<Expense>) {
        categories.clear()
        categories.addAll(newExpenses)
        notifyDataSetChanged()
    }
}