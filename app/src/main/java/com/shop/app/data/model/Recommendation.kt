package com.shop.app.data.model

import com.google.gson.annotations.SerializedName

data class RecommendationItem(
    val product: Product,
    val score: Double? = null,
)

data class PersonalRecommendationsRequest(
    val categories: List<String> = emptyList(),
    val brands: List<String> = emptyList(),
    @SerializedName("priceRange") val priceRange: List<Double>? = null,
)
