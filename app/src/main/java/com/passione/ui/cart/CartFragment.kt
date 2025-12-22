package com.passione.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.passione.R
import com.passione.ui.MainViewModel

class CartFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: CartAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyCartText: TextView
    private lateinit var checkoutCard: CardView
    private lateinit var totalText: TextView
    private lateinit var checkoutButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.cartRecyclerView)
        emptyCartText = view.findViewById(R.id.emptyCartText)
        checkoutCard = view.findViewById(R.id.checkoutCard)
        totalText = view.findViewById(R.id.totalText)
        checkoutButton = view.findViewById(R.id.checkoutButton)

        setupRecyclerView()
        setupCheckoutButton()
        observeViewModel()

        // Refresh cart when fragment is shown
        viewModel.loadCart()
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter(
            onQuantityChange = { itemId, quantity ->
                viewModel.updateCartItem(itemId, quantity)
            },
            onRemove = { itemId ->
                viewModel.removeCartItem(itemId)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupCheckoutButton() {
        checkoutButton.setOnClickListener {
            viewModel.createOrder()
        }
    }

    private fun observeViewModel() {
        viewModel.cart.observe(viewLifecycleOwner) { cart ->
            if (cart == null || cart.items.isEmpty()) {
                emptyCartText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                checkoutCard.visibility = View.GONE
            } else {
                emptyCartText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                checkoutCard.visibility = View.VISIBLE
                adapter.submitList(cart.items)
                totalText.text = "${cart.total.toInt()} ₽"
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearToast()
            }
        }

        viewModel.currentOrder.observe(viewLifecycleOwner) { order ->
            order?.let {
                // Navigate to order tab would be handled in MainActivity
            }
        }
    }
}
