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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.financify.R
import com.example.financify.databinding.FragmentDashboardBinding
import com.example.financify.ui.savings.SavingActivity
import com.example.financify.ui.stocks.StockViewActivity
import com.example.financify.ui.stocks.StocksFragment
import com.example.financify.ui.visualization.VisualizeActivity

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
        var purchaseButton: Button = root.findViewById(R.id.launch_purchases)
        purchaseButton.setOnClickListener() {
            val intent = Intent(requireActivity(), EditPurchases::class.java)
            startActivity(intent)
        }
        var visualizationButton: Button = root.findViewById(R.id.launch_visualization)
        visualizationButton.setOnClickListener() {
            val intent = Intent(requireActivity(), VisualizeActivity::class.java)
            startActivity(intent)
        }
    //  menu pop-up button
        val menu_btn: Button = root.findViewById(R.id.menu_budget)
        // Initializing the popup menu and giving the reference as current context
        menu_btn.setOnClickListener(){
            val popupMenu: PopupMenu = PopupMenu(requireActivity(), it, Gravity.FILL_VERTICAL)
            val inflater: MenuInflater = popupMenu.menuInflater
            inflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.show()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}