package com.soi.moya.data

import androidx.lifecycle.LiveData
import com.soi.moya.models.Music
import com.soi.moya.repository.MusicRepository
import com.soi.moya.util.UiState
import androidx.lifecycle.MutableLiveData
import com.soi.moya.models.Team

class MusicManager private constructor() {
    private val _musicRepositoryImp = MusicRepository()
    private val _musics: MutableMap<String, LiveData<List<Music>>> = mutableMapOf()
    val musics: MutableMap<String, LiveData<List<Music>>>
        get() = _musics

    init {
        Team.values().forEach {
            loadMusics(it.getFirebaseCollectionName())
        }
    }

    private fun loadMusics(team: String) {
        val musicLiveData = MutableLiveData<List<Music>>()
        _musicRepositoryImp.getMusics(team) { result ->
            when (result) {
                is UiState.Success -> {
                    musicLiveData.postValue(result.data ?: emptyList())
                }
                is UiState.Failure -> {
                    // 에러 처리
                }
                else -> {
                    // 다른 상태 처리
                }
            }
        }
        _musics[team] = musicLiveData
    }

    fun getAllMusics(): MutableLiveData<List<Music>> {
        val allMusicsLiveData = MutableLiveData<List<Music>>()

        _musics.values.forEach { musicLiveData ->
            musicLiveData.observeForever {
                val flattenedList = _musics.values.flatMap { it.value.orEmpty() }
                allMusicsLiveData.postValue(flattenedList)
            }
        }

        return allMusicsLiveData
    }

    companion object {
        @Volatile
        private var INSTANCE: MusicManager? = null

        fun getInstance(): MusicManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MusicManager().also { INSTANCE = it }
            }
    }
}