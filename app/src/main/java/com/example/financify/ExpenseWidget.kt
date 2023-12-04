package com.example.financify

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.dataStore
import androidx.glance.Button
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.financify.ui.stocks.StockApiService
import com.example.financify.ui.stocks.StocksFragment
import com.example.financify.ui.stocks.StocksViewModel
import com.example.financify.ui.stocks.StocksViewModelFactory
import com.example.financify.ui.stocks.stockDB.StockDao
import com.example.financify.ui.stocks.stockDB.StockDatabase
import com.example.financify.ui.stocks.stockDB.StockEntity
import com.example.financify.ui.stocks.stockDB.StockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.xml.transform.Transformer

/**
 * Implementation of App Widget functionality.
 */
class ExpenseWidget : AppWidgetProvider() {

    private lateinit var allStocks: LiveData<List<StockEntity>>
    private var stockList =  ArrayList<StockEntity>()
    private lateinit var repository: StockRepository
    private lateinit var dao: StockDao


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val database = StockDatabase.getInstance(context.applicationContext)
        dao = database.stockDatabaseDao
        repository = StockRepository(dao)
        allStocks = repository.getAllStocks()
        allStocks.observeForever{
            updateList(it)
        }

        val widgetManager = AppWidgetManager.getInstance(context.applicationContext)
        widgetManager.notifyAppWidgetViewDataChanged(widgetManager.getAppWidgetIds(ComponentName(context.applicationContext.packageName,ExpenseWidget::class.java.name)),
            R.id.widget_listview
        )
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
    }
    private fun updateList(newList: List<StockEntity>) {
        stockList.clear()
        stockList.addAll(newList)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
//    val addIntent = Intent()
//    val addPendingIntent = PendingIntent.getBroadcast(context, 0, addIntent,
//        PendingIntent.FLAG_IMMUTABLE)

    val serviceIntent = Intent(context, MyRemoteView::class.java)
    serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))

//    val intent = Intent(context, StocksFragment::class.java)
//    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    val views = RemoteViews(context.packageName, R.layout.expense_widget)
    views.setRemoteAdapter(R.id.widget_listview, serviceIntent)
    views.setEmptyView(R.id.widget_listview,R.id.widget_empty_view)
    // Construct the RemoteViews object

    appWidgetManager.updateAppWidget(appWidgetId, views)

}

class MyRemoteView: RemoteViewsService(){
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return MyRemoteViewFactory(applicationContext, intent)
    }

    inner class MyRemoteViewFactory(private val context: Context, intent: Intent): RemoteViewsFactory{
        private val appWidgetId: Int = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID)
        private lateinit var allStocks: LiveData<List<StockEntity>>
        private var stockList =  ArrayList<StockEntity>()
        private var repository: StockRepository

        init {
            var dao = StockDatabase.getInstance(applicationContext).stockDatabaseDao
            repository = StockRepository(dao)
        }
        override fun onCreate() {
            allStocks = repository.getAllStocks()
            allStocks.observeForever{it->
                updateList(it)
            }
        }

        override fun onDataSetChanged() {
            allStocks = repository.getAllStocks()
            if(stockList.isNotEmpty()) {
                getViewAt(0)
            }
        }

        override fun onDestroy() {
        }

        override fun getCount(): Int {
            return stockList.size
        }

        override fun getViewAt(position: Int): RemoteViews {

            val remoteViews = RemoteViews(context.packageName, R.layout.stock_widget)
            remoteViews.setTextViewText(R.id.appwidget_text, stockList[position].symbol)
//            val data = StockApiService.stockData(stockList[position].symbol)
            remoteViews.setTextViewText(R.id.price, stockList[position].shares.toString())
            return remoteViews
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        private fun updateList(newList: List<StockEntity>) {
            stockList.clear()
            stockList.addAll(newList)
            onDataSetChanged()
        }

    }

}