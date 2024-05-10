package com.soi.moya.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.models.MusicInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface PlayListRepository {
    fun addMusic(musicInfo: MusicInfo)
    fun getAllMusic(): Flow<List<MusicInfo>>
    fun deleteMusic(musicInfo: MusicInfo)
    fun getMusicById(id: String): MusicInfo?
}

class PlayListViewModel @Inject constructor(private val itemRepository: PlayListRepository) : ViewModel() {
    private val _allItems: MutableLiveData<List<MusicInfo>> = MutableLiveData()
    val allItems: LiveData<List<MusicInfo>> = _allItems

    init {
        viewModelScope.launch {
            itemRepository.getAllMusic().collect { musicInfoList ->
                _allItems.value = musicInfoList
            }
        }
    }

    fun getPreviousMusic(currentMusic: MusicInfo): MusicInfo? {
        val currentIndex = _allItems.value?.indexOfFirst { it.id == currentMusic.id } ?: 0
        if (currentIndex < 1) { return null }
        return _allItems.value?.getOrNull(currentIndex - 1)
    }

    fun getNextMusic(currentMusic: MusicInfo): MusicInfo? {
        val currentIndex = _allItems.value?.indexOfFirst { it.id == currentMusic.id } ?: -1
        // TODO: 처음으로 이동한다는 toast message 출력 필요
        if (currentIndex == -1 || currentIndex == _allItems.value?.size?.minus(1)) { return _allItems.value?.first() }

        return _allItems.value?.getOrNull(currentIndex + 1) // 현재 음악의 다음 음악을 반환합니다.
    }
}