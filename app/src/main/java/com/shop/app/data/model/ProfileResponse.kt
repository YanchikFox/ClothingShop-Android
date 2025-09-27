package com.shop.app.data.model

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    val id: Int,
    val email: String,
    // Tell Gson how to map snake_case to camelCase
    @SerializedName("created_at")
    val createdAt: String
)