package com.shop.app.data.model

import com.google.gson.annotations.SerializedName

// This class exactly matches the server response
data class CartItemResponse(
    // All fields from Product
    val id: String,
    val article: String,
    @SerializedName("category_id")
    val categoryId: String,
    val name: String,
    val description: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("price_string")
    val priceString: String,
    @SerializedName("is_bestseller")
    val isBestseller: Boolean,
    @SerializedName("imageUrls")
    val imageUrls: List<String>,
    val composition: String,
    @SerializedName("careInstructions")
    val careInstructions: String,
    val features: List<ProductFeature>,
    val reviews: List<ProductReview>,
    // And quantity field from cart_items
    val quantity: Int
)