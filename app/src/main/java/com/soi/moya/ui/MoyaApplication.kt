package com.soi.moya.ui

import android.app.Application
import com.soi.moya.data.AppContainer
import com.soi.moya.data.AppDataContainer
import com.soi.moya.models.UserPreferences
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class MoyaApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)

        val userPreferences = getUserPreferences()

        CoroutineScope(Dispatchers.Main).launch {
            userPreferences.saveCurrentSongId("")
        }
    }
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface UserPreferencesEntryPoint {
        fun getUserPreferences(): UserPreferences
    }
    private fun getUserPreferences(): UserPreferences {
        val entryPoint = EntryPointAccessors.fromApplication(this, UserPreferencesEntryPoint::class.java)
        return entryPoint.getUserPreferences()
    }
}