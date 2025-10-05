package com.shop.app.data.repository

import com.shop.app.data.model.AddToCartRequest
import com.shop.app.data.model.CartItem
import com.shop.app.data.model.OrderItemRequest
import com.shop.app.data.model.OrderResponse
import com.shop.app.data.model.PlaceOrderRequest
import com.shop.app.data.model.Product
import com.shop.app.data.model.UpdateCartRequest
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
                categoryId = responseItem.categoryId,
                name = responseItem.name,
                description = responseItem.description,
                price = responseItem.price,
                priceString = responseItem.priceString,
                isBestseller = responseItem.isBestseller,
                imageUrls = responseItem.imageUrls,
                composition = responseItem.composition,
                careInstructions = responseItem.careInstructions,
                features = responseItem.features,
                reviews = responseItem.reviews
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

    suspend fun placeOrder(token: String, cartItems: List<CartItem>): OrderResponse {
        val request = PlaceOrderRequest(
            items = cartItems.map { cartItem ->
                OrderItemRequest(
                    productId = cartItem.product.id,
                    quantity = cartItem.quantity
                )
            }
        )

        return apiService.placeOrder(token, request)
    }
}