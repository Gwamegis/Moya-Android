package com.soi.moya.ui.music_list

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.soi.moya.data.MusicManager
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.soi.moya.data.SeasonSongManager
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.Team
import com.soi.moya.models.UserPreferences
import com.soi.moya.models.toItem
import com.soi.moya.models.toStoredMusic
import com.soi.moya.repository.MusicPlayerManager
import com.soi.moya.ui.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicListViewModel(
    private val application: Application,
    private val storedMusicRepository: StoredMusicRepository
) : AndroidViewModel(application) {

    private val _userPreferences = UserPreferences(application)
    private val _selectedTeam = MutableStateFlow<String>("doosan")
    val selectedTeam: StateFlow<String> = _selectedTeam
    private val _musicManager = MusicManager.getInstance()
    private val _seasonSongManager = SeasonSongManager.getInstance()
    private val seasonSongs: LiveData<List<String>> get() = _seasonSongManager.getSeasonSongsForTeam(_selectedTeam.value)
    private val _teamMusics = mutableStateOf(emptyList<MusicInfo>())
    private val _playerMusics = mutableStateOf(emptyList<MusicInfo>())

    private val _musicPlayerManager = mutableStateOf(
        MusicPlayerManager.getInstance(
            application = application,
            storedMusicRepository = storedMusicRepository
        )
    )
    private val musicPlayerManager: State<MusicPlayerManager>
        get() = _musicPlayerManager

    init {
        observeUserPreference()
        observeSelectedTeam()
    }

    private fun observeUserPreference() {
        viewModelScope.launch {
            _userPreferences.getSelectedTeam.collect { team ->
                _selectedTeam.value = team ?: "doosan"
                updateMusicForSelectedTeam()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onTapListItem(music: MusicInfo) {
//        val mediaItem =
//            MediaItem.Builder()
//                .setMediaId(music.id)
//                .setUri(music.url)
//                .setMediaMetadata(
//                    MediaMetadata.Builder()
//                        .setArtist(music.team.getKrTeamName())
//                        .setTitle(music.title)
////                        .setArtworkUri(music.team.getPlayerAlbumImageResourceId())
//                        .build()
//                )
//                .build()



        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val existingMusic = storedMusicRepository.getItemById(music.id, "default")

                if (existingMusic != null) {
                    // 이미 저장된 음악 정보가 있을 때
                    storedMusicRepository.updateOrder(
                        start = 0,
                        end = existingMusic.order,
                        increment = 1
                    )
                    storedMusicRepository.updateOrder(existingMusic.songId, 0)
                } else {
                    // 새로운 음악 정보를 추가할 때
                    val order = storedMusicRepository.getItemCount(playlist = "default")
                    storedMusicRepository.updateOrder(
                        start = 0,
                        end = order,
                        increment = 1
                    )

                    val newMusic = music.toStoredMusic(
                        team = music.team,
                        order = 0,
                        date = Utility.getCurrentTimeString(),
                        playlist = "default"
                    )
                    storedMusicRepository.insertItem(newMusic.toItem())
                }

                saveCurrentSongId(music.id)
                saveIsMiniplayerActivated()

                val currentSongId = _userPreferences.currentPlaySongId.firstOrNull()

                if (currentSongId != music.id) {
                    playMusic(music)
                }
            }
        }
    }

    private fun saveIsMiniplayerActivated() {
        viewModelScope.launch {
            _userPreferences.saveIsMiniplayerActivated(false)
        }
    }

    private fun saveCurrentSongId(songId: String) {
        viewModelScope.launch {
            _userPreferences.saveCurrentSongId(songId)
            _userPreferences.saveIsMiniplayerActivated(false)
        }
    }

    private fun updateMusicForSelectedTeam() {

        val selectedTeam = _selectedTeam.value
        _musicManager.getFilteredSelectedTeamMusic(selectedTeam).observeForever { musics ->
            _teamMusics.value = musics?.filter { !it.type } ?: emptyList()
            _playerMusics.value = musics?.filter { it.type } ?: emptyList()
        }
    }

    private fun observeSelectedTeam() {
        viewModelScope.launch {
            _userPreferences.getSelectedTeam.collect { team ->
                _selectedTeam.value = team ?: "doosan"
                updateMusicForSelectedTeam()
            }
        }
    }

    fun getMusicListSize(page: Int): Int {
        return when (page) {
            0 -> _teamMusics.value.size
            1 -> _playerMusics.value.size
            else -> 0
        }
    }

    fun getMusicAt(page: Int, index: Int): MusicInfo {
        return when (page) {
            0 -> _teamMusics.value[index]
            1 -> _playerMusics.value[index]
            else -> _teamMusics.value[index]
        }
    }

    fun fetchAlbumImageResourceId(music: MusicInfo, team: Team): Int {
        return if (seasonSongs.value?.contains(music.title) == true) {
            team.getSeasonSongAlbumImageResourceId()
        } else {
            if(music.type) {
                team.getPlayerAlbumImageResourceId()
            } else {
                team.getTeamImageResourceId()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun playMusic(music: MusicInfo) {
        viewModelScope.launch {
            musicPlayerManager.value.playMusic(music)
        }
    }

    fun onTapSelectTeamButton() {
        viewModelScope.launch {
            _userPreferences.saveIsNeedHideMiniPlayer(isNeedToHide = true)
        }
    }
}