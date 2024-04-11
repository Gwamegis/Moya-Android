package com.soi.moya.data

import androidx.lifecycle.LiveData
import com.soi.moya.models.Music
import com.soi.moya.util.UiState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.Team
import com.soi.moya.models.toMusicInfo
import com.soi.moya.repository.FirebaseRepository
class MusicManager private constructor() {
    private val _firebaseRepository = FirebaseRepository<Music>(clazz = Music::class.java)
    private val _musics: MutableMap<String, MutableLiveData<List<Music>>> = mutableMapOf()

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

    fun observeMusics(observer: Observer<in List<Music>?>) {
        _musics.forEach { (_, liveData) ->
            liveData.observeForever(observer)
        }
    }

    fun getFilteredSelectedTeamMusic(teamName: String): LiveData<List<Music>> {
        return _musics[teamName] ?: MutableLiveData(emptyList())
    }

    fun getAllMusicInfo(): MutableLiveData<List<MusicInfo>> {
        val allMusicInfoLiveData = MutableLiveData<List<MusicInfo>>()

        val flattenedList = _musics.values.flatMap {
            it.value.orEmpty()
        }
        val musicInfoList = flattenedList.map { music ->
            music.toMusicInfo(Team.valueOf(_musics.entries.first { it.value.value?.contains(music) == true }.key))
        }

        allMusicInfoLiveData.postValue(musicInfoList)

        return allMusicInfoLiveData
    }

    fun getMusicById(songId: String): Music {
        for ((_, liveData) in _musics) {
            val musicList = liveData.value ?: continue
            val music = musicList.find { it.id == songId }
            if (music != null) {
                return music
            }
        }
        throw IllegalArgumentException("Invalid songId: $songId")
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