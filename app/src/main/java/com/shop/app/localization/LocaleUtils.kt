package com.shop.app.localization

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Returns the locale that should be used for formatting and casing.
 * Prefers the explicit AppCompat locale (set via [AppCompatDelegate.setApplicationLocales])
 * and falls back to the configuration locale or the system default if nothing is specified.
 */
fun currentLocale(context: Context): Locale {
    val appLocales = AppCompatDelegate.getApplicationLocales()
    val delegateLocale = if (!appLocales.isEmpty) appLocales[0] else null
    if (delegateLocale != null) {
        return delegateLocale
    }

    val configuration = context.resources.configuration
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val locales = configuration.locales
        if (!locales.isEmpty) locales[0] else Locale.getDefault()
    } else {
        @Suppress("DEPRECATION")
        configuration.locale ?: Locale.getDefault()
    }
}

@Composable
fun rememberCurrentLocale(): Locale {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    return remember(configuration) {
        currentLocale(context)
    }
}
