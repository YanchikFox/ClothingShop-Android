package com.shop.app.data.model

import com.google.gson.annotations.SerializedName

data class Product(
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
    val reviews: List<ProductReview>
)

data class ProductFeature(
    val title: String,
    val value: String
)

data class ProductReview(
    val author: String,
    val rating: Int,
    val comment: String
)