package com.soi.moya.models

import android.content.Context
import android.util.Log
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
        val CURRENT_PLAY_SONG_ID = stringPreferencesKey("song_Id")
        val CURRENT_PLAY_SONG_POSITION = stringPreferencesKey("song_position")
        val IS_MINIPLAYER_ACTIVATED = booleanPreferencesKey("is_miniplayer_activated")
        val IS_NEED_HIDE_MINIPLAYER = booleanPreferencesKey("is_need_hide_miniplayer")
        val IS_LYRIC_DISPLAYING = booleanPreferencesKey("is_lyric_displaying")
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

    val currentPlaySongPosition: Flow<Int?> = context.dataStore.data
        .map {
            it[CURRENT_PLAY_SONG_POSITION]?.toInt() ?: -1
        }

    val isMiniPlayerActivated: Flow<Boolean> = context.dataStore.data
        .map {
            it[IS_MINIPLAYER_ACTIVATED] ?: true
        }

    val isNeedHideMiniPlayer: Flow<Boolean> = context.dataStore.data
        .map {
            it[IS_NEED_HIDE_MINIPLAYER] ?: false
        }
    val isLyricDisplaying: Flow<Boolean> = context.dataStore.data
        .map {
            it[IS_LYRIC_DISPLAYING] ?: true
        }
    suspend fun saveSelectedTeam(team: Team) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_TEAM] = team.name
        }
    }

    suspend fun saveCurrentSongId(songId: String) {
        context.dataStore.edit { preferences ->
            Log.d("**UserPreferences", "songId: $songId")
            preferences[CURRENT_PLAY_SONG_ID] = songId
        }
    }

    suspend fun saveCurrentSongPosition(position: Int) {
        context.dataStore.edit { preferences ->
            preferences[CURRENT_PLAY_SONG_POSITION] = position.toString()
        }
    }

    suspend fun saveAppVersion(version: Version?) {
        if (version != null) {
            context.dataStore.edit {
                it[APP_VERSION] = version.version
            }
        }
    }

    suspend fun saveIsMiniplayerActivated(isActivated: Boolean) {
        context.dataStore.edit {
            it[IS_MINIPLAYER_ACTIVATED] = isActivated
        }
    }
    suspend fun saveIsNeedHideMiniPlayer(isNeedToHide: Boolean) {
        context.dataStore.edit {
            it[IS_NEED_HIDE_MINIPLAYER] = isNeedToHide
        }
    }
    suspend fun saveIsLyricState(isLyric: Boolean) {
        context.dataStore.edit {
            it[IS_LYRIC_DISPLAYING] = isLyric
        }
    }
}