package com.shop.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.shop.app.di.ServiceLocator
import com.shop.app.ui.components.ProductCard
import com.shop.app.ui.viewmodels.ProductListViewModel
import com.shop.app.ui.viewmodels.ProductsUiState
import androidx.compose.ui.res.stringResource
import com.shop.app.R

@Composable
fun ProductListScreen(
    modifier: Modifier = Modifier,
    languageTag: String?,
    formatPrice: (Double) -> String,
    onProductClick: (String) -> Unit,
    productListViewModel: ProductListViewModel = viewModel()
) {
    val uiState by productListViewModel.uiState.collectAsState()
    val filters by productListViewModel.filters.collectAsState()
    val selectedCategoryId by productListViewModel.selectedCategoryId.collectAsState()

    val skipFirstRefresh = remember { mutableStateOf(true) }
    LaunchedEffect(languageTag) {
        if (skipFirstRefresh.value) {
            skipFirstRefresh.value = false
        } else {
            productListViewModel.refreshForLanguageChange()
        }
    }

    when (val state = uiState) {
        is ProductsUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ProductsUiState.Success -> {
            Column(modifier = modifier.fillMaxSize()) {
                if (filters.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        items(filters) { category ->
                            FilterChip(
                                selected = category.id == selectedCategoryId,
                                onClick = {
                                    productListViewModel.onFilterSelected(category.id)
                                },
                                label = { Text(category.name) },
                                leadingIcon = {
                                    AsyncImage(
                                        model = ServiceLocator.imagesBaseUrl + category.iconPath,
                                        contentDescription = category.name,
                                        modifier = Modifier.size(24.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            )
                        }
                    }
                }

                val contentModifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)

                if (state.products.isEmpty()) {
                    Box(
                        modifier = contentModifier,
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.product_list_empty_message))
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = contentModifier,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.products) { product ->
                            ProductCard(
                                product = product,
                                formatPrice = formatPrice,
                                onClick = { onProductClick(product.id) }
                            )
                        }
                    }
                }
            }
        }
        is ProductsUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.error_loading_products))
            }
        }
    }
}