package com.example.financify.ui.savings

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ProgressBar
import android.widget.TextView
import com.example.financify.R
import com.example.financify.ui.savings.savingsDB.GoalEntity
import com.example.financify.ui.stocks.stockDB.StockEntity
import java.text.DecimalFormat

class GoalAdaptor(private val context: Context, private val goalList: List<GoalEntity>) : BaseAdapter() {

    override fun getCount(): Int = goalList.size

    override fun getItem(position: Int): GoalEntity = goalList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = View.inflate(context, R.layout.goal_adaptor_view, null)
        var name = itemView.findViewById<TextView>(R.id.textViewGoalName)
        var value =  itemView.findViewById<TextView>(R.id.textViewGoalPrice)
        var progress =  itemView.findViewById<TextView>(R.id.textViewProgress)
        var progressBar =  itemView.findViewById<ProgressBar>(R.id.progressBar)



        val goal = goalList.get(position)
        name.text = goal.name
        value.text = "Goal: ${goal.amount}"
        val percent = DecimalFormat("#.##").format((goal.progress/goal.amount)*100)
        progress.text = "${percent} %"

        progressBar.progress = (Math.round(percent.toDouble())).toInt()

        return itemView!!
    }
}