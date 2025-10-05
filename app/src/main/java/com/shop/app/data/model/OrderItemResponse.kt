package com.shop.app.data.model

import com.google.gson.annotations.SerializedName

data class OrderItemResponse(
    @SerializedName("product_id")
    val productId: String,
    val quantity: Int,
    @SerializedName("unit_price")
    val unitPrice: Double,
    @SerializedName("line_total")
    val lineTotal: Double,
)
