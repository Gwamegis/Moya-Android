package com.soi.moya.di

import android.app.Application
import com.soi.moya.data.StoredMusicRepository
import com.soi.moya.models.UserPreferences
import com.soi.moya.repository.MediaControllerManager
import com.soi.moya.repository.MusicPlaybackManager
import com.soi.moya.repository.MusicStateRepository
import com.soi.moya.ui.MoyaApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideStoredMusicRepository(
        application: Application
    ): StoredMusicRepository {
        val moyaApplication = application as MoyaApplication
        return moyaApplication.container.itemsRepository
    }
    @Provides
    @Singleton
    fun provideUserPreferences(application: Application): UserPreferences {
        return UserPreferences(application)
    }

    @Provides
    @Singleton
    fun provideMusicStateRepository(
        userPreferences: UserPreferences
    ): MusicStateRepository {
        return MusicStateRepository(userPreferences)
    }

    @Provides
    @Singleton
    fun provideMediaControllerManager(
        application: Application,
        musicStateRepository: MusicStateRepository
    ): MediaControllerManager {
        return MediaControllerManager(application, musicStateRepository)
    }

    @Provides
    @Singleton
    fun provideMusicPlaybackManager(
        controllerManager: MediaControllerManager,
        application: Application
    ): MusicPlaybackManager {
        return MusicPlaybackManager(controllerManager, application)
    }
}