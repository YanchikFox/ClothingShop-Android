package com.shop.app.localization

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private const val LANGUAGE_DATA_STORE = "language_settings"
private val Context.languageDataStore by preferencesDataStore(name = LANGUAGE_DATA_STORE)
private val LANGUAGE_KEY = stringPreferencesKey("app_language")

object LanguagePreferencesDataStore {

    fun languageFlow(context: Context): Flow<String?> =
        context.languageDataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY]
        }

    suspend fun setLanguage(context: Context, languageTag: String?) {
        context.languageDataStore.edit { prefs ->
            if (languageTag.isNullOrBlank()) {
                prefs.remove(LANGUAGE_KEY)
            } else {
                prefs[LANGUAGE_KEY] = languageTag
            }
        }
    }

    suspend fun currentLanguage(context: Context): String? =
        languageFlow(context).firstOrNull()
}
