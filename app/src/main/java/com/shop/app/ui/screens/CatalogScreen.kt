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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shop.app.data.model.Category
import com.shop.app.ui.components.CategoryCard
import com.shop.app.ui.viewmodels.CatalogUiState
import com.shop.app.ui.viewmodels.CatalogViewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import com.shop.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    modifier: Modifier = Modifier,
    catalogViewModel: CatalogViewModel,
    onCategoryClick: (String) -> Unit,
    imagesBaseUrl: String,
) {
    val uiState by catalogViewModel.uiState.collectAsState()
    val isRefreshing = uiState is CatalogUiState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { catalogViewModel.refreshCategories() },
        modifier = modifier.fillMaxSize()
    ) {
        when (val state = uiState) {
            is CatalogUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CatalogUiState.Success -> {
                val categoriesByParent = state.categories.groupBy { it.parentId }
                val rootCategories = categoriesByParent[null].orEmpty()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
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
                                imagesBaseUrl = imagesBaseUrl,
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
                                            onClick = { onCategoryClick(child.id) },
                                            imagesBaseUrl = imagesBaseUrl
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is CatalogUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.catalog_error_loading))
                }
            }
        }
    }
}

@Composable
private fun SubcategoryChip(
    category: Category,
    onClick: () -> Unit,
    imagesBaseUrl: String
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
                model = imagesBaseUrl + category.iconPath,
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