package com.example.financify.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.financify.R
import com.example.financify.databinding.FragmentDashboardBinding
import com.example.financify.ui.savings.SavingActivity
import com.example.financify.ui.stocks.StockViewActivity
import com.example.financify.ui.stocks.StocksFragment

class DashboardFragment : Fragment() {

    private lateinit var saving_btn: Button
    private var _binding: FragmentDashboardBinding? = null

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
        saving_btn = root.findViewById(R.id.savings_btn)

        saving_btn.setOnClickListener{
            val intent = Intent(requireContext(), SavingActivity::class.java)
            startActivity(intent)
        }

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        var button: Button = root.findViewById(R.id.launch_budget)
        button.setOnClickListener() {
            val intent = Intent(requireActivity(), EditBudget::class.java)
            startActivity(intent)
        }
        var expenseButton: Button = root.findViewById(R.id.launch_expenses)
        expenseButton.setOnClickListener() {
            val intent = Intent(requireActivity(), EditExpenses::class.java)
            startActivity(intent)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}