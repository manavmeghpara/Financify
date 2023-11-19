package com.example.financify.ui.notifications

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.financify.R
import io.finnhub.api.models.Quote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class StockSearch : AppCompatActivity() {
    private lateinit var editTextSymbol: EditText
    private lateinit var buttonSearch: Button
    private lateinit var textStk: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_search)

        editTextSymbol = findViewById(R.id.editTextSymbol)
        buttonSearch = findViewById(R.id.buttonSearch)
        textStk = findViewById(R.id.textStock)

        buttonSearch.setOnClickListener {
            val symbol = editTextSymbol.text.toString().trim()
            if (symbol.isNotEmpty()) {
                // Use coroutines to fetch and visualize stock data
                GlobalScope.launch(Dispatchers.Main) {
                    val stockData = fetchStockData(symbol)
                    println("List = ${stockData}")
                    textStk.text = stockData.toString()

                }
            }
        }
    }

    private suspend fun fetchStockData(symbol: String): Quote {
        return withContext(Dispatchers.IO) {
            StockApiService.searchStock(symbol)
        }
    }



}