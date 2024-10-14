package com.soi.moya.ui

import android.app.Application
import com.soi.moya.data.AppContainer
import com.soi.moya.data.AppDataContainer
import com.soi.moya.models.UserPreferences
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class MoyaApplication : Application() {
    lateinit var container: AppContainer
    private val userPreferences: UserPreferences by lazy {
        UserPreferences(this)
    }

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)

        CoroutineScope(Dispatchers.Main).launch {
            userPreferences.saveCurrentSongId("")
        }
    }
}