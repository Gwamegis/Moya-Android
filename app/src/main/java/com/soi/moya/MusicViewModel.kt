package com.soi.moya

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MusicViewModel: ViewModel() {
    private var _data = MutableLiveData<List<MusicModel>>()
    var teamMusicList: List<MusicModel> = emptyList()
    var playerMusicList: List<MusicModel> = emptyList()

    fun setData(data: List<MusicModel>) {
        _data.postValue(data)
        teamMusicList = data.filter { music ->
            !music.type
        }
        playerMusicList = data.filter { music ->
            music.type
        }
    }

    fun fetchData(): LiveData<List<MusicModel>> {
        return _data
    }

}