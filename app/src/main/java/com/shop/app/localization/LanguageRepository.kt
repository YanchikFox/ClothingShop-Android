package com.shop.app.localization

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val LANGUAGE_DATA_STORE = "language_settings"
private val Context.languageDataStore by preferencesDataStore(name = LANGUAGE_DATA_STORE)

class LanguageRepository(private val context: Context) {

    private val languageKey = stringPreferencesKey("app_language")

    val languageFlow: Flow<String?> = context.languageDataStore.data.map { preferences: Preferences ->
        preferences[languageKey]
    }

    suspend fun setLanguage(languageTag: String?) {
        context.languageDataStore.edit { prefs ->
            if (languageTag.isNullOrBlank()) {
                prefs.remove(languageKey)
            } else {
                prefs[languageKey] = languageTag
            }
        }
    }
}
