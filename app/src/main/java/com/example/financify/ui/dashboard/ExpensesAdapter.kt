package com.example.financify.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
class ExpensesAdapter(
    context: Context,
    private val categories: MutableList<Expense>
) : ArrayAdapter<Expense>(context, android.R.layout.simple_list_item_2, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)

        val categoryNameTextView: TextView = view.findViewById(android.R.id.text1)

        categoryNameTextView.text = "${getItem(position)?.name}: $${getItem(position)?.amount}"

        return view
    }

    // Custom function to update the data in the adapter
    fun updateData(newExpenses: List<Expense>) {
        categories.clear()
        categories.addAll(newExpenses)
        notifyDataSetChanged()
    }
}