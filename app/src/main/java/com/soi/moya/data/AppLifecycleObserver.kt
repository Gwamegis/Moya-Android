package com.soi.moya.data

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.soi.moya.repository.MusicStateRepository
import kotlinx.coroutines.launch

class AppLifecycleObserver(
    private val musicStateRepository: MusicStateRepository,
    private val lifecycleOwner: LifecycleOwner
) : DefaultLifecycleObserver {

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("**onStop", "onStop")

        saveStateToDataStore()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        Log.d("**onPause", "onPause")
    }
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        saveStateToDataStore()
        Log.d("lifecycleObserver-desto", "App is being destroyed")
    }
    private fun saveStateToDataStore() {
        // DataStore에 상태 저장 로직
        lifecycleOwner.lifecycleScope.launch {
            Log.d("**lifecycleObserver", "save")
            musicStateRepository.saveToDataStore()
        }
    }
}