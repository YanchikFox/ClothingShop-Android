package com.shop.app.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    val id: String,
    val name: String,
    val slug: String = id,

    @SerializedName("parent_id")
    val parentId: String? = null,

    @SerializedName("image_path")
    val imagePath: String = "",

    @SerializedName("icon_path")
    val iconPath: String = ""
)