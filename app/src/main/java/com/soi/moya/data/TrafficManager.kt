package com.soi.moya.data

import com.soi.moya.models.Traffic
import com.soi.moya.repository.FirebaseRepository
import com.soi.moya.util.UiState

class TrafficManager private constructor() {
    private val _firebaseRepository = FirebaseRepository<Traffic>(clazz = Traffic::class.java)

    init {
        loadTraffic()
    }

    private fun loadTraffic() {
        _firebaseRepository.getSingleData("Traffic") { result ->
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
        private var INSTANCE: TrafficManager? = null

        fun getInstance(): TrafficManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: TrafficManager().also { INSTANCE = it }
            }
    }
}