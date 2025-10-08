package com.shop.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.shop.app.di.AppContainer
import com.shop.app.di.DefaultAppContainer
import com.shop.app.localization.LanguagePreferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MyApplication : Application() {
    lateinit var container: AppContainer
    private val localeScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        observeLocales()
    }

    private fun observeLocales() {
        localeScope.launch {
            val initialLanguage = LanguagePreferencesDataStore.currentLanguage(this@MyApplication)
            applyLocales(initialLanguage)
            LanguagePreferencesDataStore.languageFlow(this@MyApplication)
                .distinctUntilChanged()
                .collect { languageTag ->
                    applyLocales(languageTag)
                }
        }
    }

    private fun applyLocales(languageTag: String?) {
        val locales = languageTag
            ?.takeIf { it.isNotBlank() }
            ?.let { LocaleListCompat.forLanguageTags(it) }
            ?: LocaleListCompat.getEmptyLocaleList()
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
