package com.shop.app.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: String,
    val article: String,
    @SerializedName("category_id")
    val categoryId: String,
    val name: String,
    val description: String,

    @SerializedName("image_path")
    val imagePath: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("price_string")
    val priceString: String,

    @SerializedName("is_bestseller")
    val isBestseller: Boolean
)