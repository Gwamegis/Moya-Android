package com.soi.moya.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.soi.moya.models.Traffic
import com.soi.moya.repository.FirebaseRepository
import com.soi.moya.util.UiState

class TrafficManager private constructor() {
    private val _firebaseRepository = FirebaseRepository<Traffic>(clazz = Traffic::class.java)
    private val _traffic = MutableLiveData<Traffic?>()
    init {
        loadTraffic()
    }

    val traffic: MutableLiveData<Traffic?>
        get() = _traffic

    private fun loadTraffic() {
        _firebaseRepository.getSingleData("Traffic") { result ->
            when(result) {
                is UiState.Success -> {
                    _traffic.value = result.data
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
        private var INSTANCE: TrafficManager? = null

        fun getInstance(): TrafficManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: TrafficManager().also { INSTANCE = it }
            }
    }
}