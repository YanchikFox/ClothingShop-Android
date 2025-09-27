package com.shop.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shop.app.data.network.RetrofitInstance
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Use the familiar state for product list
// sealed interface ProductsUiState { ... }

class SearchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Success(emptyList()))
    val uiState: StateFlow<ProductsUiState> = _uiState

    // Job for tracking current search request
    private var searchJob: Job? = null

    fun searchProducts(query: String) {
        // Cancel previous search request if it exists
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.value = ProductsUiState.Success(emptyList())
            return
        }

        // Start a new request with a small delay (debounce)
        // This is needed so we don't send a request for every letter typed
        searchJob = viewModelScope.launch {
            delay(300L) // Wait 300 milliseconds
            _uiState.value = ProductsUiState.Loading
            try {
                val results = RetrofitInstance.api.searchProducts(query)
                _uiState.value = ProductsUiState.Success(results)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = ProductsUiState.Error
            }
        }
    }
}