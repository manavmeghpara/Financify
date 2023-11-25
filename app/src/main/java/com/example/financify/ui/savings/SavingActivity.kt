package com.example.financify.ui.savings

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.financify.R
import com.example.financify.ui.savings.savingsDB.GoalDao
import com.example.financify.ui.savings.savingsDB.GoalDatabase
import com.example.financify.ui.savings.savingsDB.GoalEntity
import com.example.financify.ui.savings.savingsDB.GoalRepository


class SavingActivity : AppCompatActivity() {
    private lateinit var goalListView: ListView
    private lateinit var arrayList: ArrayList<GoalEntity>
    private lateinit var goalAdapter: GoalAdaptor

    private lateinit var savingsViewModel: SavingsViewModel
    private lateinit var database: GoalDatabase
    private lateinit var dbDao: GoalDao
    private lateinit var repository: GoalRepository
    private lateinit var vmFactory: SavingsViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saving)

        goalListView = findViewById(R.id.goalListView)
        database =GoalDatabase.getInstance(this)
        dbDao =database.goalDatabaseDao
        repository = GoalRepository(dbDao)
        vmFactory = SavingsViewModelFactory(repository)
        savingsViewModel = ViewModelProvider(this, vmFactory).get(SavingsViewModel::class.java)

        arrayList = ArrayList()
        goalAdapter = GoalAdaptor(this, arrayList)
        goalListView.adapter = goalAdapter

        savingsViewModel.goalListLive.observe(this, Observer {it ->
            goalAdapter = GoalAdaptor(this, it)
            goalListView.adapter = goalAdapter
            goalAdapter.notifyDataSetChanged()
        })


    }
}