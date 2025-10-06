package com.shop.app.currency

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private const val CURRENCY_DATA_STORE = "currency_settings"
const val DEFAULT_CURRENCY_CODE = "UAH"

private val Context.currencyDataStore by preferencesDataStore(name = CURRENCY_DATA_STORE)
private val CURRENCY_KEY = stringPreferencesKey("app_currency")

object CurrencyPreferencesDataStore {

    fun currencyFlow(context: Context): Flow<String> =
        context.currencyDataStore.data.map { preferences ->
            preferences[CURRENCY_KEY] ?: DEFAULT_CURRENCY_CODE
        }

    suspend fun setCurrency(context: Context, currencyCode: String) {
        context.currencyDataStore.edit { prefs ->
            prefs[CURRENCY_KEY] = currencyCode
        }
    }

    suspend fun currentCurrency(context: Context): String =
        currencyFlow(context).firstOrNull() ?: DEFAULT_CURRENCY_CODE
}
