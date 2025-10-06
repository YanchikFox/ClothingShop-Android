package com.shop.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AuthRepository(private val context: Context) {

    private object PreferencesKeys {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit {
            it[PreferencesKeys.AUTH_TOKEN] = token
        }
    }

    suspend fun getAuthToken(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[PreferencesKeys.AUTH_TOKEN]
    }
}