package com.passione.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.passione.data.model.*
import com.passione.data.repository.PassioneRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PassioneRepository(application)

    // Menu
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _restaurant = MutableLiveData<Restaurant>()
    val restaurant: LiveData<Restaurant> = _restaurant

    // Cart
    private val _cart = MutableLiveData<Cart>()
    val cart: LiveData<Cart> = _cart

    private val _cartItemCount = MutableLiveData(0)
    val cartItemCount: LiveData<Int> = _cartItemCount

    // Order
    private val _currentOrder = MutableLiveData<Order?>()
    val currentOrder: LiveData<Order?> = _currentOrder

    private val _orderStatus = MutableLiveData<String?>()
    val orderStatus: LiveData<String?> = _orderStatus

    // Loading states
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Toast messages
    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    init {
        loadMenu()
        loadCart()
    }

    fun loadMenu(lang: String = "ru") {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getMenu(lang).fold(
                onSuccess = { menu ->
                    _restaurant.value = menu.restaurant
                    _categories.value = menu.categories
                    _error.value = null
                },
                onFailure = { e ->
                    _error.value = e.message ?: "Failed to load menu"
                }
            )
            _isLoading.value = false
        }
    }

    fun loadCart() {
        viewModelScope.launch {
            repository.getCart().fold(
                onSuccess = { cartData ->
                    _cart.value = cartData
                    _cartItemCount.value = cartData.items.sumOf { it.quantity }
                },
                onFailure = { e ->
                    // Silent fail for cart - it's okay if empty
                    _cart.value = null
                    _cartItemCount.value = 0
                }
            )
        }
    }

    fun addToCart(dish: Dish, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(dish.id, quantity).fold(
                onSuccess = { cartData ->
                    _cart.value = cartData
                    _cartItemCount.value = cartData.items.sumOf { it.quantity }
                    _toastMessage.value = "${dish.name} добавлено в корзину"
                },
                onFailure = { e ->
                    _toastMessage.value = "Ошибка: ${e.message}"
                }
            )
        }
    }

    fun updateCartItem(itemId: String, quantity: Int) {
        viewModelScope.launch {
            if (quantity <= 0) {
                removeCartItem(itemId)
                return@launch
            }

            repository.updateCartItem(itemId, quantity).fold(
                onSuccess = {
                    loadCart()
                },
                onFailure = { e ->
                    _toastMessage.value = "Ошибка: ${e.message}"
                }
            )
        }
    }

    fun removeCartItem(itemId: String) {
        viewModelScope.launch {
            repository.removeCartItem(itemId).fold(
                onSuccess = {
                    loadCart()
                    _toastMessage.value = "Удалено из корзины"
                },
                onFailure = { e ->
                    _toastMessage.value = "Ошибка: ${e.message}"
                }
            )
        }
    }

    fun createOrder(comment: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createOrder(comment).fold(
                onSuccess = { order ->
                    _currentOrder.value = order
                    _orderStatus.value = order.status
                    _cart.value = null
                    _cartItemCount.value = 0
                    _toastMessage.value = "Заказ оформлен!"
                },
                onFailure = { e ->
                    _toastMessage.value = "Ошибка оформления заказа: ${e.message}"
                }
            )
            _isLoading.value = false
        }
    }

    fun checkOrderStatus() {
        val orderId = _currentOrder.value?.id ?: return
        viewModelScope.launch {
            repository.getOrderStatus(orderId).fold(
                onSuccess = { status ->
                    _orderStatus.value = status.status
                },
                onFailure = { /* ignore */ }
            )
        }
    }

    fun clearOrder() {
        _currentOrder.value = null
        _orderStatus.value = null
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
