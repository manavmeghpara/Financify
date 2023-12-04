package com.example.financify.ui.budget.DBs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.financify.R
import java.text.SimpleDateFormat
import java.util.Locale

class PurchasesAdapter(
    context: Context,
    private val categories: MutableList<Purchase>
) : ArrayAdapter<Purchase>(context, android.R.layout.simple_list_item_2, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.listview_purchase, parent, false)

        val text1: TextView = view.findViewById(android.R.id.text1)
        val text2: TextView = view.findViewById(android.R.id.text2)
        val amountText: TextView = view.findViewById(R.id.amountTextView)
        val dateText: TextView = view.findViewById(R.id.dateTextView)


        val date = getItem(position)?.dateTime
        val dateFormat = SimpleDateFormat("MMM. d", Locale.getDefault())
        var formattedDate = ""
        if (date != null) {
            formattedDate = dateFormat.format(date.time)
        }

        text1.text = "${getItem(position)?.name}"
        amountText.text = "$${getItem(position)?.amount}"
        text2.text = "${getItem(position)?.categoryName}"
        dateText.text = "${formattedDate}"

        return view
    }

    // Custom function to update the data in the adapter
    fun updateData(newPurchases: List<Purchase>) {
        categories.clear()
        categories.addAll(newPurchases)
        notifyDataSetChanged()
    }
}