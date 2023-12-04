package com.example.financify.ui.stocks

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


class StockSearch : AppCompatActivity() {
    private lateinit var saveBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var addStock: NumberPicker
    private lateinit var candleChart: CandleStickChart
    private var stockSymbol: String? = null
    private lateinit var compose: ComposeView

    private lateinit var loadingProgressBar: ProgressBar
    companion object{
        val STOCK_TRANSFER_INTENT = "stock"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_search)

        compose = findViewById(R.id.compose_view)
        compose.setContent {
            DockedSearchBarSample()
        }

        saveBtn = findViewById(R.id.save_)
        cancelBtn = findViewById(R.id.cancel_)

        addStock = findViewById(R.id.number_picker)
        candleChart = findViewById(R.id.stockChart)
        candleChart.isVisible = false
        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        StockApiService.stkSearchMutableLiveData.observe(this@StockSearch, Observer { it ->
            updateChart(it);
        })

        cancelBtn.setOnClickListener{
            StockApiService.clear()
            this.finish()
        }

        saveBtn.setOnClickListener{
            if(stockSymbol!=null) {
                var stock =  StockEntity(0, stockSymbol!!.uppercase(), addStock.value )
                val intent = Intent()
                val gson = Gson()
                val data = gson.toJson(stock)
                intent.putExtra(STOCK_TRANSFER_INTENT, data)
                setResult(RESULT_OK, intent)
                StockApiService.clear()
                this.finish()
            }
        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DockedSearchBarSample() {
        var text by rememberSaveable { mutableStateOf("") }
        var list by  rememberSaveable { mutableStateOf(listOf<String>()) }
        var active by rememberSaveable { mutableStateOf(false) }

        Box(
            Modifier
                .fillMaxSize()
                .semantics { isTraversalGroup = true }) {
            DockedSearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .semantics { traversalIndex = -1f },
                query = text,
                onQueryChange = { text = it
                                       StockApiService.getAllRelatedStocks(it)
                },
                onSearch = { active = false
                    showLoadingView()
                    StockApiService.searchStock(text.uppercase())
                    stockSymbol = text
                           compose.layoutParams.height = 220},
                active = active,
                onActiveChange = { active = it },
                placeholder = { Text("Enter Stock Symbol") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
            ) {
                StockApiService.responseMutableLiveData.observe(
                    this@StockSearch,
                    Observer { it ->
                        list = it
                        println(list)
                    })
                list.forEach {
                    Row(modifier = Modifier.padding(all = 14.dp)) {
                        Text(
                            text = it,
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clickable {
                                    text = it
                                    active = false
                                }
                        )
                    }

                }
            }
        }
    }


    private fun updateChart(candles: List<StockData>) {
        hideLoadingView()
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