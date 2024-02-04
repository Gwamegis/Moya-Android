package com.soi.moya.ui.music_list

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.soi.moya.data.MusicManager
import com.soi.moya.models.Music

class MusicListViewModel(
    // TODO: Repository 연결
    application: Application
) : AndroidViewModel(application) {

    private val _musicManager = MusicManager.getInstance()

    // TODO: 현재는 musics지만, 추후 좋아요 등의 정보를 가지고 있는 데이터로 변경이 필요
    private val _teamMusics = mutableStateOf(emptyList<Music>())
    private val _playerMusics = mutableStateOf(emptyList<Music>())

    private val teamMusics: LiveData<List<Music>> get() = _musicManager.getMusics("Doosan") ?: MutableLiveData(emptyList())
    private val playerMusics: LiveData<List<Music>> get() = _musicManager.getMusics("Doosan") ?: MutableLiveData(emptyList())

    init {
        updateMusics()
    }

    private fun updateMusics() {
        teamMusics.observeForever { musics ->
            _teamMusics.value = musics.filter { it.type }
        }

        playerMusics.observeForever { musics ->
            _playerMusics.value = musics.filter { !it.type }
        }
    }

    fun getMusicListSize(page: Int): Int {
        return when (page) {
            0 -> _teamMusics.value.size
            1 -> _playerMusics.value.size
            else -> 0
        }
    }

    fun getMusicAt(page: Int, index: Int): Music {
        return when (page) {
            0 -> _teamMusics.value[index]
            1 -> _playerMusics.value[index]
            else -> _teamMusics.value[index]
        }
    }

    fun toggleLike(index: Int) {
        // TODO
    }
}