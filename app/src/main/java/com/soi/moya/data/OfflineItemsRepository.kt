package com.soi.moya.data

import com.soi.moya.models.StoredMusic
import kotlinx.coroutines.flow.Flow

class OfflineItemsRepository(private val storedMusicDao: StoredMusicDao) : StoredMusicRepository {
    override fun getAllItemsStream(): Flow<List<StoredMusic>> = storedMusicDao.getAllItems()
    override fun getItemStream(id: Int): Flow<StoredMusic?> = storedMusicDao.getItem(id)
    override fun getByStoragePlaylist(playlist: String): Flow<List<StoredMusic>?> = storedMusicDao.getByStoragePlaylist(playlist)
    override fun getByDefaultPlaylist(playlist: String): Flow<List<StoredMusic>?> = storedMusicDao.getByDefaultPlaylist(playlist)
    override suspend fun insertItem(item: StoredMusic) = storedMusicDao.insert(item)
    override suspend fun insertAll(items: List<StoredMusic>) = storedMusicDao.insertAll(items)
    override suspend fun deleteItem(item: StoredMusic) = storedMusicDao.delete(item)
    override suspend fun updateItem(item: StoredMusic) = storedMusicDao.update(item)
}