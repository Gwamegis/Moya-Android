package com.soi.moya.data

import android.content.Context
import com.soi.moya.models.MusicInfo
import kotlinx.coroutines.flow.Flow

interface AppContainer {
    val playListRepository: PlayListRepository
    val itemsRepository: StoredMusicRepository
}
class AppDataContainer(private val context: Context) : AppContainer {

    override val playListRepository: PlayListRepository by lazy {
        PlayListRepositoryImpl(context)
    }

    override val itemsRepository: StoredMusicRepository by lazy {
        OfflineItemsRepository(StoredMusicDatabase.getDatabase(context).storedMusicDao())
    }
}

class PlayListRepositoryImpl(context: Context) : PlayListRepository {
    private val musicDao: MusicDao = PlayListDatabase.getDatabase(context).musicDao()

    override fun addMusic(musicInfo: MusicInfo) {
        musicDao.addMusic(musicInfo)
    }

    override fun getAllMusic(): Flow<List<MusicInfo>> {
        return musicDao.getAllMusic()
    }

    override fun deleteMusic(musicInfo: MusicInfo) {
        musicDao.deleteMusic(musicInfo)
    }

    override fun getMusicById(id: String): MusicInfo? {
        return musicDao.getMusicById(id)
    }
}