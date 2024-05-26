package com.soi.moya.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.soi.moya.models.SeasonSong
import com.soi.moya.models.Team
import com.soi.moya.repository.FirebaseRepository
import com.soi.moya.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.full.memberProperties

class SeasonSongManager private constructor() {
    private val _firebaseRepository = FirebaseRepository<SeasonSong>(clazz = SeasonSong::class.java)
    private val _seasonSong: MutableMap<String, LiveData<List<String>>> = mutableMapOf()
    init {
        loadSeasonSongData()
    }

    private fun loadSeasonSongData() {
        val seasonSongData = MutableLiveData<SeasonSong>()
        _firebaseRepository.getSingleData("SeasonSong") { result ->
            when (result) {
                is UiState.Success -> {
                    seasonSongData.postValue(result.data ?: null)
                    for (team in Team.values()) {
                        val teamData = SeasonSong::class.memberProperties.firstOrNull { it.name == team.name }?.get(result.data) as? String ?: ""
                        _seasonSong[team.name] = MutableLiveData(teamData.split(","))
                    }
                } else -> {
                    // fail 처리
                }

            }
        }
    }
    fun getSeasonSongsForTeam(teamName: String): LiveData<List<String>> {
        return _seasonSong[teamName] ?: MutableLiveData(emptyList())
    }

    fun getSeasonSongs(): Map<String, LiveData<List<String>>> {
        return _seasonSong
    }
    companion object {
        @Volatile
        private var INSTANCE: SeasonSongManager? = null

        fun getInstance(): SeasonSongManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SeasonSongManager().also { INSTANCE = it }
            }
    }
}