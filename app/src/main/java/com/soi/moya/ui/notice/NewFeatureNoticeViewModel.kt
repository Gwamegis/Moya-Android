package com.soi.moya.ui.notice

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.VersionManager
import com.soi.moya.models.UserPreferences
import com.soi.moya.models.Version
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NewFeatureNoticeViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _userPreferences = UserPreferences(application)
    private val _versionManager = VersionManager.getInstance()
    private val _versionState = mutableStateOf<Version?>(null)
    private val _isNotCheckedVersion = mutableStateOf(false)
    val isNotCheckedVersion: State<Boolean>
        get() = _isNotCheckedVersion
    val versionState: State<Version?>
        get() = _versionState

    init {
        _versionManager.version.observeForever { newVersion ->
            viewModelScope.launch {

                val savedVersion = _userPreferences.appVersion.first()
                _isNotCheckedVersion.value = savedVersion != newVersion?.version
                if (_isNotCheckedVersion.value) {
                    _versionState.value = newVersion
                }
            }
        }
    }
}