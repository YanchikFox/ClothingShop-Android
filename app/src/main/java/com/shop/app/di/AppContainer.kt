package com.shop.app.di

import android.content.Context
import com.shop.app.BuildConfig
import com.shop.app.data.network.NetworkConfig
import com.shop.app.data.network.RetrofitInstance
import com.shop.app.data.repository.AuthRepository
import com.shop.app.data.repository.CartRepository
import com.shop.app.data.repository.CatalogRepository
import com.shop.app.data.repository.ProductRepository
import com.shop.app.data.repository.UserRepository
import com.shop.app.localization.LanguageRepository

interface AppContainer {
    val cartRepository: CartRepository
    val userRepository: UserRepository
    val languageRepository: LanguageRepository
    val catalogRepository: CatalogRepository
    val authRepository: AuthRepository
    val productRepository: ProductRepository
    fun getImagesBaseUrl(): String
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val networkConfig = NetworkConfig(
        apiBaseUrl = BuildConfig.API_BASE_URL,
        imagesBaseUrl = BuildConfig.IMAGES_BASE_URL
    )

    override val authRepository: AuthRepository by lazy {
        AuthRepository(context)
    }

    override val languageRepository: LanguageRepository by lazy {
        LanguageRepository(context)
    }

    private val retrofitInstance by lazy {
        RetrofitInstance(networkConfig, languageRepository, authRepository)
    }

    override val catalogRepository: CatalogRepository by lazy {
        CatalogRepository(retrofitInstance.api)
    }

    override val cartRepository: CartRepository by lazy {
        CartRepository(retrofitInstance.api)
    }

    override val userRepository: UserRepository by lazy {
        UserRepository(retrofitInstance.api)
    }

    override val productRepository: ProductRepository by lazy {
        ProductRepository(retrofitInstance.api)
    }

    override fun getImagesBaseUrl(): String {
        return networkConfig.imagesBaseUrl
    }
}