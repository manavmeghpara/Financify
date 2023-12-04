package com.example.financify.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.financify.R
import com.example.financify.SignInActivity
import com.example.financify.databinding.FragmentDashboardBinding
import com.example.financify.ui.budget.EditBudget
import com.example.financify.ui.budget.EditExpenses
import com.example.financify.ui.budget.EditPurchases
import com.example.financify.ui.savings.SavingActivity
import com.example.financify.ui.visualization.VisualizeActivity
import com.google.firebase.auth.FirebaseAuth

class DashboardFragment : Fragment() {

    private lateinit var saving_btn: Button
    private var _binding: FragmentDashboardBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}