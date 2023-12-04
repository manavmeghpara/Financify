package com.example.financify.ui.stocks

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.financify.ExpenseWidget
import com.example.financify.R
import com.example.financify.databinding.FragmentStocksBinding
import com.example.financify.ui.stocks.stockDB.StockDao
import com.example.financify.ui.stocks.stockDB.StockDatabase
import com.example.financify.ui.stocks.stockDB.StockEntity
import com.example.financify.ui.stocks.stockDB.StockRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StocksFragment : Fragment() {

    private val STOCK_SEARCH_REQ_CODE = 101
    private lateinit var listView :ListView

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
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database =StockDatabase.getInstance(requireActivity())
        dbDao =database.stockDatabaseDao
        repository = StockRepository(dbDao)
        vmFactory = StocksViewModelFactory(repository)
        stocksViewModel =ViewModelProvider(this, vmFactory).get(StocksViewModel::class.java)

        stocksViewModel.stockList.observe(viewLifecycleOwner, Observer { it->
            val appWidgetManager = AppWidgetManager.getInstance(requireActivity().applicationContext)
            val thisAppWidget = ComponentName(
                requireActivity().applicationContext.packageName,
                ExpenseWidget::class.java.name
            )
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview )
        })

        _binding = FragmentStocksBinding.inflate(inflater, container, false)
        val root: View = binding.root

        arrayList = ArrayList()
        listView = root.findViewById(R.id.listView)
        stkAdaptor = StockAdapter(requireContext(), arrayList)

        listView.adapter = stkAdaptor
        stocksViewModel.stockList.observe(requireActivity(), Observer {it ->
            stkAdaptor = StockAdapter(root.context, it)
            listView.adapter = stkAdaptor
            stkAdaptor.notifyDataSetChanged()
        })

        listView.setOnItemClickListener{parent, view, pos, id->
            val intent = Intent(requireContext(), StockViewActivity::class.java)
            intent.putExtra(STOCK_VIEW_KEY, stocksViewModel.stockList.value?.get(pos)?.symbol)
            val a : FragmentActivity? = activity
            startActivity(intent);
            a!!.overridePendingTransition(R.anim.slide_up,  R.anim.slide_down )

        }

        val fab: View = binding.fab
        fab.setOnClickListener { view ->
            val intent = Intent(requireActivity(), StockSearch::class.java)
            startActivityForResult(intent, STOCK_SEARCH_REQ_CODE )
        }

        return root
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

