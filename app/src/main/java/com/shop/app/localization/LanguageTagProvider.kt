package com.shop.app.localization

import android.content.Context
import kotlinx.coroutines.runBlocking
import java.util.Locale

interface LanguageTagProvider {
    fun currentLanguageTag(): String?
}

class DataStoreLanguageTagProvider(context: Context) : LanguageTagProvider {
    private val appContext = context.applicationContext

    override fun currentLanguageTag(): String? {
        val stored = runBlocking {
            LanguagePreferencesDataStore.currentLanguage(appContext)
        }
        return stored?.takeIf { it.isNotBlank() } ?: Locale.getDefault().toLanguageTag()
    }
}
