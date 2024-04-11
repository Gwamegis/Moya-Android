package com.soi.moya.data

import android.content.Context

interface AppContainer {
    val itemsRepository: StoredMusicRepository
}
class AppDataContainer(private val context: Context) : AppContainer {
    override val itemsRepository: StoredMusicRepository by lazy {
        OfflineItemsRepository(StoredMusicDatabase.getDatabase(context).storedMusicDao())
    }
}