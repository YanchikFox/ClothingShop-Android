package com.shop.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shop.app.ui.components.CategoryCard
import com.shop.app.ui.viewmodels.CatalogUiState
import com.shop.app.ui.viewmodels.CatalogViewModel

@Composable
fun CatalogScreen(
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit,
    catalogViewModel: CatalogViewModel = viewModel() // Get ViewModel instance
) {
    // Subscribe to UI state changes
    val uiState by catalogViewModel.uiState.collectAsState()

    // Render different UI based on state
    when (val state = uiState) {
        is CatalogUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is CatalogUiState.Success -> {
            // Render grid of categories
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // Three columns
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { onCategoryClick(category.id) }
                    )
                }
            }
        }
        is CatalogUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Failed to load categories")
            }
        }
    }
}