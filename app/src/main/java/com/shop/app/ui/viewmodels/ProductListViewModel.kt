package com.shop.app.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shop.app.data.model.Product
import com.shop.app.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Use the same state class as the main screen
// To avoid code duplication, it can be moved to a separate file,
// but for now we'll leave it like this for simplicity.
// sealed interface ProductsUiState { ... }

class ProductListViewModel(
    savedStateHandle: SavedStateHandle // Object for getting navigation arguments
) : ViewModel() {

    // Get category ID from navigation arguments
    private val categoryId: String? = savedStateHandle.get("categoryId")

    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val uiState: StateFlow<ProductsUiState> = _uiState

    init {
        fetchProductsByCategory()
    }

    private fun fetchProductsByCategory() {
        viewModelScope.launch {
            _uiState.value = ProductsUiState.Loading
            try {
                // Call our updated method, passing the category ID to it
                val products = ServiceLocator.apiService.getProducts(gender = categoryId)
                _uiState.value = ProductsUiState.Success(products)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = ProductsUiState.Error
            }
        }
    }
}