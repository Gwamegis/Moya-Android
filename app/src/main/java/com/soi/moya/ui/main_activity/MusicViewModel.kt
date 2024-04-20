package com.soi.moya.ui.main_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.soi.moya.models.MusicInfo

class MusicViewModel : ViewModel() {
    private val _selectedMusic = MutableLiveData<MusicInfo?>()
    val selectedMusic: LiveData<MusicInfo?> = _selectedMusic

    fun setSelectedMusic(music: MusicInfo) {
        _selectedMusic.value = music
    }
}