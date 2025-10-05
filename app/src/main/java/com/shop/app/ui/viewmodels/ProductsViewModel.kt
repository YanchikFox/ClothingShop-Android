package com.shop.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shop.app.data.model.Product
import com.shop.app.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface ProductsUiState {
    data class Success(val products: List<Product>) : ProductsUiState
    data object Error : ProductsUiState
    data object Loading : ProductsUiState
}

class ProductsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val uiState: StateFlow<ProductsUiState> = _uiState

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            _uiState.value = ProductsUiState.Loading
            try {
                val products = ServiceLocator.apiService.getProducts()
                _uiState.value = ProductsUiState.Success(products)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = ProductsUiState.Error
            }
        }
    }

    fun refreshProducts() {
        fetchProducts()
    }
}