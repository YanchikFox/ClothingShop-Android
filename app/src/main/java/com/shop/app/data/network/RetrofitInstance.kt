package com.shop.app.data.network

import com.shop.app.data.repository.AuthRepository
import com.shop.app.localization.LanguageRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance(
    private val networkConfig: NetworkConfig,
    private val languageRepository: LanguageRepository,
    private val authRepository: AuthRepository
) {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                runBlocking {
                    val languageTag = languageRepository.languageFlow.first()
                    val token = authRepository.getAuthToken()

                    val requestBuilder = chain.request().newBuilder()

                    if (!languageTag.isNullOrBlank()) {
                        requestBuilder.header("Accept-Language", languageTag)
                    }

                    token?.let {
                        requestBuilder.header("x-auth-token", it)
                    }

                    chain.proceed(requestBuilder.build())
                }
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(networkConfig.apiBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val baseImageUrl: String
        get() = networkConfig.imagesBaseUrl
}