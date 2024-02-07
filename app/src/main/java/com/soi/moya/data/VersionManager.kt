package com.soi.moya.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.soi.moya.models.Version
import com.soi.moya.repository.FirebaseRepository
import com.soi.moya.util.UiState

class VersionManager private constructor() {
    private val _firebaseRepository = FirebaseRepository(clazz = Version::class.java)
    private val _loading = MutableLiveData<Boolean>()
    private val _version = MutableLiveData<Version?>()

    val version: MutableLiveData<Version?>
        get() = _version

    val loading: LiveData<Boolean> get() = _loading

    init {
        loadVersion()
    }

    private fun loadVersion() {
        _loading.value = true
        _firebaseRepository.getSingleData("AndroidVersion") { result ->
            _loading.value = false
            when(result) {
                is UiState.Success -> {
                    _version.value = result.data
                    Log.d("test", result.toString())
                }
                else -> {
                 // fail 처리
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: VersionManager? = null

        fun getInstance(): VersionManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VersionManager().also { INSTANCE = it }
            }
    }
}