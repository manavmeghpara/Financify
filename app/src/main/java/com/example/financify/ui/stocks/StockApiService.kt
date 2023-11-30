package com.example.financify.ui.stocks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient
import io.finnhub.api.infrastructure.RequestConfig
import io.finnhub.api.models.CompanyProfile
import io.finnhub.api.models.CompanyProfile2
import io.finnhub.api.models.Quote
import io.finnhub.api.models.StockCandles
import io.finnhub.api.models.SymbolLookup
import io.finnhub.api.models.SymbolLookupInfo
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.util.Calendar


object StockApiService {
    private const val API_KEY = "clckc1hr01qk5dvqpgngclckc1hr01qk5dvqpgo0"
    private val apiClient = DefaultApi()
    var responseMutableLiveData: MutableLiveData<List<String>> = MutableLiveData()

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

    fun getAllRelatedStocks(symbol: String): List<String>?{
        val url = "https://finnhub.io/api/v1/search?q=$symbol&token=$API_KEY"

        val client = OkHttpClient()
        var res:List<String>? = null

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    // Parse and handle the response data
                    val symbolResponse: SymbolResponse = Gson().fromJson(responseBody?.trimIndent(), SymbolResponse::class.java)

                    res = symbolResponse.result.map { it.symbol + it }
                    responseMutableLiveData.postValue(symbolResponse.result.map { it.symbol })
                } else {
                    // Handle unsuccessful response
                    throw IOException("Unexpected code $response")
                }
            }
        })
        return res
    }
}


data class SymbolResponse(
    val count: Int,
    val result: List<SymbolItem>
)

data class SymbolItem(
    val symbol: String
)
