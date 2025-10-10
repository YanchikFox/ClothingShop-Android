package com.shop.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shop.app.data.model.Product
import com.shop.app.data.preferences.OnboardingPreferencesDataStore.OnboardingPreferences
import com.shop.app.data.repository.OnboardingPreferencesRepository
import com.shop.app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed interface ProductsUiState {
    data class Success(
        val products: List<Product>,
        val personalRecommendations: List<Product> = emptyList(),
        val popularProducts: List<Product> = emptyList(),
    ) : ProductsUiState
    data object Error : ProductsUiState
    data object Loading : ProductsUiState
}

class ProductsViewModel(
    private val repository: ProductRepository,
    private val onboardingPreferencesRepository: OnboardingPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val uiState: StateFlow<ProductsUiState> = _uiState

    private var latestPreferences: OnboardingPreferences? = null

    init {
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            onboardingPreferencesRepository.preferencesFlow.collectLatest { preferences ->
                latestPreferences = preferences
                loadHomeSections(preferences)
            }
        }
    }

    private suspend fun loadHomeSections(preferences: OnboardingPreferences?) {
        _uiState.value = ProductsUiState.Loading
        try {
            val newArrivals = repository.getProducts(
                sortBy = "newest",
                limit = HOME_SECTION_LIMIT,
            )

            val personalRecommendations = loadPersonalRecommendations(preferences)

            val popularProducts = if (personalRecommendations.isEmpty()) {
                loadPopularProducts()
            } else {
                emptyList()
            }

            _uiState.value = ProductsUiState.Success(
                products = newArrivals,
                personalRecommendations = personalRecommendations,
                popularProducts = popularProducts,
            )
        } catch (e: Exception) {
            _uiState.value = ProductsUiState.Error
        }
    }

    private suspend fun loadPersonalRecommendations(
        preferences: OnboardingPreferences?,
    ): List<Product> {
        if (preferences == null) {
            return emptyList()
        }

        return try {
            val priceRange = listOf(preferences.minPrice.toDouble(), preferences.maxPrice.toDouble())
            repository.getPersonalRecommendations(
                categories = preferences.selectedCategories.toList(),
                brands = preferences.selectedBrands.toList(),
                priceRange = priceRange,
                limit = HOME_SECTION_LIMIT,
            )
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun loadPopularProducts(): List<Product> {
        return try {
            repository.getProducts(
                sortBy = "bestseller",
                limit = HOME_SECTION_LIMIT,
            )
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun refreshProducts() {
        viewModelScope.launch {
            loadHomeSections(latestPreferences)
        }
    }

    companion object {
        private const val HOME_SECTION_LIMIT = 12

        fun provideFactory(
            repository: ProductRepository,
            onboardingPreferencesRepository: OnboardingPreferencesRepository,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return ProductsViewModel(repository, onboardingPreferencesRepository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}