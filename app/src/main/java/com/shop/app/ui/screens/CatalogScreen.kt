package com.shop.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.shop.app.data.model.Category
import com.shop.app.di.ServiceLocator
import com.shop.app.ui.components.CategoryCard
import com.shop.app.ui.viewmodels.CatalogUiState
import com.shop.app.ui.viewmodels.CatalogViewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import com.shop.app.R

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
            val categoriesByParent = state.categories.groupBy { it.parentId }
            val rootCategories = categoriesByParent[null].orEmpty()

            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(rootCategories) { category ->
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        CategoryCard(
                            category = category,
                            modifier = Modifier.fillMaxWidth().height(180.dp),
                            onClick = { onCategoryClick(category.id) }
                        )

                        val children = categoriesByParent[category.id].orEmpty()
                        if (children.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(end = 16.dp)
                            ) {
                                items(children) { child ->
                                    SubcategoryChip(
                                        category = child,
                                        onClick = { onCategoryClick(child.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        is CatalogUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.catalog_error_loading))
            }
        }
    }
}

@Composable
private fun SubcategoryChip(
    category: Category,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ServiceLocator.imagesBaseUrl + category.iconPath,
                contentDescription = category.name,
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}