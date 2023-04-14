package com.soi.moya

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MusicViewModel: ViewModel() {
    private var _data = MutableLiveData<List<MusicModel>>()

    fun setData(data: List<MusicModel>) {
        _data.value = data
    }

    fun fetchData(): MutableLiveData<List<MusicModel>> {
        return _data
    }
}