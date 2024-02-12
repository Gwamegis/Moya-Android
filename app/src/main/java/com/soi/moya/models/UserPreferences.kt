package com.soi.moya.models

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("selected_team")
        val SELECTED_TEAM = stringPreferencesKey("selected_team")
        val APP_VERSION = stringPreferencesKey("app_version")
    }

    val getSelectedTeam: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_TEAM] ?: "doosan"
        }

    val appVersion: Flow<String>
        get() = context.dataStore.data.map { preferences ->
            preferences[APP_VERSION] ?: "1.0.0"
        }

    suspend fun saveSelectedTeam(team: Team) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_TEAM] = team.name
        }
    }

    suspend fun saveAppVersion(version: Version?) {
        if (version != null) {
            context.dataStore.edit {
                it[APP_VERSION] = version.version
                Log.d("[저장 완료] version", version.version)
            }
        }
    }
}