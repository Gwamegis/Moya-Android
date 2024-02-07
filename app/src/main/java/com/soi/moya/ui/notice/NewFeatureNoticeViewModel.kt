package com.soi.moya.ui.notice

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.soi.moya.data.VersionManager
import com.soi.moya.models.Version

class NewFeatureNoticeViewModel : ViewModel() {

    private val _versionManager = VersionManager.getInstance()
    private val _versionState = mutableStateOf<Version?>(null)
    val versionState: State<Version?> get() = _versionState

    init {
        _versionManager.version.observeForever { newVersion ->
            _versionState.value = newVersion
        }
    }
}