package com.soi.moya.data

import com.soi.moya.models.Music
import com.soi.moya.models.Version
import com.soi.moya.repository.FirebaseRepository
import com.soi.moya.util.UiState

class VersionManager private constructor() {
    private val _firebaseRepository = FirebaseRepository<Version>(clazz = Version::class.java)

    init {
        loadVersion()
    }

    private fun loadVersion() {
        _firebaseRepository.getSingleData("AndroidVersion") { result ->
            when(result) {
                is UiState.Success -> {

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