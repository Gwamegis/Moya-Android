package com.soi.moya.ui.notice

import android.app.Application
import android.util.Log
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
                Log.d("[현재 저장된 버전] version", savedVersion.toString())
                Log.d("[출시된 버전] version", newVersion?.version.toString())
                _versionState.value = newVersion
                _isNotCheckedVersion.value = savedVersion != newVersion?.version
            }
        }
    }

    suspend fun saveCheckVersion() {
        _userPreferences.saveAppVersion(_versionState.value)
    }
}