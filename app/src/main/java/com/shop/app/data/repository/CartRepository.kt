package com.shop.app.data.repository

import com.shop.app.data.model.AddToCartRequest
import com.shop.app.data.model.CartItem
import com.shop.app.data.model.Product
import com.shop.app.data.model.UpdateCartRequest
import com.shop.app.data.network.ApiService

class CartRepository(private val apiService: ApiService) {

    suspend fun getCart(): List<CartItem> {
        // Get "flat" response from server
        val responseItems = apiService.getCart()

        // Transform it into the List<CartItem> structure we need
        return responseItems.map { responseItem ->
            // Build Product object from response fields
            val product = Product(
                id = responseItem.id,
                article = responseItem.article,
                categoryId = responseItem.categoryId,
                name = responseItem.name,
                description = responseItem.description,
                price = responseItem.price,
                priceString = responseItem.priceString,
                isBestseller = responseItem.isBestseller,
                imageUrls = responseItem.imageUrls.ifEmpty {
                    responseItem.imagePath?.let { listOf(it) } ?: emptyList()
                },
                imagePath = responseItem.imagePath,
                composition = responseItem.composition,
                careInstructions = responseItem.careInstructions,
                features = responseItem.features,
                reviews = responseItem.reviews,
                gender = responseItem.gender
            )
            // Create CartItem
            CartItem(product = product, quantity = responseItem.quantity)
        }
    }

    suspend fun addToCart(productId: String, quantity: Int) {
        val request = AddToCartRequest(productId = productId, quantity = quantity)
        apiService.addToCart(request)
    }

    suspend fun updateCartItemQuantity(productId: String, quantity: Int) {
        val request = UpdateCartRequest(quantity = quantity)
        apiService.updateCartItemQuantity(productId, request)
    }

    suspend fun removeCartItem(productId: String) {
        apiService.removeCartItem(productId)
    }
}