package com.example.financify.ui.stocks

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.financify.R
import com.example.financify.ui.stocks.stockDB.StockEntity

class StockAdapter(private val context: Context, private val stocks: List<StockEntity>) : BaseAdapter() {

    override fun getCount(): Int = stocks.size

    override fun getItem(position: Int): StockEntity = stocks[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = View.inflate(context, R.layout.stock_adaptor_view, null)
        var symbol = itemView.findViewById<TextView>(R.id.textViewSymbol)
        var value =  itemView.findViewById<TextView>(R.id.textViewAmount)


        val stock = stocks.get(position)
        symbol.text = stock.symbol
        value.text = "Shares: ${stock.shares}"

        return itemView!!
    }
}