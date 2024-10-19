package com.soi.moya.data

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
        saveStateToDataStore()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        saveStateToDataStore()
    }
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        saveStateToDataStore()
    }
    private fun saveStateToDataStore() {
        lifecycleOwner.lifecycleScope.launch {
            musicStateRepository.saveToDataStore()
        }
    }
}