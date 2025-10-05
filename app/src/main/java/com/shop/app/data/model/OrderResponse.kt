package com.shop.app.data.model

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    val id: Int,
    @SerializedName("total_amount")
    val totalAmount: Double,
    @SerializedName("created_at")
    val createdAt: String,
    val items: List<OrderItemResponse>,
)
