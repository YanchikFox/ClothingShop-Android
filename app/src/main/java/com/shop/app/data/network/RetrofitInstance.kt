package com.shop.app.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // IMPORTANT: Configure the correct server address
    //
    // 1. If you're running the app on an Android emulator 
    //    and the server on the same computer, use this special IP address:
    private const val BASE_URL = "http://192.168.0.134:3000/" // 3000 is your Node.js server port
    //
    const val BASE_IMAGE_URL = BASE_URL
    // 2. If you're running on a real phone, you need to
    //    run ngrok and insert its https://... address here.
    // private const val BASE_URL = "https://your-ngrok-address.ngrok.io/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}