package com.soi.moya.ui.main_activity

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.MusicManager
import com.soi.moya.models.MusicInfo
import com.soi.moya.models.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
//import com.soi.moya.repository.MusicPlayerManager
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    application: Application
) : ViewModel() {

    private val _userPreferences = UserPreferences(application)

    private val _selectedMusic = MutableLiveData<MusicInfo?>()
    val selectedMusic: LiveData<MusicInfo?> = _selectedMusic

    companion object {
        @Volatile
        private var instance: MusicViewModel? = null

        fun getInstance(application: Application) =
            instance ?: synchronized(this) {
                instance ?: MusicViewModel(application).also { instance = it }
            }
    }
}