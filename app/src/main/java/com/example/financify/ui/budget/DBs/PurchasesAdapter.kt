package com.example.financify.ui.budget.DBs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.financify.R
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Locale

class PurchasesAdapter(
    context: Context,
    private val purchases: MutableList<Purchase>
) : ArrayAdapter<Purchase>(context, android.R.layout.simple_list_item_2, purchases) {

    private val colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
    private lateinit var categoryColorMap: Map<String, Int>
    init {
        initializeColorMap()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.listview_purchase, parent, false)

        val text1: TextView = view.findViewById(android.R.id.text1)
        val text2: TextView = view.findViewById(android.R.id.text2)
        val amountText: TextView = view.findViewById(R.id.amountTextView)
        val dateText: TextView = view.findViewById(R.id.dateTextView)

        val purchase = getItem(position)

        val date = purchase?.dateTime
        val dateFormat = SimpleDateFormat("MMM. d", Locale.getDefault())
        var formattedDate = ""
        if (date != null) {
            formattedDate = dateFormat.format(date.time)
        }

        text1.text = "${purchase?.name}"
        amountText.text = "$${purchase?.amount}"
        text2.text = "${purchase?.categoryName}"
        dateText.text = "${formattedDate}"

        // Set color based on category
        if (purchase != null) {
            categoryColorMap[purchase.categoryName]?.let { text2.setTextColor(it) }
        }


        return view
    }

    // Custom function to update the data in the adapter
    fun updateData(newPurchases: List<Purchase>) {
        purchases.clear()
        purchases.addAll(newPurchases)
        initializeColorMap()
        notifyDataSetChanged()
    }

    fun initializeColorMap() {
        // Sort categories by name
        val sortedCategories = purchases.map {it.categoryName}.distinct().sorted()

        // Create a map from categoryName -> Color
        categoryColorMap = sortedCategories.zip(colors).toMap()
    }
}