package com.shop.app.localization

import android.content.Context
import android.content.ContextWrapper
import android.os.LocaleList
import androidx.core.os.LocaleListCompat
import java.util.Locale

fun Context.createLocaleWrapper(language: String): ContextWrapper {
    val locale = if (language.isEmpty()) {
        Locale.getDefault()
    } else {
        Locale.forLanguageTag(language)
    }
    val configuration = resources.configuration
    configuration.setLocale(locale)
    val localeList = LocaleList.forLanguageTags(locale.toLanguageTag())
    configuration.setLocales(localeList)
    val context = createConfigurationContext(configuration)
    return ContextWrapper(context)
}
