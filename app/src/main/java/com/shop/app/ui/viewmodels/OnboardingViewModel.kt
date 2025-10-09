package com.shop.app.ui.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shop.app.R
import com.shop.app.data.preferences.OnboardingPreferencesDataStore.DEFAULT_MAX_PRICE
import com.shop.app.data.preferences.OnboardingPreferencesDataStore.DEFAULT_MIN_PRICE
import com.shop.app.data.preferences.OnboardingPreferencesDataStore.OnboardingPreferences
import com.shop.app.data.repository.OnboardingPreferencesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val onboardingPreferencesRepository: OnboardingPreferencesRepository
) : ViewModel() {

    private val defaultCategories = listOf(
        OnboardingOption("women", R.string.onboarding_category_women),
        OnboardingOption("men", R.string.onboarding_category_men),
        OnboardingOption("unisex", R.string.onboarding_category_unisex),
        OnboardingOption("kids", R.string.onboarding_category_kids)
    )

    private val defaultBrands = listOf(
        OnboardingOption("acme", R.string.onboarding_brand_acme),
        OnboardingOption("nord", R.string.onboarding_brand_nord),
        OnboardingOption("aurora", R.string.onboarding_brand_aurora),
        OnboardingOption("vertex", R.string.onboarding_brand_vertex)
    )

    private val _uiState = MutableStateFlow(
        OnboardingUiState(
            availableCategories = defaultCategories,
            availableBrands = defaultBrands,
            priceRange = DEFAULT_MIN_PRICE..DEFAULT_MAX_PRICE,
            selectedPriceRange = DEFAULT_MIN_PRICE..DEFAULT_MAX_PRICE
        )
    )
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    val completionState: StateFlow<OnboardingCompletionState> =
        onboardingPreferencesRepository.preferencesFlow
            .map { prefs -> prefs.toCompletionState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = OnboardingCompletionState.Loading
            )

    private val _events = MutableSharedFlow<OnboardingEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            onboardingPreferencesRepository.preferencesFlow.collect { prefs ->
                if (prefs != null) {
                    _uiState.value = _uiState.value.copy(
                        selectedCategories = prefs.selectedCategories,
                        selectedBrands = prefs.selectedBrands,
                        selectedPriceRange = prefs.minPrice..prefs.maxPrice
                    )
                }
            }
        }
    }

    fun toggleCategory(categoryId: String) {
        if (_uiState.value.isSaving) return
        _uiState.value = _uiState.value.copy(
            selectedCategories = _uiState.value.selectedCategories.toggle(categoryId)
        )
    }

    fun toggleBrand(brandId: String) {
        if (_uiState.value.isSaving) return
        _uiState.value = _uiState.value.copy(
            selectedBrands = _uiState.value.selectedBrands.toggle(brandId)
        )
    }

    fun updatePriceRange(range: ClosedFloatingPointRange<Float>) {
        if (_uiState.value.isSaving) return
        _uiState.value = _uiState.value.copy(selectedPriceRange = range)
    }

    fun savePreferences() {
        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.value = currentState.copy(isSaving = true)
            onboardingPreferencesRepository.savePreferences(
                selectedCategories = currentState.selectedCategories,
                selectedBrands = currentState.selectedBrands,
                minPrice = currentState.selectedPriceRange.start,
                maxPrice = currentState.selectedPriceRange.endInclusive
            )
            _uiState.value = currentState.copy(isSaving = false)
            _events.emit(OnboardingEvent.Completed)
        }
    }

    fun skipOnboarding() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            onboardingPreferencesRepository.savePreferences(
                selectedCategories = emptySet(),
                selectedBrands = emptySet(),
                minPrice = DEFAULT_MIN_PRICE,
                maxPrice = DEFAULT_MAX_PRICE
            )
            _uiState.value = _uiState.value.copy(isSaving = false)
            _events.emit(OnboardingEvent.Completed)
        }
    }

    private fun Set<String>.toggle(id: String): Set<String> {
        return if (contains(id)) {
            toMutableSet().apply { remove(id) }
        } else {
            toMutableSet().apply { add(id) }
        }
    }

    private fun OnboardingPreferences?.toCompletionState(): OnboardingCompletionState {
        return when {
            this == null -> OnboardingCompletionState.NotCompleted
            else -> OnboardingCompletionState.Completed(this)
        }
    }

    data class OnboardingOption(val id: String, @StringRes val labelRes: Int)

    data class OnboardingUiState(
        val availableCategories: List<OnboardingOption>,
        val availableBrands: List<OnboardingOption>,
        val priceRange: ClosedFloatingPointRange<Float>,
        val selectedPriceRange: ClosedFloatingPointRange<Float>,
        val selectedCategories: Set<String> = emptySet(),
        val selectedBrands: Set<String> = emptySet(),
        val isSaving: Boolean = false
    )

    sealed interface OnboardingCompletionState {
        data object Loading : OnboardingCompletionState
        data object NotCompleted : OnboardingCompletionState
        data class Completed(val preferences: OnboardingPreferences) : OnboardingCompletionState
    }

    sealed interface OnboardingEvent {
        data object Completed : OnboardingEvent
    }

    companion object {
        fun provideFactory(
            repository: OnboardingPreferencesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(OnboardingViewModel::class.java))
                return OnboardingViewModel(repository) as T
            }
        }
    }
}
