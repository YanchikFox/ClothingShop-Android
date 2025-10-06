package com.shop.app.data.repository

import com.shop.app.data.model.AuthRequest
import com.shop.app.data.model.AuthResponse
import com.shop.app.data.model.ProfileResponse
import com.shop.app.data.model.ProfileUpdateRequest
import com.shop.app.data.network.ApiService

class UserRepository(private val apiService: ApiService) {

    suspend fun registerUser(request: AuthRequest) {
        apiService.registerUser(request)
    }

    suspend fun loginUser(request: AuthRequest): AuthResponse {
        return apiService.loginUser(request)
    }

    suspend fun getProfile(): ProfileResponse {
        return apiService.getProfile()
    }

    suspend fun updateProfile(request: ProfileUpdateRequest): ProfileResponse {
        return apiService.updateProfile(request)
    }
}