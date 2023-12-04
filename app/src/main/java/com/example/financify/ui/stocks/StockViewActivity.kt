package com.example.financify.ui.stocks

import android.graphics.Paint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.allViews
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
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
import io.finnhub.api.models.CompanyProfile2
import io.finnhub.api.models.Quote
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(this.supportActionBar!=null)
            this.supportActionBar!!.hide();
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

        StockApiService.stkSearchMutableLiveData.observe(this, Observer { it ->
            updateChart(it);
        })

        stockSymbol = intent?.getStringExtra(StocksFragment.STOCK_VIEW_KEY)
        if (stockSymbol != null) {
            GlobalScope.launch(Dispatchers.Main) {
                val description = fetchStockDescription(stockSymbol!!)
                description?.let {
                    if(description.name != null){
                        findViewById<ComposeView>(R.id.compose).setContent { MediumTopAppBarExample(description.name + " (" +description.ticker+ ")") }
                    }}
            }
            // Use coroutines to fetch and visualize stock data
            GlobalScope.launch(Dispatchers.Main) {
                StockApiService.searchStock(stockSymbol!!)

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


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MediumTopAppBarExample(symbol: String) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(
                            symbol,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { this@StockViewActivity.finish() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { stocksViewModel.delete(stockSymbol!!)
                            Toast.makeText(this@StockViewActivity, "Stock deleted!", Toast.LENGTH_LONG).show()
                            this@StockViewActivity.finish()}) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
        ) {}
    }


    private suspend fun fetchData(symbol: String): Quote? {
        return withContext(Dispatchers.IO) {
            StockApiService.stockData(symbol)
        }
    }

    private suspend fun fetchStockDescription(symbol: String): CompanyProfile2 {
        return withContext(Dispatchers.IO) {
            StockApiService.getStockDescription(symbol)
        }
    }

    private fun updateChart(candles: List<StockData>) {
        loadingProgressBar.visibility = View.GONE
        candleChart.isVisible = true
        // Extract data from candles and update the LineChart
        // You'll need to adapt this based on the format of data returned by Finnhub API
        val entries = ArrayList<CandleEntry>()
        val dateIndex = arrayOfNulls<String>(candles.size)
        var i = 0
        while (i< candles.size){
            val e = CandleEntry(i*1f,
                candles[i].high.toFloat(), candles[i].low.toFloat(),
                candles[i].open.toFloat(), candles[i].close.toFloat()
            )
            entries.add(e)
            dateIndex.set(i, getDayAndMonthFromTimestamp(candles[i].timestamp).toString())
            i++
        }
        candleChart.setHighlightPerDragEnabled(true)
        candleChart.setDrawBorders(true)
        candleChart.setBorderColor(android.graphics.Color.LTGRAY)

        val yAxis: YAxis = candleChart.getAxisLeft()
        val rightAxis: YAxis = candleChart.getAxisRight()
        yAxis.setDrawGridLines(true)
        rightAxis.setDrawGridLines(true)
        candleChart.requestDisallowInterceptTouchEvent(true)

        val xAxis: XAxis = candleChart.getXAxis()

        xAxis.setDrawGridLines(true) // disable x axis grid lines

        xAxis.setDrawLabels(true)
        rightAxis.textColor = android.graphics.Color.WHITE
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
        set1.color = android.graphics.Color.rgb(80, 80, 80)
        set1.shadowColor = android.graphics.Color.GRAY
        set1.shadowWidth = 0.8f
        set1.decreasingColor = android.graphics.Color.RED
        set1.decreasingPaintStyle = Paint.Style.FILL
        set1.increasingColor = android.graphics.Color.GREEN
        set1.increasingPaintStyle = Paint.Style.FILL
        set1.neutralColor = android.graphics.Color.LTGRAY
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