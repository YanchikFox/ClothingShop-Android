package com.shop.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shop.app.ui.components.ProductRow
import com.shop.app.ui.components.PromoBanner
import com.shop.app.ui.components.SearchBar
import com.shop.app.ui.components.SectionTitle
import com.shop.app.ui.viewmodels.ProductsUiState

@Composable
fun HomeScreen(
    uiState: ProductsUiState,
    modifier: Modifier = Modifier,
    onProductClick: (String) -> Unit,
    onSearchClick: () -> Unit // This parameter is received from MainActivity
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            // Pass the received onSearchClick action to our SearchBar
            SearchBar(onClick = onSearchClick)
        }
        item { PromoBanner() }

        when (val state = uiState) {
            is ProductsUiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is ProductsUiState.Success -> {
                item { SectionTitle(title = "New Arrivals") }
                item {
                    ProductRow(
                        products = state.products,
                        onProductClick = onProductClick
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }

                item { SectionTitle(title = "You Might Like") }
                item {
                    ProductRow(
                        products = state.products.shuffled(),
                        onProductClick = onProductClick
                    )
                }
            }
            is ProductsUiState.Error -> {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Failed to load products")
                    }
                }
            }
        }
    }
}