package com.shop.app.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance(
    private val networkConfig: NetworkConfig
) {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(networkConfig.apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val baseImageUrl: String
        get() = networkConfig.imagesBaseUrl
}
