package com.soi.moya.ui.music_list

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.soi.moya.data.MusicManager
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.SeasonSongManager
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.Team
import com.soi.moya.models.toItem
import com.soi.moya.models.toMediaItem
import com.soi.moya.models.toStoredMusic
import com.soi.moya.repository.MediaControllerManager
import com.soi.moya.repository.MusicPlaybackManager
import com.soi.moya.repository.MusicStateRepository
import com.soi.moya.ui.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val application: Application,
    private val storedMusicRepository: StoredMusicRepository,
    private val musicPlaybackManager: MusicPlaybackManager,
    private val mediaControllerManager: MediaControllerManager,
    private val musicStateRepository: MusicStateRepository
) : ViewModel() {
    val selectedTeam: LiveData<Team?> = musicStateRepository.selectedTeam.asLiveData()
    val currentSongId: LiveData<String?> = musicStateRepository.currentPlaySongId.asLiveData()

    private val _musicManager = MusicManager.getInstance()
    private val _seasonSongManager = SeasonSongManager.getInstance()
    private val seasonSongs: LiveData<List<String>> get() = _seasonSongManager.getSeasonSongsForTeam(selectedTeam.value?.name ?: "doosan")
    private val _teamMusics = mutableStateOf(emptyList<MusicInfo>())
    private val _playerMusics = mutableStateOf(emptyList<MusicInfo>())

    init {
        observeUserPreference()
    }

    private fun observeUserPreference() {
        selectedTeam.observeForever {
            updateMusicForSelectedTeam()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onTapListItem(music: MusicInfo) {
        val mediaItem = music.toMediaItem(application.filesDir.absolutePath)
        val controller = mediaControllerManager.controller

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val existingMusic = storedMusicRepository.getItemById(music.id, "default")
                val count = storedMusicRepository.getItemCount("default")

                if (existingMusic != null) {
                    // 이미 저장된 음악 정보가 있을 때
                    val existingIndex = mediaControllerManager.mediaItemList.value.indexOfFirst { it.mediaId == mediaItem.mediaId }

                    storedMusicRepository.updateOrder(
                        start = existingIndex,
                        end = count,
                        increment = -1
                    )
                    storedMusicRepository.updateOrder(existingMusic.songId, count-1)

                    withContext(Dispatchers.Main) {
                        if (existingIndex != null) {
                            controller?.removeMediaItem(existingIndex)
                        }
                        controller?.addMediaItem(mediaItem)
                        mediaControllerManager.addMediaItem(mediaItem)
                    }

                } else {
                    // 새로운 음악 정보를 추가할 때
                    withContext(Dispatchers.Main) {
                        controller?.addMediaItem(mediaItem)
                        mediaControllerManager.addMediaItem(mediaItem)
                    }

                    val newMusic = music.toStoredMusic(
                        team = music.team,
                        order = count,
                        date = Utility.getCurrentTimeString(),
                        playlist = "default"
                    )
                    storedMusicRepository.insertItem(newMusic.toItem())
                }

                withContext(Dispatchers.Main) {
                    controller?.let {
                        if (it.mediaItemCount > 0) {
                            it.seekTo(it.mediaItemCount - 1, 0) // 마지막 아이템의 인덱스와 시작 시간 0
                        }
                    }
                }

                saveCurrentSongId(music.id, count-1)

                if (currentSongId.value != music.id) {
                    playMusic(music)
                }
            }
        }
    }

    private fun saveCurrentSongId(songId: String, position: Int) {
        musicStateRepository.setCurrentPlaySongId(songId)
        musicStateRepository.setMiniPlayerActivated(false)
        musicStateRepository.setCurrentPlaySongPosition(position)
    }

    private fun updateMusicForSelectedTeam() {
        val selectedTeam = selectedTeam.value?.name ?: "doosan"
        _musicManager.getFilteredSelectedTeamMusic(selectedTeam).observeForever { musics ->
            _teamMusics.value = musics?.filter { !it.type } ?: emptyList()
            _playerMusics.value = musics?.filter { it.type } ?: emptyList()
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

    private fun playMusic(music: MusicInfo) {
        viewModelScope.launch {
            musicPlaybackManager.playMusic(music)
        }
    }

    fun onTapSelectTeamButton() {
        musicStateRepository.setNeedHideMiniPlayer(true)
    }
}