package com.shop.app.currency

import android.content.Context

class CurrencyRepository(private val context: Context) {
    val currencyFlow = CurrencyPreferencesDataStore.currencyFlow(context)

    suspend fun setCurrency(code: String) {
        CurrencyPreferencesDataStore.setCurrency(context, code)
    }
}
