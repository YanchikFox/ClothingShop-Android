package com.shop.app.data.repository

import android.content.Context
import com.shop.app.data.preferences.OnboardingPreferencesDataStore
import com.shop.app.data.preferences.OnboardingPreferencesDataStore.OnboardingPreferences
import kotlinx.coroutines.flow.Flow

class OnboardingPreferencesRepository(private val context: Context) {

    val preferencesFlow: Flow<OnboardingPreferences?> =
        OnboardingPreferencesDataStore.preferencesFlow(context)

    suspend fun savePreferences(
        selectedCategories: Set<String>,
        selectedBrands: Set<String>,
        minPrice: Float,
        maxPrice: Float
    ) {
        OnboardingPreferencesDataStore.setPreferences(
            context = context,
            selectedCategories = selectedCategories,
            selectedBrands = selectedBrands,
            minPrice = minPrice,
            maxPrice = maxPrice,
            completed = true
        )
    }
}
