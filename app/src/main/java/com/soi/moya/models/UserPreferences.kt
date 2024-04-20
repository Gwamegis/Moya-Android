package com.soi.moya.models

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.soi.moya.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("selected_team")
        val SELECTED_TEAM = stringPreferencesKey("selected_team")
        val APP_VERSION = stringPreferencesKey("app_version")
        val SHOW_MINI_PLAYER = booleanPreferencesKey("show_mini_player")
        val CURRENT_PLAY_SONG_ID = stringPreferencesKey("song_Id")
    }

    val getSelectedTeam: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_TEAM]
        }

    val appVersion: Flow<String>
        get() = context.dataStore.data.map { preferences ->
            preferences[APP_VERSION] ?: BuildConfig.VERSION_NAME
        }

    val currentPlaySongId: Flow<String?> = context.dataStore.data
        .map {
            it[CURRENT_PLAY_SONG_ID]
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
            }
        }
    }
}