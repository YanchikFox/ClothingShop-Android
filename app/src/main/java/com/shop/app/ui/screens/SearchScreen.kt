package com.shop.app.ui.screens

import androidx.annotation.StringRes
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import com.shop.app.ui.components.ProductCard
import com.shop.app.ui.viewmodels.ProductsUiState
import com.shop.app.ui.viewmodels.SearchFilters
import com.shop.app.ui.viewmodels.SearchSortOption
import com.shop.app.ui.viewmodels.SearchViewModel
import com.shop.app.R

private data class CategoryFilterOption(val id: String?, @StringRes val labelRes: Int)
private data class PriceFilterOption(
    val id: String,
    @StringRes val labelRes: Int,
    val min: Double?,
    val max: Double?
)
private data class SizeFilterOption(val id: String?, @StringRes val labelRes: Int)

private val categoryFilterOptions = listOf(
    CategoryFilterOption(null, R.string.search_filter_category_all),
    CategoryFilterOption("female", R.string.search_filter_category_women),
    CategoryFilterOption("male", R.string.search_filter_category_men),
    CategoryFilterOption("unisex", R.string.search_filter_category_unisex),
)

private val priceFilterOptions = listOf(
    PriceFilterOption("all", R.string.search_filter_price_all, null, null),
    PriceFilterOption("lt1500", R.string.search_filter_price_under_1500, null, 1500.0),
    PriceFilterOption("1500_2500", R.string.search_filter_price_1500_2500, 1500.0, 2500.0),
    PriceFilterOption("gt2500", R.string.search_filter_price_over_2500, 2500.0, null),
)

private val sizeFilterOptions = listOf(
    SizeFilterOption(null, R.string.search_filter_size_all),
    SizeFilterOption("XS", R.string.size_xs),
    SizeFilterOption("S", R.string.size_s),
    SizeFilterOption("M", R.string.size_m),
    SizeFilterOption("L", R.string.size_l),
    SizeFilterOption("XL", R.string.size_xl),
    SizeFilterOption("38", R.string.size_38),
    SizeFilterOption("39", R.string.size_39),
    SizeFilterOption("40", R.string.size_40),
    SizeFilterOption("41", R.string.size_41),
    SizeFilterOption("42", R.string.size_42),
    SizeFilterOption("43", R.string.size_43),
)

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    languageTag: String?,
    formatPrice: (Double) -> String,
    onProductClick: (String) -> Unit,
    searchViewModel: SearchViewModel = viewModel(),
) {
    val uiState by searchViewModel.uiState.collectAsState()
    val query by searchViewModel.query.collectAsState()
    val filters by searchViewModel.filters.collectAsState()
    val history by searchViewModel.searchHistory.collectAsState()
    val popularQueries by searchViewModel.popularQueries.collectAsState()

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(languageTag) {
        searchViewModel.onLanguageChanged()
    }

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { searchViewModel.onQueryChange(it) },
            label = { Text(stringResource(R.string.search_field_label)) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.cd_search)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { searchViewModel.onQueryChange("") }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.cd_clear_search)
                        )
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
                            stringResource(R.string.search_empty_prompt)
                        } else {
                            stringResource(R.string.search_empty_filtered)
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
                                    formatPrice = formatPrice,
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
                            text = stringResource(R.string.search_error_message),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { searchViewModel.retrySearch() }) {
                            Text(stringResource(R.string.search_retry))
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
        Text(stringResource(R.string.search_filters_title), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        Text(stringResource(R.string.search_filters_category), style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                    label = { Text(stringResource(id = option.labelRes)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.search_filters_price), style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                    label = { Text(stringResource(id = option.labelRes)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.search_filters_size), style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                    label = { Text(stringResource(id = option.labelRes)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.search_filters_sort), style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SearchSortOption.values().forEach { option ->
                val isSelected = filters.sortOption == option
                FilterChip(
                    selected = isSelected,
                    onClick = { onSortSelected(option) },
                    label = { Text(stringResource(id = option.labelRes)) }
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
    popularQueries: List<Int>,
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
                Text(
                    stringResource(R.string.search_recent_queries_title),
                    style = MaterialTheme.typography.labelLarge
                )
                TextButton(onClick = onClearHistory) {
                    Text(stringResource(R.string.search_clear_history))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
            Text(
                stringResource(R.string.search_popular_title),
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            val localizedPopularQueries = popularQueries.map { stringResource(id = it) }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                localizedPopularQueries.forEach { term ->
                    SuggestionChip(
                        onClick = { onSuggestionClick(term) },
                        label = { Text(term) }
                    )
                }
            }
        }
    }
}
