package com.soi.moya.data

import android.content.Context
import com.soi.moya.data.OfflineItemsRepository
import com.soi.moya.data.StoredMusicDatabase
import com.soi.moya.data.StoredMusicRepository

interface AppContainer {
    val itemsRepository: StoredMusicRepository
}
class AppDataContainer(private val context: Context) : AppContainer {
    override val itemsRepository: StoredMusicRepository by lazy {
        OfflineItemsRepository(StoredMusicDatabase.getDatabase(context).storedMusicDao())
    }
}