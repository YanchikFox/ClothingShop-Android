package com.shop.app.localization

import android.content.Context
import kotlinx.coroutines.flow.Flow

class LanguageRepository(private val context: Context) {

    val languageFlow: Flow<String?> = LanguagePreferencesDataStore.languageFlow(context)

    suspend fun setLanguage(languageTag: String?) {
        LanguagePreferencesDataStore.setLanguage(context, languageTag)
    }
}