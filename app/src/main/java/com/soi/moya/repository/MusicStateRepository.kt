package com.soi.moya.repository

import android.util.Log
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MusicStateRepository  @Inject constructor (
    private val userPreferences: UserPreferences
) {
    private val _selectedTeam = MutableStateFlow<Team?>(null)
    val selectedTeam: StateFlow<Team?> get() = _selectedTeam

    private val _currentPlaySongId = MutableStateFlow<String?>(null)
    val currentPlaySongId: StateFlow<String?> get() = _currentPlaySongId

    private val _currentPlaySongPosition = MutableStateFlow<Int>(0)
    val currentPlaySongPosition: StateFlow<Int> get() = _currentPlaySongPosition

    private val _isMiniPlayerActivated = MutableStateFlow(false)
    val isMiniPlayerActivated: StateFlow<Boolean> get() = _isMiniPlayerActivated

    private val _isNeedHideMiniPlayer = MutableStateFlow(false)
    val isNeedHideMiniPlayer: StateFlow<Boolean> get() = _isNeedHideMiniPlayer

    private val _isLyricDisplaying = MutableStateFlow(false)
    val isLyricDisplaying: StateFlow<Boolean> get() = _isLyricDisplaying

    private val _isInitialLoad = MutableStateFlow(false)
    val isInitialLoad: StateFlow<Boolean> get() = _isInitialLoad

    fun setSelectedTeam(team: Team) {
        _selectedTeam.value = team
    }
    fun setCurrentPlaySongId(songId: String) {
        _currentPlaySongId.value = songId
    }
    fun setCurrentPlaySongPosition(position: Int) {
        _currentPlaySongPosition.value = position
    }
    fun setMiniPlayerActivated(activated: Boolean) {
        _isMiniPlayerActivated.value = activated
    }
    fun setNeedHideMiniPlayer(hide: Boolean) {
        _isNeedHideMiniPlayer.value = hide
    }
    fun setLyricDisplaying(displaying: Boolean) {
        _isLyricDisplaying.value = displaying
    }
    fun setInitialLoad(isLoaded: Boolean) {
        _isInitialLoad.value = isLoaded
    }

    //TODO: 앱 종료시 데이터 백업
    suspend fun saveToDataStore() {
        userPreferences.saveSelectedTeam(selectedTeam.value ?: Team.doosan)
        userPreferences.saveCurrentSongId(currentPlaySongId.value ?: "")
        userPreferences.saveCurrentSongPosition(currentPlaySongPosition.value.toInt())
        userPreferences.saveIsMiniplayerActivated(isMiniPlayerActivated.value)
        userPreferences.saveIsNeedHideMiniPlayer(isNeedHideMiniPlayer.value)
        userPreferences.saveIsLyricState(isLyricDisplaying.value)
    }

    suspend fun loadUserPreferences() {
        // 데이터 스토어에서 값을 불러와 MutableStateFlow에 설정
        userPreferences.getSelectedTeam.collect {team ->
            _selectedTeam.value = team?.let { Team.valueOf(it) }
            setInitialLoad(true)
        }
        userPreferences.currentPlaySongId.collect { songId ->
            _currentPlaySongId.value = songId
        }
        userPreferences.currentPlaySongPosition.collect { position ->
            _currentPlaySongPosition.value = position ?: 0
        }
        userPreferences.isMiniPlayerActivated.collect { isActivated ->
            _isMiniPlayerActivated.value = isActivated
        }
        userPreferences.isNeedHideMiniPlayer.collect { isNeedToHide ->
            _isNeedHideMiniPlayer.value = isNeedToHide
        }
        userPreferences.isLyricDisplaying.collect { isDisplaying ->
            _isLyricDisplaying.value = isDisplaying
        }
    }
}