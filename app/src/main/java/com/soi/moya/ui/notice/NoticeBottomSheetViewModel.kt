package com.soi.moya.ui.notice

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soi.moya.data.TrafficManager
import com.soi.moya.data.VersionManager
import com.soi.moya.models.Traffic
import com.soi.moya.models.UserPreferences
import com.soi.moya.models.Version
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class NoticeBottomSheetViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _userPreferences = UserPreferences(application)
    private val _versionManager = VersionManager.getInstance()
    private val _versionState = mutableStateOf<Version?>(null)
    private val _isNotCheckedVersion = mutableStateOf(false)
    private val _trafficManager = TrafficManager.getInstance()
    private val _traffic = mutableStateOf<Traffic?>(null)
    private val _isExistTrafficIssue = mutableStateOf(false)

    val isNotCheckedVersion: State<Boolean>
        get() = _isNotCheckedVersion

    val versionState: State<Version?>
        get() = _versionState

    val isExistTrafficIssue: State<Boolean>
        get() = _isExistTrafficIssue

    val traffic: State<Traffic?>
        get() = _traffic

    init {
        _versionManager.version.observeForever { newVersion ->
            viewModelScope.launch {

                val savedVersion = _userPreferences.appVersion.first()
                _versionState.value = newVersion
                _isNotCheckedVersion.value = savedVersion < newVersion?.version.toString()
            }
        }

        _trafficManager.traffic.observeForever { newTraffic ->
            viewModelScope.launch {
                _traffic.value = newTraffic
                _isExistTrafficIssue.value = checkIsExistTrafficIssue()
            }
        }
    }

    suspend fun checkRequiredUpdate(): Boolean {
        val version = versionState.value
        return if (version?.isRequired == true) {
            true
        } else {
            saveCheckVersion()
            false
        }
    }

    // traffic 공지가 오늘 게시된 공지인지 확인
    private fun checkIsExistTrafficIssue(): Boolean {
        val traffic = _traffic.value
        val trafficDate = traffic?.date
        return isToday(trafficDate)
    }

    private fun isToday(date: Date?): Boolean {
        if (date == null) return false

        val todayCalendar = Calendar.getInstance()
        val anotherCalendar = Calendar.getInstance()
        anotherCalendar.time = date

        return todayCalendar.get(Calendar.YEAR) == anotherCalendar.get(Calendar.YEAR) &&
                todayCalendar.get(Calendar.DAY_OF_YEAR) == anotherCalendar.get(Calendar.DAY_OF_YEAR)
    }

    private suspend fun saveCheckVersion() {
        _userPreferences.saveAppVersion(_versionState.value)
    }
}