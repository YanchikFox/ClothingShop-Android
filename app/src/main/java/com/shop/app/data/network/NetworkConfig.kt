package com.shop.app.data.network

import com.shop.app.BuildConfig

data class NetworkConfig(
    val apiBaseUrl: String = BuildConfig.API_BASE_URL,
    val imagesBaseUrl: String = BuildConfig.IMAGES_BASE_URL
)
