package com.shop.app.data.repository

import com.shop.app.data.model.PersonalRecommendationsRequest
import com.shop.app.data.model.Product
import com.shop.app.data.network.ApiService

class ProductRepository(private val apiService: ApiService) {

    suspend fun getProducts(
        gender: String? = null,
        categoryId: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        sortBy: String? = null,
        sortOrder: String? = null,
        limit: Int? = null,
        page: Int? = null,
    ): List<Product> {
        return apiService.getProducts(
            gender = gender,
            categoryId = categoryId,
            minPrice = minPrice,
            maxPrice = maxPrice,
            sortBy = sortBy,
            sortOrder = sortOrder,
            limit = limit,
            page = page,
        )
    }

    suspend fun searchProducts(query: String?, gender: String?, categoryId: String?): List<Product> {
        return apiService.searchProducts(query, gender, categoryId)
    }

    suspend fun getSimilarProducts(productId: String, limit: Int? = null): List<Product> {
        return apiService.getSimilarProducts(productId, limit).map { it.product }
    }

    suspend fun getPersonalRecommendations(
        categories: List<String>,
        brands: List<String>,
        priceRange: List<Double>?,
        limit: Int? = null,
    ): List<Product> {
        val request = PersonalRecommendationsRequest(
            categories = categories,
            brands = brands,
            priceRange = priceRange,
        )
        return apiService.getPersonalRecommendations(request, limit).map { it.product }
    }
}