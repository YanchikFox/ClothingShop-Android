package com.shop.app.data.network

import com.shop.app.data.model.AddToCartRequest
import com.shop.app.data.model.AuthRequest
import com.shop.app.data.model.AuthResponse
import com.shop.app.data.model.CartItemResponse
import com.shop.app.data.model.Category
import com.shop.app.data.model.Product
import com.shop.app.data.model.ProfileResponse
import com.shop.app.data.model.ProfileUpdateRequest
import com.shop.app.data.model.UpdateCartRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Products and Categories
    @GET("products")
    suspend fun getProducts(@Query("gender") gender: String? = null): List<Product>

    @GET("categories")
    suspend fun getCategories(): List<Category>

    // Authentication
    @POST("register")
    suspend fun registerUser(@Body request: AuthRequest)

    @POST("login")
    suspend fun loginUser(@Body request: AuthRequest): AuthResponse

    @GET("profile")
    suspend fun getProfile(): ProfileResponse

    @PUT("profile")
    suspend fun updateProfile(@Body request: ProfileUpdateRequest): ProfileResponse

    // Get user's cart
    @GET("cart")
    suspend fun getCart(): List<CartItemResponse> // Expect new type

    @PUT("cart/item/{productId}")
    suspend fun updateCartItemQuantity(
        @Path("productId") productId: String,
        @Body request: UpdateCartRequest
    )

    @DELETE("cart/item/{productId}")
    suspend fun removeCartItem(@Path("productId") productId: String)

    // Add product to cart
    @POST("cart")
    suspend fun addToCart(@Body request: AddToCartRequest)

    @GET("search")
    suspend fun searchProducts(
        @Query("q") query: String? = null,
        @Query("gender") gender: String? = null,
    ): List<Product>
}