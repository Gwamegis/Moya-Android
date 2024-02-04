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
    private val _teamMusics = mutableStateOf(emptyList<Music>())
    private val _playerMusics = mutableStateOf(emptyList<Music>())
    private val _musics = _musicManager.musics["Doosan"] ?: MutableLiveData(emptyList())

    init {
        filteringMusics()
    }

    private fun filteringMusics() {
        _musics.observeForever { musics ->
            _teamMusics.value = musics.filter { it.type }
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