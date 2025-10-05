package com.shop.app.data.model

import com.google.gson.annotations.SerializedName

data class PlaceOrderRequest(
    @SerializedName("items")
    val items: List<OrderItemRequest>,
)
