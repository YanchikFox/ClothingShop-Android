package com.shop.app.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    val id: String,
    val name: String,
    val slug: String,

    @SerializedName("parent_id")
    val parentId: String?,

    @SerializedName("image_path")
    val imagePath: String,

    @SerializedName("icon_path")
    val iconPath: String
)