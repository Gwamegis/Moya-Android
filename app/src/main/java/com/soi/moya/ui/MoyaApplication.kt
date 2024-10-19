package com.soi.moya.ui

import android.app.Application
import com.soi.moya.data.AppContainer
import com.soi.moya.data.AppDataContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MoyaApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}