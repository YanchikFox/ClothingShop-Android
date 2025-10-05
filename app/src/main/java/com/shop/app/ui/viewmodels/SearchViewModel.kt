package com.shop.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shop.app.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SEARCH_DEBOUNCE_DELAY = 300L

data class SearchFilters(
    val categoryId: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val size: String? = null,
    val sortOption: SearchSortOption = SearchSortOption.Relevance,
)

enum class SearchSortOption(val label: String, val sortBy: String?, val sortOrder: String?) {
    Relevance("По релевантности", null, null),
    PriceLowToHigh("Сначала дешевые", "price", "asc"),
    PriceHighToLow("Сначала дорогие", "price", "desc"),
    Newest("Новинки", "created_at", "desc"),
    Name("По алфавиту", "name", "asc");
}

class SearchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Success(emptyList()))
    val uiState: StateFlow<ProductsUiState> = _uiState

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _filters = MutableStateFlow(SearchFilters())
    val filters: StateFlow<SearchFilters> = _filters

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory

    private val _popularQueries = MutableStateFlow(
        listOf("Футболка", "Джинсы", "Кроссовки", "Платье", "Куртка")
    )
    val popularQueries: StateFlow<List<String>> = _popularQueries

    init {
        viewModelScope.launch {
            combine(_query, _filters) { currentQuery, currentFilters ->
                currentQuery to currentFilters
            }
                .debounce(SEARCH_DEBOUNCE_DELAY)
                .collectLatest { (currentQuery, currentFilters) ->
                    executeSearch(currentQuery, currentFilters)
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun selectCategory(categoryId: String?) {
        _filters.update { it.copy(categoryId = categoryId) }
    }

    fun updatePriceRange(minPrice: Double?, maxPrice: Double?) {
        _filters.update { it.copy(minPrice = minPrice, maxPrice = maxPrice) }
    }

    fun selectSize(size: String?) {
        _filters.update { it.copy(size = size) }
    }

    fun selectSortOption(option: SearchSortOption) {
        if (_filters.value.sortOption == option) {
            return
        }

        _filters.update { it.copy(sortOption = option) }
        viewModelScope.launch {
            executeSearch(_query.value, _filters.value)
        }
    }

    fun clearHistory() {
        _searchHistory.value = emptyList()
    }

    fun applySuggestion(suggestion: String) {
        _query.value = suggestion
        viewModelScope.launch {
            executeSearch(suggestion, _filters.value)
        }
    }

    fun retrySearch() {
        viewModelScope.launch {
            executeSearch(_query.value, _filters.value)
        }
    }

    private suspend fun executeSearch(query: String, filters: SearchFilters) {
        val normalizedQuery = query.trim().ifEmpty { null }
        val hasActiveFilters = filters.categoryId != null ||
            filters.minPrice != null ||
            filters.maxPrice != null ||
            filters.size != null

        if (normalizedQuery == null && !hasActiveFilters) {
            _uiState.value = ProductsUiState.Success(emptyList())
            return
        }

        _uiState.value = ProductsUiState.Loading
        try {
            val results = ServiceLocator.apiService.searchProducts(
                query = normalizedQuery,
                gender = filters.categoryId
            )
            _uiState.value = ProductsUiState.Success(results)
            normalizedQuery?.let { addToHistory(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.value = ProductsUiState.Error
        }
    }

    private fun addToHistory(query: String) {
        _searchHistory.update { history ->
            val deduplicated = history.filterNot { it.equals(query, ignoreCase = true) }
            (listOf(query) + deduplicated).take(10)
        }
    }
}
