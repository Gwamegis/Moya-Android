package com.soi.moya.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.soi.moya.models.Music
import com.soi.moya.util.UiState
import androidx.lifecycle.MutableLiveData
import com.soi.moya.models.Team
import com.soi.moya.repository.FirebaseRepository

class MusicManager private constructor() {
    private val _firebaseRepository = FirebaseRepository<Music>(clazz = Music::class.java)
    private val _musics: MutableMap<String, LiveData<List<Music>>> = mutableMapOf()

    init {
        Team.values().forEach {
            loadMusics(it)
        }
    }

    private fun loadMusics(team: Team) {
        val musicLiveData = MutableLiveData<List<Music>>()
        _firebaseRepository.getData(team.getFirebaseCollectionName()) { result ->
            when (result) {
                is UiState.Success -> {
                    musicLiveData.postValue(result.data ?: emptyList())
                }
                else -> {
                    // fail 처리
                }
            }
        }
        _musics[team.name] = musicLiveData
    }

    fun getFilteredSelectedTeamMusic(teamName: String): LiveData<List<Music>> {
        return _musics[teamName] ?: MutableLiveData(emptyList())
    }

    fun getAllMusics(): MutableLiveData<List<Music>> {
        val allMusicsLiveData = MutableLiveData<List<Music>>()

        val flattenedList = _musics.values.flatMap { it.value.orEmpty() }
        allMusicsLiveData.postValue(flattenedList)

        return allMusicsLiveData
    }

    fun getMusicById(songId: String): Music {
        for ((_, liveData) in _musics) {
            val musicList = liveData.value ?: continue
            val music = musicList.find { it.id == songId }
            if (music != null) {
                return music
            }
        }
        throw  IllegalArgumentException("Invalid songId: $songId")
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