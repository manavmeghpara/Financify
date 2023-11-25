package com.example.financify.ui.stocks

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.financify.R
import com.example.financify.ui.stocks.stockDB.StockEntity
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.gson.Gson
import com.travijuu.numberpicker.library.NumberPicker
import io.finnhub.api.models.StockCandles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


class StockSearch : AppCompatActivity() {
    private lateinit var editTextSymbol: EditText
    private lateinit var buttonSearch: Button
    private lateinit var saveBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var addStock: NumberPicker
    private lateinit var candleChart: CandleStickChart
    private var stockSymbol: String? = null

    private lateinit var loadingProgressBar: ProgressBar
    companion object{
        val STOCK_TRANSFER_INTENT = "stock"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_search)

        editTextSymbol = findViewById(R.id.editTextSymbol)
        buttonSearch = findViewById(R.id.buttonSearch)
        saveBtn = findViewById(R.id.save_)
        cancelBtn = findViewById(R.id.cancel_)
        buttonSearch = findViewById(R.id.buttonSearch)

        addStock = findViewById(R.id.number_picker)
        candleChart = findViewById(R.id.stockChart)
        candleChart.isVisible = false
        loadingProgressBar = findViewById(R.id.loadingProgressBar)


        buttonSearch.setOnClickListener {
            showLoadingView()
            val symbol = editTextSymbol.text.toString().trim()
            if (symbol.isNotEmpty()) {
                // Use coroutines to fetch and visualize stock data
                GlobalScope.launch(Dispatchers.Main) {
                    val stockData = fetchStockData(symbol)
                    println("List = ${stockData}")
                    var isGraphOn = false
                    stockData?.let {
                        if(stockData.c != null){
                            updateChart(it);
                            isGraphOn = true
                            stockSymbol = symbol
                        }}
                    if(!isGraphOn) {
                        editTextSymbol.setError("Please Enter a Correct Stock Symbol!")
                        hideLoadingView()
                    }
                }
            }
            else{
                editTextSymbol.setError("Please Enter a Stock Symbol!")
                hideLoadingView()
            }
            val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(editTextSymbol.windowToken, 0)
        }

        cancelBtn.setOnClickListener{
            this.finish()
        }

        saveBtn.setOnClickListener{
            if(stockSymbol!=null) {
                var stock =  StockEntity(0,stockSymbol!!, addStock.value )
                val intent = Intent()
                val gson = Gson()
                val data = gson.toJson(stock)
                intent.putExtra(STOCK_TRANSFER_INTENT, data)
                setResult(RESULT_OK, intent)
                this.finish()
            }
        }


    }

    private suspend fun fetchStockData(symbol: String): StockCandles? {
        return withContext(Dispatchers.IO) {
            StockApiService.searchStock(symbol)
        }
    }

    private fun updateChart(candles: StockCandles) {
        hideLoadingView()
        candleChart.isVisible = true
        // Extract data from candles and update the LineChart
        // You'll need to adapt this based on the format of data returned by Finnhub API
        val entries = ArrayList<CandleEntry>()
        val dateIndex = arrayOfNulls<String>(candles.t!!.size)
        var i = 0
        while (i< candles.t!!.size){
            val e = CandleEntry(i*1f, candles.h!![i], candles.l!![i], candles.o!![i], candles.c!![i])
            entries.add(e)
            dateIndex.set(i, getDayAndMonthFromTimestamp(candles.t!![i]).toString())
            i++
        }
        candleChart.setHighlightPerDragEnabled(true)
        candleChart.setDrawBorders(true)
        candleChart.setBorderColor(Color.LTGRAY)

        val yAxis: YAxis = candleChart.getAxisLeft()
        val rightAxis: YAxis = candleChart.getAxisRight()
        yAxis.setDrawGridLines(true)
        rightAxis.setDrawGridLines(true)
        candleChart.requestDisallowInterceptTouchEvent(true)

        val xAxis: XAxis = candleChart.getXAxis()

        xAxis.setDrawGridLines(true) // disable x axis grid lines

        xAxis.setDrawLabels(true)
        rightAxis.textColor = Color.WHITE
        yAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setAvoidFirstLastClipping(true)

        val l: Legend = candleChart.getLegend()
        l.isEnabled = true

        val indexAxisValueFormatter = IndexAxisValueFormatter(dateIndex)
        xAxis.valueFormatter = indexAxisValueFormatter
        xAxis.labelCount = 4


        //System.out.println(candleValues.toString());
        val set1 = CandleDataSet(entries, "Stock Prices")
        set1.color = Color.rgb(80, 80, 80)
        set1.shadowColor = Color.GRAY
        set1.shadowWidth = 0.8f
        set1.decreasingColor = Color.RED
        set1.decreasingPaintStyle = Paint.Style.FILL
        set1.increasingColor = Color.GREEN
        set1.increasingPaintStyle = Paint.Style.FILL
        set1.neutralColor = Color.LTGRAY
        set1.setDrawValues(false)


        val lineData = CandleData(set1)

        candleChart.setData(lineData);
        candleChart.notifyDataSetChanged();
        candleChart.invalidate();
    }

    private fun showLoadingView() {
        loadingProgressBar.visibility = View.VISIBLE
        // Disable user interaction while loading, if needed
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private fun hideLoadingView() {
        loadingProgressBar.visibility = View.GONE
        // Enable user interaction after loading, if needed
        // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    fun getDayAndMonthFromTimestamp(unixTimestamp: Long): Pair<String, String> {
        val dateFormatDay = SimpleDateFormat("dd")
        val dateFormatMonth = SimpleDateFormat("MMM")
        dateFormatDay.timeZone = TimeZone.getTimeZone("UTC")
        dateFormatMonth.timeZone = TimeZone.getTimeZone("UTC")

        val date = Date(unixTimestamp * 1000L)

        val day = dateFormatDay.format(date)
        val month = dateFormatMonth.format(date)

        return Pair(day, month)
    }

}