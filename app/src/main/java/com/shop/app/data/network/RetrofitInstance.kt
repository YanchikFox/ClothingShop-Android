package com.shop.app.data.network

import com.shop.app.localization.LanguageTagProvider
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance(
    private val networkConfig: NetworkConfig,
    private val languageTagProvider: LanguageTagProvider? = null,
) {

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
        languageTagProvider?.let { provider ->
            builder.addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                provider.currentLanguageTag()?.takeIf { it.isNotBlank() }?.let { tag ->
                    requestBuilder.header("Accept-Language", tag)
                }
                chain.proceed(requestBuilder.build())
            }
        }
        builder.build()
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
