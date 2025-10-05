package com.shop.app.data.repository

import com.shop.app.data.model.*
import com.shop.app.data.network.ApiService
import com.shop.app.di.ServiceLocator

class CartRepository {
    private val apiService: ApiService = ServiceLocator.apiService

    suspend fun getCart(token: String): List<CartItem> {
        // Get "flat" response from server
        val responseItems = apiService.getCart(token)

        // Transform it into the List<CartItem> structure we need
        return responseItems.map { responseItem ->
            // Build Product object from response fields
            val product = Product(
                id = responseItem.id,
                article = responseItem.article,
                gender = responseItem.gender,
                name = responseItem.name,
                description = responseItem.description,
                imagePath = responseItem.imagePath,
                price = responseItem.price,
                priceString = responseItem.priceString,
                isBestseller = responseItem.isBestseller
            )
            // Create CartItem
            CartItem(product = product, quantity = responseItem.quantity)
        }
    }

    suspend fun addToCart(token: String, productId: String, quantity: Int) {
        val request = AddToCartRequest(productId = productId, quantity = quantity)
        apiService.addToCart(token, request)
    }

    suspend fun updateCartItemQuantity(token: String, productId: String, quantity: Int) {
        val request = UpdateCartRequest(quantity = quantity)
        apiService.updateCartItemQuantity(token, productId, request)
    }

    suspend fun removeCartItem(token: String, productId: String) {
        apiService.removeCartItem(token, productId)
    }
}