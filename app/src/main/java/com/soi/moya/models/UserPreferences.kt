package com.soi.moya.models

import android.content.Context
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
    }

    val getSelectedTeam: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_TEAM] ?: "doosan"
        }
    suspend fun saveSelectedTeam(team: Team) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_TEAM] = team.name
        }
    }
}