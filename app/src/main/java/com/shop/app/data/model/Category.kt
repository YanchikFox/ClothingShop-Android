package com.shop.app.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    val id: String,
    val name: String,

    @SerializedName("image_path")
    val imagePath: String
)