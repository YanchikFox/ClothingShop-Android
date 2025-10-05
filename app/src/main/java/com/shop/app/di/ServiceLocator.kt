package com.shop.app.di

import com.shop.app.data.network.ApiService
import com.shop.app.data.network.NetworkConfig
import com.shop.app.data.network.RetrofitInstance

object ServiceLocator {

    var networkConfig: NetworkConfig = NetworkConfig()
        set(value) {
            field = value
            updateRetrofitInstance(RetrofitInstance(field))
        }

    private var retrofitInstance: RetrofitInstance = RetrofitInstance(networkConfig)
    private var _apiService: ApiService = retrofitInstance.api

    val apiService: ApiService
        get() = _apiService

    val imagesBaseUrl: String
        get() = retrofitInstance.baseImageUrl

    fun updateRetrofitInstance(instance: RetrofitInstance) {
        retrofitInstance = instance
        _apiService = instance.api
    }

    fun setApiService(apiService: ApiService) {
        this._apiService = apiService
    }
}
