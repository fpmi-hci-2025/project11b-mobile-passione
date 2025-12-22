package com.passione

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.passione.ui.MainViewModel
import com.passione.ui.cart.CartFragment
import com.passione.ui.menu.MenuFragment
import com.passione.ui.order.OrderFragment

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var toolbar: MaterialToolbar
    private lateinit var bottomNavigation: BottomNavigationView

    private val menuFragment = MenuFragment()
    private val cartFragment = CartFragment()
    private val orderFragment = OrderFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        setSupportActionBar(toolbar)
        setupBottomNavigation()

        // Show menu fragment by default
        if (savedInstanceState == null) {
            showFragment(menuFragment)
        }

        observeViewModel()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_menu -> {
                    showFragment(menuFragment)
                    toolbar.title = "Passione"
                    true
                }
                R.id.nav_cart -> {
                    showFragment(cartFragment)
                    toolbar.title = "Корзина"
                    true
                }
                R.id.nav_order -> {
                    showFragment(orderFragment)
                    toolbar.title = "Заказ"
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun observeViewModel() {
        // Navigate to order tab after successful order
        viewModel.currentOrder.observe(this) { order ->
            order?.let {
                bottomNavigation.selectedItemId = R.id.nav_order
            }
        }

        // Update cart badge
        viewModel.cartItemCount.observe(this) { count ->
            val badge = bottomNavigation.getOrCreateBadge(R.id.nav_cart)
            if (count > 0) {
                badge.isVisible = true
                badge.number = count
            } else {
                badge.isVisible = false
            }
        }
    }
}
