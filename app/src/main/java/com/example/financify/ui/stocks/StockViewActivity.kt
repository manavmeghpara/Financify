package com.example.financify.ui.stocks

import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.financify.R
import com.example.financify.ui.stocks.stockDB.StockDao
import com.example.financify.ui.stocks.stockDB.StockDatabase
import com.example.financify.ui.stocks.stockDB.StockRepository
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import io.finnhub.api.models.Quote
import io.finnhub.api.models.StockCandles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class StockViewActivity : AppCompatActivity() {
    private lateinit var candleChart: CandleStickChart
    private lateinit var openTextView: TextView
    private lateinit var highTextView: TextView
    private lateinit var lowTextView: TextView
    private lateinit var currTextView: TextView
    private lateinit var prevCloseTextView: TextView
    private lateinit var changeTextView: TextView
    private lateinit var percentTextView: TextView

    private var stockSymbol: String? = null
    private lateinit var loadingProgressBar: ProgressBar

    private lateinit var stocksViewModel: StocksViewModel

    private lateinit var database: StockDatabase
    private lateinit var dbDao: StockDao
    private lateinit var repository: StockRepository
    private lateinit var vmFactory: StocksViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_view)
        candleChart = findViewById(R.id.stockGraph)
        openTextView = findViewById(R.id.textViewOpenPrice)
        highTextView = findViewById(R.id.textViewHighPrice)
        lowTextView = findViewById(R.id.textViewLowPrice)
        currTextView = findViewById(R.id.textViewCurrentPrice)
        prevCloseTextView = findViewById(R.id.textViewPreviousClosePrice)
        changeTextView = findViewById(R.id.textViewChange)
        percentTextView = findViewById(R.id.textViewPercentChange)

        loadingProgressBar = findViewById(R.id.loading)

        candleChart.isVisible = false
        database =StockDatabase.getInstance(this)
        dbDao =database.stockDatabaseDao
        repository = StockRepository(dbDao)
        vmFactory = StocksViewModelFactory(repository)
        stocksViewModel = ViewModelProvider(this, vmFactory).get(StocksViewModel::class.java)

        stockSymbol = intent?.getStringExtra(StocksFragment.STOCK_VIEW_KEY)
        if (stockSymbol != null) {
            // Use coroutines to fetch and visualize stock data
            GlobalScope.launch(Dispatchers.Main) {
                val stockData = fetchStockData(stockSymbol!!)
                println("List = ${stockData}")
                stockData?.let {
                    if(stockData.c != null){
                        updateChart(it);
                    }}
            }
            GlobalScope.launch(Dispatchers.Main) {
                val stockData = fetchData(stockSymbol!!)
                println("List = ${stockData}")
                stockData?.let {
                    openTextView.text ="Open Price: "+ stockData.o.toString()
                    highTextView.text ="High Price: "+  stockData.h.toString()
                    lowTextView.text ="Low Price: "+  stockData.l.toString()
                    currTextView.text ="Current Price: "+  stockData.c.toString()
                    prevCloseTextView.text ="Previous Closing Price: "+  stockData.pc.toString()
                    changeTextView.text ="Change: "+  stockData.d.toString()
                    percentTextView.text ="Percent Change: "+  stockData.dp.toString()

                }
            }

        }
    }

    private suspend fun fetchStockData(symbol: String): StockCandles? {
        return withContext(Dispatchers.IO) {
            StockApiService.searchStock(symbol)
        }
    }

    private suspend fun fetchData(symbol: String): Quote? {
        return withContext(Dispatchers.IO) {
            StockApiService.stockData(symbol)
        }
    }

    private fun updateChart(candles: StockCandles) {
        loadingProgressBar.visibility = View.GONE
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.delete, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_item1 -> {
                stocksViewModel.delete(stockSymbol!!)
                Toast.makeText(this, "Stock deleted!", Toast.LENGTH_LONG).show()
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}