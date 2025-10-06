package com.shop.app.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shop.app.R
import com.shop.app.currency.CurrencyRepository
import com.shop.app.currency.DEFAULT_CURRENCY_CODE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val DEFAULT_CONVERSION_RATES = mapOf(
    DEFAULT_CURRENCY_CODE to 1.0,
    "USD" to 0.026,
    "EUR" to 0.024
)

private val availableOptions = listOf(
    CurrencyOption(DEFAULT_CURRENCY_CODE, R.string.currency_uah),
    CurrencyOption("USD", R.string.currency_usd),
    CurrencyOption("EUR", R.string.currency_eur)
)

data class CurrencyOption(val code: String, val labelRes: Int)

data class CurrencyUiState(
    val options: List<CurrencyOption>,
    val selectedCurrencyCode: String = DEFAULT_CURRENCY_CODE,
    val conversionRates: Map<String, Double> = DEFAULT_CONVERSION_RATES
)

class CurrencyViewModel(private val repository: CurrencyRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CurrencyUiState(
            options = availableOptions,
            selectedCurrencyCode = DEFAULT_CURRENCY_CODE,
            conversionRates = DEFAULT_CONVERSION_RATES
        )
    )
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.currencyFlow.collect { currencyCode ->
                _uiState.update { current ->
                    current.copy(selectedCurrencyCode = currencyCode)
                }
            }
        }
    }

    fun updateCurrency(currencyCode: String) {
        viewModelScope.launch {
            repository.setCurrency(currencyCode)
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory {
            val appContext = context.applicationContext
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(CurrencyViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return CurrencyViewModel(CurrencyRepository(appContext)) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
