package com.soi.moya.data

import androidx.lifecycle.LiveData
import com.soi.moya.models.Music
import com.soi.moya.util.UiState
import androidx.lifecycle.MutableLiveData
import com.soi.moya.models.Team
import com.soi.moya.repository.FirebaseRepository

class MusicManager private constructor() {
    private val _firebaseRepository = FirebaseRepository<Music>(clazz = Music::class.java)
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
        _firebaseRepository.getData(team) { result ->
            when (result) {
                is UiState.Success -> {
                    musicLiveData.postValue(result.data ?: emptyList())
                }
                else -> {
                    // fail 처리
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