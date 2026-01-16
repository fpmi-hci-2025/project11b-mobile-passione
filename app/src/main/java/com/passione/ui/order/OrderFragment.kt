package com.passione.ui.order

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.passione.R
import com.passione.ui.MainViewModel

class OrderFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var noOrderText: TextView
    private lateinit var orderCard: CardView
    private lateinit var statusText: TextView
    private lateinit var statusDescription: TextView
    private lateinit var orderTotalText: TextView
    private lateinit var statusTimeline: LinearLayout
    private lateinit var newOrderButton: MaterialButton

    private val statusLabels = mapOf(
        "PENDING" to Pair("Ожидание", "Ваш заказ принят и ожидает подтверждения"),
        "CONFIRMED" to Pair("Подтверждён", "Заказ подтверждён и передан на кухню"),
        "PREPARING" to Pair("Готовится", "Повара готовят ваш заказ"),
        "READY" to Pair("Готов", "Ваш заказ готов! Ожидайте официанта"),
        "DELIVERED" to Pair("Выдан", "Приятного аппетита!"),
        "CANCELLED" to Pair("Отменён", "Заказ был отменён")
    )

    private val statusOrder = listOf("PENDING", "CONFIRMED", "PREPARING", "READY", "DELIVERED")

    private val handler = Handler(Looper.getMainLooper())
    private val statusCheckRunnable = object : Runnable {
        override fun run() {
            viewModel.checkOrderStatus()
            handler.postDelayed(this, 5000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noOrderText = view.findViewById(R.id.noOrderText)
        orderCard = view.findViewById(R.id.orderCard)
        statusText = view.findViewById(R.id.statusText)
        statusDescription = view.findViewById(R.id.statusDescription)
        orderTotalText = view.findViewById(R.id.orderTotalText)
        statusTimeline = view.findViewById(R.id.statusTimeline)
        newOrderButton = view.findViewById(R.id.newOrderButton)

        setupNewOrderButton()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.currentOrder.value != null) {
            handler.post(statusCheckRunnable)
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(statusCheckRunnable)
    }

    private fun setupNewOrderButton() {
        newOrderButton.setOnClickListener {
            viewModel.clearOrder()
        }
    }

    private fun observeViewModel() {
        viewModel.currentOrder.observe(viewLifecycleOwner) { order ->
            if (order == null) {
                noOrderText.visibility = View.VISIBLE
                orderCard.visibility = View.GONE
                statusTimeline.visibility = View.GONE
                newOrderButton.visibility = View.GONE
                handler.removeCallbacks(statusCheckRunnable)
            } else {
                noOrderText.visibility = View.GONE
                orderCard.visibility = View.VISIBLE
                statusTimeline.visibility = View.VISIBLE
                newOrderButton.visibility = View.VISIBLE
                orderTotalText.text = "${order.total.toInt()} ₽"
                handler.post(statusCheckRunnable)
            }
        }

        viewModel.orderStatus.observe(viewLifecycleOwner) { status ->
            status?.let { updateStatusUI(it) }
        }
    }

    private fun updateStatusUI(status: String) {
        val (label, description) = statusLabels[status] ?: Pair(status, "")
        statusText.text = label
        statusDescription.text = description

        // Update timeline
        val currentIndex = statusOrder.indexOf(status)
        updateTimelineStep(R.id.step1, "Принят", currentIndex >= 0, currentIndex > 0)
        updateTimelineStep(R.id.step2, "Подтверждён", currentIndex >= 1, currentIndex > 1)
        updateTimelineStep(R.id.step3, "Готовится", currentIndex >= 2, currentIndex > 2)
        updateTimelineStep(R.id.step4, "Готов", currentIndex >= 3, currentIndex > 3)
        updateTimelineStep(R.id.step5, "Выдан", currentIndex >= 4, false)

        // Show new order button only when delivered
        newOrderButton.visibility = if (status == "DELIVERED" || status == "CANCELLED") {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun updateTimelineStep(viewId: Int, text: String, isActive: Boolean, isCompleted: Boolean) {
        val stepView = view?.findViewById<View>(viewId) ?: return
        val indicator = stepView.findViewById<View>(R.id.stepIndicator)
        val stepText = stepView.findViewById<TextView>(R.id.stepText)

        stepText.text = text

        when {
            isCompleted -> {
                indicator.setBackgroundResource(R.drawable.circle_completed)
                stepText.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            }
            isActive -> {
                indicator.setBackgroundResource(R.drawable.circle_active)
                stepText.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            }
            else -> {
                indicator.setBackgroundResource(R.drawable.circle_inactive)
                stepText.setTextColor(resources.getColor(android.R.color.darker_gray, null))
            }
        }
    }
}
