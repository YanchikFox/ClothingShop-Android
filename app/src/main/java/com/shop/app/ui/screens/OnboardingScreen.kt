package com.shop.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shop.app.R
import com.shop.app.ui.viewmodels.OnboardingViewModel.OnboardingOption
import com.shop.app.ui.viewmodels.OnboardingViewModel.OnboardingUiState
import kotlin.math.roundToInt

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onCategoryToggle: (String) -> Unit,
    onBrandToggle: (String) -> Unit,
    onPriceRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onContinueClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.onboarding_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.onboarding_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                OnboardingOptionsSection(
                    title = stringResource(R.string.onboarding_categories_title),
                    subtitle = stringResource(R.string.onboarding_categories_subtitle),
                    options = uiState.availableCategories,
                    selectedItems = uiState.selectedCategories,
                    onToggle = onCategoryToggle
                )
            }

            item {
                OnboardingOptionsSection(
                    title = stringResource(R.string.onboarding_brands_title),
                    subtitle = stringResource(R.string.onboarding_brands_subtitle),
                    options = uiState.availableBrands,
                    selectedItems = uiState.selectedBrands,
                    onToggle = onBrandToggle
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = stringResource(R.string.onboarding_price_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.onboarding_price_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val startPrice = uiState.selectedPriceRange.start.roundToInt()
                    val endPrice = uiState.selectedPriceRange.endInclusive.roundToInt()
                    Text(
                        text = stringResource(
                            id = R.string.onboarding_price_format,
                            startPrice,
                            endPrice
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    RangeSlider(
                        value = uiState.selectedPriceRange,
                        valueRange = uiState.priceRange,
                        onValueChange = onPriceRangeChange,
                        steps = 8,
                        enabled = !uiState.isSaving
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onContinueClick,
                        enabled = !uiState.isSaving
                    ) {
                        Text(text = stringResource(R.string.onboarding_continue))
                    }
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onSkipClick,
                        enabled = !uiState.isSaving
                    ) {
                        Text(text = stringResource(R.string.onboarding_skip))
                    }
                }
            }
            }

            if (uiState.isSaving) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun OnboardingOptionsSection(
    title: String,
    subtitle: String,
    options: List<OnboardingOption>,
    selectedItems: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                OnboardingOptionRow(
                    option = option,
                    isSelected = selectedItems.contains(option.id),
                    onToggle = onToggle
                )
            }
        }
    }
}

@Composable
private fun OnboardingOptionRow(
    option: OnboardingOption,
    isSelected: Boolean,
    onToggle: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = option.labelRes)
        )
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle(option.id) }
        )
    }
}
