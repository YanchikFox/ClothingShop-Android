package com.shop.app.data.repository

import com.shop.app.data.model.Product
import com.shop.app.data.network.ApiService

class ProductRepository(private val apiService: ApiService) {

    suspend fun getProducts(gender: String?): List<Product> {
        return apiService.getProducts(gender)
    }

    suspend fun searchProducts(query: String?, gender: String?): List<Product> {
        return apiService.searchProducts(query, gender)
    }
}