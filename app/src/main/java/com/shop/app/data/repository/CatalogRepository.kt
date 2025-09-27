package com.shop.app.data.repository

import com.shop.app.data.model.Category
import com.shop.app.data.network.ApiService
import com.shop.app.data.network.RetrofitInstance

class CatalogRepository {
    // Get access to our configured ApiService
    private val apiService: ApiService = RetrofitInstance.api

    // Function that calls the API method to fetch categories
    suspend fun getCategories(): List<Category> {
        return apiService.getCategories()
    }
}