package com.soi.moya.data

import com.soi.moya.models.StoredMusic
import kotlinx.coroutines.flow.Flow

interface StoredMusicRepository {
    fun getAllItemsStream(): Flow<List<StoredMusic>>
    fun getItemStream(id: Int): Flow<StoredMusic?>
    fun getByStoragePlaylist(playlist: String): Flow<List<StoredMusic>?>
    fun getByDefaultPlaylist(playlist: String): Flow<List<StoredMusic>?>
    suspend fun insertItem(item: StoredMusic)
    suspend fun insertAll(items: List<StoredMusic>)
    suspend fun deleteItem(item: StoredMusic)
    suspend fun updateItem(item: StoredMusic)
}