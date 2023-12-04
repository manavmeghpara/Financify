package com.example.financify.ui.budget.DBs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
class CategoryAdapter(
    context: Context,
    private val categories: MutableList<Category> // Pair of ID and Name
) : ArrayAdapter<Category>(context, android.R.layout.simple_list_item_1, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)

        val categoryNameTextView: TextView = view.findViewById(android.R.id.text1)

        categoryNameTextView.text = "${getItem(position)?.name}: $${getItem(position)?.amount}"

        return view
    }

    // Custom function to update the data in the adapter
    fun updateData(newCategories: List<Category>) {
        categories.clear()
        categories.addAll(newCategories)
        notifyDataSetChanged()
    }
}