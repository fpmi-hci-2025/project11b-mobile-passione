package com.passione.data.api

import com.passione.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("restaurants/{restaurantId}/menu")
    suspend fun getMenu(
        @Path("restaurantId") restaurantId: String,
        @Query("lang") lang: String = "ru"
    ): MenuResponse

    @POST("sessions")
    suspend fun createSession(
        @Body request: CreateSessionRequest
    ): Session

    @GET("carts/{sessionId}")
    suspend fun getCart(
        @Path("sessionId") sessionId: String
    ): Response<Cart>

    @POST("carts/{sessionId}/items")
    suspend fun addToCart(
        @Path("sessionId") sessionId: String,
        @Body request: AddToCartRequest
    ): Response<Cart>

    @PATCH("cart-items/{itemId}")
    suspend fun updateCartItem(
        @Path("itemId") itemId: String,
        @Body request: UpdateCartItemRequest
    ): CartItem

    @DELETE("cart-items/{itemId}")
    suspend fun removeCartItem(
        @Path("itemId") itemId: String
    ): Response<Unit>

    @POST("orders")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): Order

    @GET("orders/{orderId}/status")
    suspend fun getOrderStatus(
        @Path("orderId") orderId: String
    ): OrderStatus
}
