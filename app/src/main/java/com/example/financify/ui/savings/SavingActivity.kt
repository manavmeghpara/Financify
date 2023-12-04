package com.example.financify.ui.savings

import android.os.Bundle
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.financify.R
import com.example.financify.ui.savings.savingsDB.GoalDao
import com.example.financify.ui.savings.savingsDB.GoalDatabase
import com.example.financify.ui.savings.savingsDB.GoalEntity
import com.example.financify.ui.savings.savingsDB.GoalRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton


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
        var fabAddGoal: FloatingActionButton = findViewById(R.id.fab_add_goal)
        fabAddGoal.setOnClickListener {
            showAddGoalDialog()
        }
        goalListView.setOnItemClickListener { _, _, position, _ ->
            // Get the selected goal
            val selectedGoal = goalAdapter.getItem(position)

            // Show details dialog for the selected goal
            showGoalDetailsDialog(selectedGoal)
            }


    }
    private fun showAddGoalDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_goal, null)
        dialogBuilder.setView(dialogView)

        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        val editTextTarget = dialogView.findViewById<EditText>(R.id.editTextTarget)
        val editTextProgress = dialogView.findViewById<EditText>(R.id.editTextProgress)

        dialogBuilder.setTitle("Add Goal")
        dialogBuilder.setPositiveButton("Save") { _, _ ->
            val name = editTextName.text.toString()
            val target = editTextTarget.text.toString().toDouble()
            val progress = editTextProgress.text.toString().toDouble()

            // Save the goal using your ViewModel
            val newGoal = GoalEntity(type = "Custom", name = name, amount = target, progress = progress)
            savingsViewModel.insert(newGoal)
        }
        dialogBuilder.setNegativeButton("Discard") { _, _ ->
            // Do nothing, as we're discarding the changes
        }

        val dialog = dialogBuilder.create()
        dialog.show()
        }
    private fun showGoalDetailsDialog(goal: GoalEntity) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_goal_details, null)
        dialogBuilder.setView(dialogView)

        val editTextName = dialogView.findViewById<EditText>(R.id.editTextGoalName)
        val editTextAmount = dialogView.findViewById<EditText>(R.id.editTextGoalAmount)
        val editTextProgress = dialogView.findViewById<EditText>(R.id.editTextGoalProgress)

        editTextName.setText(goal.name)
        editTextAmount.setText(goal.amount.toString())
        editTextProgress.setText(goal.progress.toString())

        dialogBuilder.setTitle("Edit Goal Details")
        dialogBuilder.setPositiveButton("Save") { _, _ ->
            try {
                val editedName = editTextName.text.toString()
                val editedAmount = editTextAmount.text.toString().toDouble()
                val editedProgress = editTextProgress.text.toString().toDouble()

                // Create a new GoalEntity with the edited values
                val editedGoal = goal.copy(name = editedName, amount = editedAmount, progress = editedProgress)

                // update
                savingsViewModel.UpdateGoal(editedGoal)
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid input. Please enter numeric values.", Toast.LENGTH_SHORT).show()
            }
        }
        dialogBuilder.setNegativeButton("Cancel") { _, _ ->
            // Do nothing, as the user canceled the edit
        }

        val dialog = dialogBuilder.create()
        dialog.show()
        }
}