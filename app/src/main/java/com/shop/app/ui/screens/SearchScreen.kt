package com.shop.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardActions
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shop.app.ui.components.ProductCard
import com.shop.app.ui.viewmodels.ProductsUiState
import com.shop.app.ui.viewmodels.SearchFilters
import com.shop.app.ui.viewmodels.SearchSortOption
import com.shop.app.ui.viewmodels.SearchViewModel

private data class CategoryFilterOption(val id: String?, val label: String)
private data class PriceFilterOption(val id: String, val label: String, val min: Double?, val max: Double?)
private data class SizeFilterOption(val id: String?, val label: String)

private val categoryFilterOptions = listOf(
    CategoryFilterOption(null, "Все"),
    CategoryFilterOption("female", "Женщины"),
    CategoryFilterOption("male", "Мужчины"),
    CategoryFilterOption("unisex", "Унисекс"),
)

private val priceFilterOptions = listOf(
    PriceFilterOption("all", "Любая", null, null),
    PriceFilterOption("lt1500", "До 1500 ₴", null, 1500.0),
    PriceFilterOption("1500_2500", "1500–2500 ₴", 1500.0, 2500.0),
    PriceFilterOption("gt2500", "От 2500 ₴", 2500.0, null),
)

private val sizeFilterOptions = listOf(
    SizeFilterOption(null, "Все"),
    SizeFilterOption("XS", "XS"),
    SizeFilterOption("S", "S"),
    SizeFilterOption("M", "M"),
    SizeFilterOption("L", "L"),
    SizeFilterOption("XL", "XL"),
    SizeFilterOption("38", "38"),
    SizeFilterOption("39", "39"),
    SizeFilterOption("40", "40"),
    SizeFilterOption("41", "41"),
    SizeFilterOption("42", "42"),
    SizeFilterOption("43", "43"),
)

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onProductClick: (String) -> Unit,
    searchViewModel: SearchViewModel = viewModel(),
) {
    val uiState by searchViewModel.uiState.collectAsState()
    val query by searchViewModel.query.collectAsState()
    val filters by searchViewModel.filters.collectAsState()
    val history by searchViewModel.searchHistory.collectAsState()
    val popularQueries by searchViewModel.popularQueries.collectAsState()

    val focusRequester = remember { FocusRequester() }

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { searchViewModel.onQueryChange(it) },
            label = { Text("Поиск") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { searchViewModel.onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { searchViewModel.retrySearch() }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .focusRequester(focusRequester)
        )

        FiltersSection(
            filters = filters,
            onCategorySelected = searchViewModel::selectCategory,
            onPriceSelected = searchViewModel::updatePriceRange,
            onSizeSelected = searchViewModel::selectSize,
            onSortSelected = searchViewModel::selectSortOption,
        )

        if (query.isBlank()) {
            SuggestionsSection(
                history = history,
                popularQueries = popularQueries,
                onSuggestionClick = searchViewModel::applySuggestion,
                onClearHistory = searchViewModel::clearHistory,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            when (val state = uiState) {
                is ProductsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is ProductsUiState.Success -> {
                    val products = state.products
                    val hasActiveFilters = filters.categoryId != null ||
                        filters.minPrice != null ||
                        filters.maxPrice != null ||
                        filters.size != null

                    if (products.isEmpty()) {
                        val message = if (query.isBlank() && !hasActiveFilters) {
                            "Начните вводить запрос или выберите фильтры"
                        } else {
                            "По заданным условиям ничего не найдено"
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(products) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product.id) }
                                )
                            }
                        }
                    }
                }

                is ProductsUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Не удалось загрузить результаты",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { searchViewModel.retrySearch() }) {
                            Text("Повторить")
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FiltersSection(
    filters: SearchFilters,
    onCategorySelected: (String?) -> Unit,
    onPriceSelected: (Double?, Double?) -> Unit,
    onSizeSelected: (String?) -> Unit,
    onSortSelected: (SearchSortOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text("Фильтры", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        Text("Категория", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
            categoryFilterOptions.forEach { option ->
                val isSelected = option.id?.let { filters.categoryId == it } ?: (filters.categoryId == null)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newValue = when {
                            option.id == null -> null
                            isSelected -> null
                            else -> option.id
                        }
                        onCategorySelected(newValue)
                    },
                    label = { Text(option.label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Цена", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
            priceFilterOptions.forEach { option ->
                val isSelected = option.min == filters.minPrice && option.max == filters.maxPrice
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) {
                            onPriceSelected(null, null)
                        } else {
                            onPriceSelected(option.min, option.max)
                        }
                    },
                    label = { Text(option.label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Размер", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
            sizeFilterOptions.forEach { option ->
                val isSelected = option.id?.let { filters.size == it } ?: (filters.size == null)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newSize = when {
                            option.id == null -> null
                            isSelected -> null
                            else -> option.id
                        }
                        onSizeSelected(newSize)
                    },
                    label = { Text(option.label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Сортировка", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
            SearchSortOption.values().forEach { option ->
                val isSelected = filters.sortOption == option
                FilterChip(
                    selected = isSelected,
                    onClick = { onSortSelected(option) },
                    label = { Text(option.label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SuggestionsSection(
    history: List<String>,
    popularQueries: List<String>,
    onSuggestionClick: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (history.isEmpty() && popularQueries.isEmpty()) {
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (history.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Недавние запросы", style = MaterialTheme.typography.labelLarge)
                TextButton(onClick = onClearHistory) {
                    Text("Очистить")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                history.forEach { term ->
                    SuggestionChip(
                        onClick = { onSuggestionClick(term) },
                        label = { Text(term) }
                    )
                }
            }
        }

        if (history.isNotEmpty() && popularQueries.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (popularQueries.isNotEmpty()) {
            Text("Популярно сейчас", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                popularQueries.forEach { term ->
                    SuggestionChip(
                        onClick = { onSuggestionClick(term) },
                        label = { Text(term) }
                    )
                }
            }
        }
    }
}
