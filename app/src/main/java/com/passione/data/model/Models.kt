package com.passione.data.model

import com.google.gson.annotations.SerializedName

data class Restaurant(
    val id: String,
    val name: String,
    val description: String? = null,
    val address: String? = null
)

data class Category(
    val id: String,
    val name: String,
    @SerializedName("display_order")
    val displayOrder: Int = 0,
    val dishes: List<Dish> = emptyList()
)

data class Dish(
    val id: String,
    val name: String,
    val description: String? = null,
    val price: Double,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("is_available")
    val isAvailable: Boolean = true,
    val weight: String? = null,
    val calories: Int? = null
)

data class MenuResponse(
    val restaurant: Restaurant,
    val categories: List<Category>
)

data class CartItem(
    val id: String,
    @SerializedName("dish_id")
    val dishId: String,
    val dish: Dish? = null,
    val quantity: Int,
    val comment: String? = null,
    @SerializedName("item_total")
    val itemTotal: Double = 0.0
)

data class Cart(
    val id: String,
    @SerializedName("session_id")
    val sessionId: String,
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0
)

data class Session(
    val id: String,
    @SerializedName("table_id")
    val tableId: String,
    @SerializedName("device_id")
    val deviceId: String,
    val language: String = "ru"
)

data class CreateSessionRequest(
    @SerializedName("table_id")
    val tableId: String,
    @SerializedName("device_id")
    val deviceId: String,
    val language: String = "ru"
)

data class AddToCartRequest(
    @SerializedName("dish_id")
    val dishId: String,
    val quantity: Int = 1,
    val comment: String? = null
)

data class UpdateCartItemRequest(
    val quantity: Int,
    val comment: String? = null
)

data class CreateOrderRequest(
    @SerializedName("session_id")
    val sessionId: String,
    val comment: String? = null
)

data class Order(
    val id: String,
    @SerializedName("session_id")
    val sessionId: String,
    val status: String,
    val items: List<OrderItem> = emptyList(),
    val total: Double = 0.0,
    val comment: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null
)

data class OrderItem(
    val id: String,
    @SerializedName("dish_id")
    val dishId: String,
    @SerializedName("dish_name")
    val dishName: String,
    val quantity: Int,
    val price: Double,
    @SerializedName("item_total")
    val itemTotal: Double
)

data class OrderStatus(
    val status: String,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)
