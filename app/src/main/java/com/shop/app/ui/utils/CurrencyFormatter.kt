package com.shop.app.ui.utils

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import com.shop.app.ui.viewmodels.CurrencyUiState
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@Composable
fun rememberPriceFormatter(currencyUiState: CurrencyUiState): (Double) -> String {
    val configuration = LocalConfiguration.current
    val locale = remember(configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            configuration.locale
        }
    } ?: Locale.getDefault()

    return remember(currencyUiState.selectedCurrencyCode, currencyUiState.conversionRates, locale) {
        val formatter = NumberFormat.getCurrencyInstance(locale)
        runCatching { formatter.currency = Currency.getInstance(currencyUiState.selectedCurrencyCode) }
        formatter.maximumFractionDigits = 0
        val rate = currencyUiState.conversionRates[currencyUiState.selectedCurrencyCode] ?: 1.0
        { amount -> formatter.format(amount * rate) }
    }
}
