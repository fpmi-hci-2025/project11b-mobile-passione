package com.passione.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.passione.R
import com.passione.ui.MainViewModel

class MenuFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: MenuAdapter

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        recyclerView = view.findViewById(R.id.menuRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.errorText)

        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MenuAdapter { dish ->
            viewModel.addToCart(dish)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setColorSchemeColors(
            resources.getColor(android.R.color.holo_red_dark, null)
        )
        swipeRefresh.setOnRefreshListener {
            viewModel.loadMenu()
        }
    }

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.submitCategories(categories)
            recyclerView.visibility = View.VISIBLE
            errorText.visibility = View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!swipeRefresh.isRefreshing) {
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
            if (!isLoading) {
                swipeRefresh.isRefreshing = false
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                errorText.text = it
                errorText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                viewModel.clearError()
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearToast()
            }
        }
    }
}
