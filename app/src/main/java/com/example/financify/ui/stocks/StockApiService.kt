package com.example.financify.ui.stocks

import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient
import io.finnhub.api.models.CompanyProfile
import io.finnhub.api.models.CompanyProfile2
import io.finnhub.api.models.Quote
import io.finnhub.api.models.StockCandles
import java.util.Calendar


object StockApiService {
    private const val API_KEY = "clckc1hr01qk5dvqpgngclckc1hr01qk5dvqpgo0"
    private val apiClient = DefaultApi()

    fun searchStock(symbol: String): StockCandles? {
        ApiClient.apiKey["token"] = API_KEY
        val currentTimeMillis = System.currentTimeMillis()

        // Unix timestamp for the current time
        val currentUnixTimestamp = currentTimeMillis / 1000

        // Unix timestamp for 7 days ago
        val sevenDaysAgoUnixTimestamp = (currentTimeMillis - 7 * 24 * 60 * 60 * 1000) / 1000
        return apiClient.stockCandles(symbol, "D", 	sevenDaysAgoUnixTimestamp, currentUnixTimestamp )
    }

    fun stockData(symbol: String): Quote{
        return apiClient.quote(symbol)
    }

    fun getStockDescription(symbol:String): CompanyProfile2 {
        return apiClient.companyProfile2(symbol, null, null)
    }
}
