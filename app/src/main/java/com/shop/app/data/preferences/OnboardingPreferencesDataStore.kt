package com.shop.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val ONBOARDING_DATA_STORE = "onboarding_preferences"

private val Context.onboardingDataStore by preferencesDataStore(name = ONBOARDING_DATA_STORE)

object OnboardingPreferencesDataStore {
    private val KEY_SELECTED_CATEGORIES = stringSetPreferencesKey("selected_categories")
    private val KEY_SELECTED_BRANDS = stringSetPreferencesKey("selected_brands")
    private val KEY_MIN_PRICE = floatPreferencesKey("min_price")
    private val KEY_MAX_PRICE = floatPreferencesKey("max_price")
    private val KEY_COMPLETED = booleanPreferencesKey("completed")

    data class OnboardingPreferences(
        val selectedCategories: Set<String>,
        val selectedBrands: Set<String>,
        val minPrice: Float,
        val maxPrice: Float,
        val completed: Boolean
    )

    fun preferencesFlow(context: Context): Flow<OnboardingPreferences?> {
        return context.onboardingDataStore.data.map { preferences ->
            preferences.toOnboardingPreferences()
        }
    }

    suspend fun setPreferences(
        context: Context,
        selectedCategories: Set<String>,
        selectedBrands: Set<String>,
        minPrice: Float,
        maxPrice: Float,
        completed: Boolean
    ) {
        context.onboardingDataStore.edit { prefs ->
            prefs[KEY_SELECTED_CATEGORIES] = selectedCategories
            prefs[KEY_SELECTED_BRANDS] = selectedBrands
            prefs[KEY_MIN_PRICE] = minPrice
            prefs[KEY_MAX_PRICE] = maxPrice
            prefs[KEY_COMPLETED] = completed
        }
    }

    suspend fun resetOnboarding(context: Context) {
        context.onboardingDataStore.edit { prefs ->
            prefs.clear()
        }
    }

    private fun Preferences.toOnboardingPreferences(): OnboardingPreferences? {
        val isCompleted = this[KEY_COMPLETED] ?: false
        if (!isCompleted) {
            return null
        }
        val min = this[KEY_MIN_PRICE] ?: DEFAULT_MIN_PRICE
        val max = this[KEY_MAX_PRICE] ?: DEFAULT_MAX_PRICE
        return OnboardingPreferences(
            selectedCategories = this[KEY_SELECTED_CATEGORIES] ?: emptySet(),
            selectedBrands = this[KEY_SELECTED_BRANDS] ?: emptySet(),
            minPrice = min,
            maxPrice = max,
            completed = isCompleted
        )
    }

    const val DEFAULT_MIN_PRICE = 0f
    const val DEFAULT_MAX_PRICE = 500f
}
