package com.shop.app.data.model

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    val id: Int,
    val email: String,
    val name: String?,
    val phone: String?,
    @SerializedName("created_at") val createdAt: String,
    val addresses: List<UserAddress>,
    @SerializedName("order_history") val orderHistory: List<OrderHistoryItem>
)

data class UserAddress(
    val id: Int,
    val label: String,
    val line1: String,
    val line2: String?,
    val city: String,
    @SerializedName("postal_code") val postalCode: String,
    val country: String,
    @SerializedName("is_default") val isDefault: Boolean
)

data class OrderHistoryItem(
    val id: Int,
    @SerializedName("order_number") val orderNumber: String,
    val status: String,
    @SerializedName("total_amount") val totalAmount: String,
    @SerializedName("placed_at") val placedAt: String
)

data class ProfileUpdateRequest(
    val name: String,
    val phone: String,
    val addresses: List<UpdateAddressRequest>
)

data class UpdateAddressRequest(
    val label: String,
    val line1: String,
    val line2: String?,
    val city: String,
    @SerializedName("postal_code") val postalCode: String,
    val country: String,
    @SerializedName("is_default") val isDefault: Boolean
)