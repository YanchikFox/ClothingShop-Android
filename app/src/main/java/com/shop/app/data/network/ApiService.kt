package com.shop.app.data.network

import com.shop.app.data.model.AddToCartRequest
import com.shop.app.data.model.AuthRequest
import com.shop.app.data.model.AuthResponse
import com.shop.app.data.model.CartItemResponse
import com.shop.app.data.model.Category
import com.shop.app.data.model.OrderResponse
import com.shop.app.data.model.PlaceOrderRequest
import com.shop.app.data.model.Product
import com.shop.app.data.model.ProfileResponse
import com.shop.app.data.model.ProfileUpdateRequest
import com.shop.app.data.model.UpdateCartRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Products and Categories
    @GET("api/products")
    suspend fun getProducts(@Query("categoryId") categoryId: String? = null): List<Product>

    @GET("api/categories")
    suspend fun getCategories(): List<Category>

    // Authentication
    @POST("api/register")
    suspend fun registerUser(@Body request: AuthRequest)

    @POST("api/login")
    suspend fun loginUser(@Body request: AuthRequest): AuthResponse

    @GET("api/profile")
    suspend fun getProfile(@Header("x-auth-token") token: String): ProfileResponse

    @PUT("api/profile")
    suspend fun updateProfile(
        @Header("x-auth-token") token: String,
        @Body request: ProfileUpdateRequest
    ): ProfileResponse

    // Get user's cart
    @GET("api/cart")
    suspend fun getCart(@Header("x-auth-token") token: String): List<CartItemResponse> // Expect new type

    @PUT("api/cart/item/{productId}")
    suspend fun updateCartItemQuantity(
        @Header("x-auth-token") token: String,
        @Path("productId") productId: String,
        @Body request: UpdateCartRequest
    )

    @DELETE("api/cart/item/{productId}")
    suspend fun removeCartItem(
        @Header("x-auth-token") token: String,
        @Path("productId") productId: String
    )

    // Add product to cart
    @POST("api/cart")
    suspend fun addToCart(
        @Header("x-auth-token") token: String,
        @Body request: AddToCartRequest
    )

    @GET("api/search")
    suspend fun searchProducts(
        @Query("q") query: String? = null,
        @Query("categoryId") categoryId: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("size") size: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null,
    ): List<Product>

    @POST("api/orders")
    suspend fun placeOrder(
        @Header("x-auth-token") token: String,
        @Body request: PlaceOrderRequest
    ): OrderResponse
}