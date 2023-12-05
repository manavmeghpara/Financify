package com.example.financify.ui.stocks

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.financify.ExpenseWidget
import com.example.financify.R
import com.example.financify.databinding.FragmentStocksBinding
import com.example.financify.ui.stocks.stockDB.StockDao
import com.example.financify.ui.stocks.stockDB.StockDatabase
import com.example.financify.ui.stocks.stockDB.StockEntity
import com.example.financify.ui.stocks.stockDB.StockRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StockActivity : AppCompatActivity() {
    private val STOCK_SEARCH_REQ_CODE = 101
    private lateinit var listView : ListView

    private var _binding: FragmentStocksBinding? = null
    private lateinit var arrayList: ArrayList<StockEntity>
    private lateinit var stkAdaptor: StockAdapter
    private lateinit var stocksViewModel: StocksViewModel

    private lateinit var database: StockDatabase
    private lateinit var dbDao: StockDao
    private lateinit var repository: StockRepository
    private lateinit var vmFactory: StocksViewModelFactory


    private val SHARED_PREF_LIST_KEY = "stock_list"
    companion object {
        val STOCK_VIEW_KEY = "stock_view"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock)
        database =StockDatabase.getInstance(this)
        dbDao =database.stockDatabaseDao
        repository = StockRepository(dbDao)
        vmFactory = StocksViewModelFactory(repository)
        stocksViewModel = ViewModelProvider(this, vmFactory).get(StocksViewModel::class.java)

        stocksViewModel.stockList.observe(this, Observer { it->
            val appWidgetManager = AppWidgetManager.getInstance(this.applicationContext)
            val thisAppWidget = ComponentName(
                this.applicationContext.packageName,
                ExpenseWidget::class.java.name
            )
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview )
        })


        arrayList = ArrayList()
        listView = findViewById(R.id.listView)
        stkAdaptor = StockAdapter(this, arrayList)

        listView.adapter = stkAdaptor
        stocksViewModel.stockList.observe(this, Observer {it ->
            stkAdaptor = StockAdapter(this, it)
            listView.adapter = stkAdaptor
            stkAdaptor.notifyDataSetChanged()
        })

        listView.setOnItemClickListener{parent, view, pos, id->
            val intent = Intent(this, StockViewActivity::class.java)
            intent.putExtra(STOCK_VIEW_KEY, stocksViewModel.stockList.value?.get(pos)?.symbol)
            startActivity(intent);

        }

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            val intent = Intent(this, StockSearch::class.java)
            startActivityForResult(intent, STOCK_SEARCH_REQ_CODE )
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == STOCK_SEARCH_REQ_CODE && resultCode == Activity.RESULT_OK){
            val stk = data?.getStringExtra(StockSearch.STOCK_TRANSFER_INTENT)
            val gson = Gson()
            val type = object : TypeToken<StockEntity>(){}.type
            val stock = gson.fromJson<StockEntity>(stk, type)
            println("STOCK: $stock")
            stocksViewModel.insert(stock)
        }
    }

}