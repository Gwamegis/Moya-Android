package com.soi.moya.data

import androidx.lifecycle.MutableLiveData
import com.soi.moya.models.Version
import com.soi.moya.repository.FirebaseRepository
import com.soi.moya.util.UiState

class VersionManager private constructor() {
    private val _firebaseRepository = FirebaseRepository(clazz = Version::class.java)
    private val _version = MutableLiveData<Version?>()

    val version: MutableLiveData<Version?>
        get() = _version

    init {
        loadVersion()
    }

    private fun loadVersion() {
        _firebaseRepository.getSingleData("AndroidVersion") { result ->
            when(result) {
                is UiState.Success -> {
                    _version.value = result.data
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