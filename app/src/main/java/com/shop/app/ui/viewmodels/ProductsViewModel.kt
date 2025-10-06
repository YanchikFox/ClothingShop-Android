package com.shop.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shop.app.data.model.Product
import com.shop.app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface ProductsUiState {
    data class Success(val products: List<Product>) : ProductsUiState
    data object Error : ProductsUiState
    data object Loading : ProductsUiState
}

class ProductsViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val uiState: StateFlow<ProductsUiState> = _uiState

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            _uiState.value = ProductsUiState.Loading
            try {
                val products = repository.getProducts(null)
                _uiState.value = ProductsUiState.Success(products)
            } catch (e: Exception) {
                _uiState.value = ProductsUiState.Error
            }
        }
    }

    fun refreshProducts() {
        fetchProducts()
    }

    companion object {
        fun provideFactory(repository: ProductRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return ProductsViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}