package com.shop.app.di

import android.content.Context
import com.shop.app.data.network.ApiService
import com.shop.app.data.network.NetworkConfig
import com.shop.app.data.network.RetrofitInstance
import com.shop.app.localization.DataStoreLanguageTagProvider
import com.shop.app.localization.LanguageTagProvider

object ServiceLocator {

    private var languageTagProvider: LanguageTagProvider? = null

    var networkConfig: NetworkConfig = NetworkConfig()
        set(value) {
            field = value
            updateRetrofitInstance(RetrofitInstance(field, languageTagProvider))
        }

    private var retrofitInstance: RetrofitInstance = RetrofitInstance(networkConfig, languageTagProvider)
    private var _apiService: ApiService = retrofitInstance.api

    val apiService: ApiService
        get() = _apiService

    val imagesBaseUrl: String
        get() = retrofitInstance.baseImageUrl

    fun initialize(context: Context) {
        languageTagProvider = DataStoreLanguageTagProvider(context)
        updateRetrofitInstance(RetrofitInstance(networkConfig, languageTagProvider))
    }

    fun updateRetrofitInstance(instance: RetrofitInstance) {
        retrofitInstance = instance
        _apiService = instance.api
    }

    fun setApiService(apiService: ApiService) {
        this._apiService = apiService
    }
}
