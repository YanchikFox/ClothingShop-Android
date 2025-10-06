package com.shop.app.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shop.app.data.model.Category
import com.shop.app.data.repository.CatalogRepository
import com.shop.app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductListViewModel(
    savedStateHandle: SavedStateHandle,
    private val catalogRepository: CatalogRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val initialCategoryId: String? = savedStateHandle.get("categoryId")

    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val uiState: StateFlow<ProductsUiState> = _uiState

    private val _filters = MutableStateFlow<List<Category>>(emptyList())
    val filters: StateFlow<List<Category>> = _filters

    private val _selectedCategoryId = MutableStateFlow(initialCategoryId)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId

    init {
        loadFilters()
        fetchProductsByCategory(initialCategoryId)
    }

    private fun loadFilters() {
        viewModelScope.launch {
            try {
                val categories = catalogRepository.getCategories()
                val selectedCategory = categories.find { it.id == initialCategoryId }
                val parentCategoryId = selectedCategory?.parentId ?: initialCategoryId

                val parentCategory = categories.find { it.id == parentCategoryId }
                val childCategories = categories.filter { it.parentId == parentCategoryId }

                val filters = buildList {
                    parentCategory?.let { add(it) }
                    addAll(childCategories)
                }
                _filters.value = filters
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onFilterSelected(categoryId: String) {
        if (_selectedCategoryId.value == categoryId) return
        _selectedCategoryId.value = categoryId
        fetchProductsByCategory(categoryId)
    }

    private fun fetchProductsByCategory(categoryId: String?) {
        viewModelScope.launch {
            _uiState.value = ProductsUiState.Loading
            try {
                val products = productRepository.getProducts(gender = categoryId)
                _uiState.value = ProductsUiState.Success(products)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = ProductsUiState.Error
            }
        }
    }

    fun refreshForLanguageChange() {
        loadFilters()
        fetchProductsByCategory(_selectedCategoryId.value)
    }

    companion object {
        fun provideFactory(
            catalogRepository: CatalogRepository,
            productRepository: ProductRepository,
            savedStateHandle: SavedStateHandle
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProductListViewModel(savedStateHandle, catalogRepository, productRepository) as T
                }
            }
        }
    }
}