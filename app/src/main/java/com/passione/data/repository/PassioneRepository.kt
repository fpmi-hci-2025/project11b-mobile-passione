package com.passione.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.passione.data.api.RetrofitClient
import com.passione.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PassioneRepository(context: Context) {

    private val apiService = RetrofitClient.apiService
    private val prefs: SharedPreferences = context.getSharedPreferences("passione_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val RESTAURANT_ID = "11111111-1111-1111-1111-111111111111"
        private const val TABLE_ID = "33333333-3333-3333-3333-333333333331"
        private const val KEY_SESSION_ID = "session_id"
        private const val KEY_DEVICE_ID = "device_id"
    }

    private var sessionId: String?
        get() = prefs.getString(KEY_SESSION_ID, null)
        set(value) = prefs.edit().putString(KEY_SESSION_ID, value).apply()

    private val deviceId: String
        get() {
            var id = prefs.getString(KEY_DEVICE_ID, null)
            if (id == null) {
                id = "device-${System.currentTimeMillis()}"
                prefs.edit().putString(KEY_DEVICE_ID, id).apply()
            }
            return id
        }

    suspend fun getMenu(lang: String = "ru"): Result<MenuResponse> = withContext(Dispatchers.IO) {
        try {
            val menu = apiService.getMenu(RESTAURANT_ID, lang)
            Result.success(menu)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun createSession(): String {
        val request = CreateSessionRequest(
            tableId = TABLE_ID,
            deviceId = deviceId,
            language = "ru"
        )
        val session = apiService.createSession(request)
        sessionId = session.id
        return session.id
    }

    private suspend fun ensureSession(): String {
        return sessionId ?: createSession()
    }

    private fun resetSession() {
        sessionId = null
    }

    suspend fun getCart(): Result<Cart> = withContext(Dispatchers.IO) {
        try {
            val sid = ensureSession()
            val response = apiService.getCart(sid)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 404) {
                // Session expired, create new one
                resetSession()
                val newSid = createSession()
                val newResponse = apiService.getCart(newSid)
                if (newResponse.isSuccessful && newResponse.body() != null) {
                    Result.success(newResponse.body()!!)
                } else {
                    Result.failure(Exception("Failed to get cart"))
                }
            } else {
                Result.failure(Exception("Failed to get cart: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addToCart(dishId: String, quantity: Int = 1, comment: String? = null): Result<Cart> =
        withContext(Dispatchers.IO) {
            try {
                val sid = ensureSession()
                val request = AddToCartRequest(dishId, quantity, comment)
                val response = apiService.addToCart(sid, request)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else if (response.code() == 404) {
                    // Session expired, create new one and retry
                    resetSession()
                    val newSid = createSession()
                    val newResponse = apiService.addToCart(newSid, request)
                    if (newResponse.isSuccessful && newResponse.body() != null) {
                        Result.success(newResponse.body()!!)
                    } else {
                        Result.failure(Exception("Failed to add to cart"))
                    }
                } else {
                    Result.failure(Exception("Failed to add to cart: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun updateCartItem(itemId: String, quantity: Int, comment: String? = null): Result<CartItem> =
        withContext(Dispatchers.IO) {
            try {
                val request = UpdateCartItemRequest(quantity, comment)
                val item = apiService.updateCartItem(itemId, request)
                Result.success(item)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun removeCartItem(itemId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            apiService.removeCartItem(itemId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createOrder(comment: String? = null): Result<Order> = withContext(Dispatchers.IO) {
        try {
            val sid = sessionId ?: throw Exception("No session")
            val request = CreateOrderRequest(sid, comment)
            val order = apiService.createOrder(request)
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderStatus(orderId: String): Result<OrderStatus> = withContext(Dispatchers.IO) {
        try {
            val status = apiService.getOrderStatus(orderId)
            Result.success(status)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
