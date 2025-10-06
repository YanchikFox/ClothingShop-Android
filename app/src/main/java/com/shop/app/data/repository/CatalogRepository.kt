package com.shop.app.data.repository

import com.shop.app.data.model.Category
import com.shop.app.data.network.ApiService

class CatalogRepository(private val apiService: ApiService) {
    // Function that calls the API method to fetch categories
    suspend fun getCategories(): List<Category> {
        return apiService.getCategories()
    }
}