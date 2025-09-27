package com.shop.app.data.model

// This class describes one item in the cart
data class CartItem(
    val product: Product,
    val quantity: Int
)