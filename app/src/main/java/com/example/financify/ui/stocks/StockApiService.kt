package com.example.financify.ui.stocks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient
import io.finnhub.api.infrastructure.RequestConfig
import io.finnhub.api.models.CompanyProfile
import io.finnhub.api.models.CompanyProfile2
import io.finnhub.api.models.Quote
import io.finnhub.api.models.StockCandles
import io.finnhub.api.models.SymbolLookup
import io.finnhub.api.models.SymbolLookupInfo
import io.polygon.kotlin.sdk.rest.PolygonRestClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar


object StockApiService {
    private const val API_KEY = "clckc1hr01qk5dvqpgngclckc1hr01qk5dvqpgo0"
    private val apiClient = DefaultApi()
    var responseMutableLiveData: MutableLiveData<List<String>> = MutableLiveData()
    var stkSearchMutableLiveData: MutableLiveData<List<StockData>> = MutableLiveData()

    fun clear(){ stkSearchMutableLiveData = MutableLiveData<List<StockData>>() }

    @RequiresApi(Build.VERSION_CODES.O)
    fun searchStock(symbol: String) :  List<StockData>?{
        val currentDate = LocalDate.now()
        val currentDateString = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val sevenDaysAgo = currentDate.minusDays(7)
        val sevenDaysAgoString = sevenDaysAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val url = "https://api.polygon.io/v2/aggs/ticker/${symbol}/range/1/day/$sevenDaysAgoString/$currentDateString?adjusted=true&sort=asc&limit=120&apiKey=qG0rQIeKvTAcYX4SYvYkDXKnQRLxPBm9"

        val client = OkHttpClient()
        var res:List<StockData>? = null

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
                    println(responseBody)
                    // Parse and handle the response data
                    val symbolResponse: StkResponse? = Gson().fromJson(responseBody?.trimIndent(), StkResponse::class.java)
                    res = symbolResponse?.results
                    if (symbolResponse != null) {
                        stkSearchMutableLiveData.postValue(symbolResponse.results)
                    }

                } else {
                    // Handle unsuccessful response
                    throw IOException("Unexpected code $response")
                }
            }
        })
        return res
    }

    fun stockData(symbol: String): Quote{
        ApiClient.apiKey["token"] = API_KEY
        return apiClient.quote(symbol)
    }

    fun getStockDescription(symbol:String): CompanyProfile2 {
        ApiClient.apiKey["token"] = API_KEY
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

data class StkResponse(
    @SerializedName("results") val results: List<StockData>
)

data class StockData(
    @SerializedName("v") val volume: Long,
    @SerializedName("vw") val vw: Double,
    @SerializedName("o") val open: Double,
    @SerializedName("c") val close: Double,
    @SerializedName("h") val high: Double,
    @SerializedName("l") val low: Double,
    @SerializedName("t") val timestamp: Long,
    @SerializedName("n") val trades: Int
)

data class SymbolResponse(
    val count: Int,
    val result: List<SymbolItem>
)

data class SymbolItem(
    val symbol: String
)
