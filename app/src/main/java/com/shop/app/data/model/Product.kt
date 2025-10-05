package com.shop.app.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: String,
    val article: String,
    @SerializedName("category_id")
    val categoryId: String? = null,
    val name: String,
    val description: String,
    @SerializedName("price")
    val price: Double = 0.0,

    @SerializedName("price_string")
    val priceString: String = "",

    @SerializedName("is_bestseller")
    val isBestseller: Boolean = false,

    @SerializedName("imageUrls")
    val imageUrls: List<String> = emptyList(),
    @SerializedName("image_path")
    val imagePath: String? = null,
    val composition: String = "",
    @SerializedName("careInstructions")
    val careInstructions: String = "",
    val features: List<ProductFeature> = emptyList(),
    val reviews: List<ProductReview> = emptyList(),
    @SerializedName("gender")
    val gender: String? = null
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