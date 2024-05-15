package com.soi.moya.ui.mini_player

import android.app.Application
import androidx.compose.runtime.mutableFloatStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.models.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MiniPlayerViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _userPreferences = UserPreferences(application)

    var isMiniPlayerActivated = MutableStateFlow(true)

    val minHeight = 55f
    private val _maxHeight = MutableStateFlow(0f)

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
                isMiniPlayerActivated.value = it
            }
        }
    }
    fun setMaxHeight(value: Float) {
        _maxHeight.value = value
        threshold.value = value / 2
    }

    fun setIsMiniplayerActivated(isActivated: Boolean) {
        viewModelScope.launch {
            _userPreferences.saveIsMiniplayerActivated(isActivated)
        }
    }
}