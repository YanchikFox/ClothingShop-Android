package com.shop.app.data.model

import com.google.gson.annotations.SerializedName

// This class exactly matches the server response
data class CartItemResponse(
    // All fields from Product
    val id: String,
    val article: String,
    val gender: String,
    val name: String,
    val description: String,
    @SerializedName("image_path")
    val imagePath: String,
    @SerializedName("price_string")
    val priceString: String,
    @SerializedName("is_bestseller")
    val isBestseller: Boolean,
    // And quantity field from cart_items
    val quantity: Int
)