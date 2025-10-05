package com.shop.app.data.repository

import com.shop.app.data.model.AuthRequest
import com.shop.app.data.model.AuthResponse
import com.shop.app.data.model.ProfileResponse
import com.shop.app.data.network.ApiService
import com.shop.app.di.ServiceLocator

class AuthRepository {
    private val apiService: ApiService = ServiceLocator.apiService

    suspend fun registerUser(authRequest: AuthRequest) {
        apiService.registerUser(authRequest)
    }

    suspend fun loginUser(authRequest: AuthRequest): AuthResponse {
        return apiService.loginUser(authRequest)
    }

    suspend fun getProfile(token: String): ProfileResponse {
        return apiService.getProfile(token)
    }

}