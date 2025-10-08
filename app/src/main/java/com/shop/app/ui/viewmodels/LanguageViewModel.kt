package com.shop.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shop.app.R
import com.shop.app.localization.LanguageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LanguageOption(val languageTag: String?, val labelRes: Int)

data class LanguageUiState(
    val options: List<LanguageOption>,
    val selectedLanguageTag: String? = null,
)

class LanguageViewModel(private val repository: LanguageRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LanguageUiState(
            options = availableOptions,
            selectedLanguageTag = null,
        )
    )
    val uiState: StateFlow<LanguageUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.languageFlow.collect { languageTag ->
                _uiState.update { current ->
                    current.copy(selectedLanguageTag = languageTag)
                }
            }
        }
    }

    fun updateLanguage(languageTag: String?) {
        viewModelScope.launch {
            repository.setLanguage(languageTag)
        }
    }

    companion object {
        val availableOptions = listOf(
            LanguageOption(null, R.string.language_system_default),
            LanguageOption("en", R.string.language_english),
            LanguageOption("ru", R.string.language_russian),
            LanguageOption("uk", R.string.language_ukrainian),
            LanguageOption("pl", R.string.language_polish)
        )

        fun provideFactory(repository: LanguageRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(LanguageViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return LanguageViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}