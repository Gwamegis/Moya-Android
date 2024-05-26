package com.soi.moya.data

import com.soi.moya.models.StoredMusic
import kotlinx.coroutines.flow.Flow

class OfflineItemsRepository(private val storedMusicDao: StoredMusicDao) : StoredMusicRepository {
    override fun getAllItemsStream(): Flow<List<StoredMusic>> = storedMusicDao.getAllItems()
    override fun getItemStream(id: Int): Flow<StoredMusic?> = storedMusicDao.getItem(id)
    override fun getByStoragePlaylist(): Flow<List<StoredMusic>> = storedMusicDao.getByStoragePlaylist()
    override fun getByDefaultPlaylist(): Flow<List<StoredMusic>> = storedMusicDao.getByDefaultPlaylist()
    override fun getByPlaylist(playlist: String): Flow<List<StoredMusic>> = storedMusicDao.getByDefaultPlaylist(playlist)
    override suspend fun doesItemExist(itemId: String, playlist: String): Boolean = storedMusicDao.doesItemExist(itemId, playlist)
    override suspend fun getItemCount(playlist: String): Int = storedMusicDao.getItemCount(playlist)
    override suspend fun deleteById(id: String, playlist: String) = storedMusicDao.deleteById(id, playlist)
    override suspend fun insertItem(item: StoredMusic) = storedMusicDao.insert(item)
    override suspend fun insertAll(items: List<StoredMusic>) = storedMusicDao.insertAll(items)
    override suspend fun deleteItem(item: StoredMusic) = storedMusicDao.delete(item)
    override suspend fun updateItem(item: StoredMusic) = storedMusicDao.update(item)
    override suspend fun getItemById(id: String, playlist: String): StoredMusic? = storedMusicDao.getItemById(id, playlist)
    override suspend fun isSongLiked(id: String): Boolean = storedMusicDao.isSongInFavorite(id)
    override suspend fun updateOrder(start: Int, end: Int, increment: Int) = storedMusicDao.updateOrder(start, end, increment)
    override suspend fun updateOrder(id: String, order: Int) = storedMusicDao.updateOrder(id, order)
}