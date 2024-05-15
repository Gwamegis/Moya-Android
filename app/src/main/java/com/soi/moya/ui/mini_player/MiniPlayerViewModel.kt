package com.soi.moya.ui.mini_player

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.soi.moya.models.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MiniPlayerViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _userPreferences = UserPreferences(application)

    val minHeight = 55f
    val maxHeight = MutableStateFlow(0f)
    val height = MutableStateFlow(minHeight)

//    private val _height = MutableLiveData(minHeight)
//    val height: LiveData<Float> = _height

    val threshold = mutableFloatStateOf(0f)
    val scalingFactor = 0.4f
    val bottomPadding = 110f
    val horizontalPadding = 10f

    init {
        observeIsMiniplayerActivated()
    }

    private fun observeIsMiniplayerActivated() {
        viewModelScope.launch {
            _userPreferences.isMiniPlayerActivated.collect{
                //화면 크기 변경..?
            }
        }
    }
    fun setHeight(value: Float) {
        Log.d("MiniPlayer-setHeight1", height.value.toString())
        height.value = value
        Log.d("MiniPlayer-setHeight2", height.value.toString())

    }

    fun setMaxHeight(value: Float) {
        maxHeight.value = value
        threshold.value = value / 2
    }
}