package com.example.financify.ui.notifications

import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient
import io.finnhub.api.models.Quote


object StockApiService {
    private const val API_KEY = "clckc1hr01qk5dvqpgngclckc1hr01qk5dvqpgo0"
    private val apiClient = DefaultApi()

    fun searchStock(symbol: String): Quote {
        ApiClient.apiKey["token"] = API_KEY
        return apiClient.quote(symbol)
    }
}
