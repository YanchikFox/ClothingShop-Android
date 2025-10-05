package com.shop.app.data.model

import com.google.gson.annotations.SerializedName

data class OrderItemRequest(
    @SerializedName("productId")
    val productId: String,
    @SerializedName("quantity")
    val quantity: Int,
)
