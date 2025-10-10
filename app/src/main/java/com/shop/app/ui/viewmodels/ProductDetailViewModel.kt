package com.shop.app.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shop.app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val productId: String? = savedStateHandle.get<String>(PRODUCT_ID_KEY)

    private val _similarUiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val similarUiState: StateFlow<ProductsUiState> = _similarUiState

    init {
        fetchSimilarProducts()
    }

    fun fetchSimilarProducts(limit: Int = SIMILAR_SECTION_LIMIT) {
        val id = productId
        if (id.isNullOrBlank()) {
            _similarUiState.value = ProductsUiState.Success(emptyList())
            return
        }

        viewModelScope.launch {
            _similarUiState.value = ProductsUiState.Loading
            try {
                val similarProducts = productRepository.getSimilarProducts(id, limit)
                _similarUiState.value = ProductsUiState.Success(similarProducts)
            } catch (error: Exception) {
                _similarUiState.value = ProductsUiState.Error
            }
        }
    }

    companion object {
        private const val SIMILAR_SECTION_LIMIT = 12
        private const val PRODUCT_ID_KEY = "productId"

        fun provideFactory(
            productRepository: ProductRepository,
            savedStateHandle: SavedStateHandle,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(ProductDetailViewModel::class.java))
                    return ProductDetailViewModel(savedStateHandle, productRepository) as T
                }
            }
        }
    }
}
