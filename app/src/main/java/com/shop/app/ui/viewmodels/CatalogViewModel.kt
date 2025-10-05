package com.shop.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shop.app.data.model.Category
import com.shop.app.data.repository.CatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Define states for our catalog screen
sealed interface CatalogUiState {
    data class Success(val categories: List<Category>) : CatalogUiState
    data object Error : CatalogUiState
    data object Loading : CatalogUiState
}

class CatalogViewModel : ViewModel() {
    private val repository = CatalogRepository()

    // StateFlow for storing UI state
    private val _uiState = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val uiState: StateFlow<CatalogUiState> = _uiState

    // The init block executes when ViewModel is created
    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        // Launch coroutine to perform network request in background thread
        viewModelScope.launch {
            _uiState.value = CatalogUiState.Loading // Immediately show loading indicator
            try {
                val categories = repository.getCategories()
                _uiState.value = CatalogUiState.Success(categories) // On success, pass data to UI
            } catch (e: Exception) {
                e.printStackTrace() // Print error to log for debugging
                _uiState.value = CatalogUiState.Error // On error, pass error state
            }
        }
    }

    fun refreshCategories() {
        fetchCategories()
    }
}