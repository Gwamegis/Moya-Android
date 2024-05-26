package com.soi.moya.data

import com.soi.moya.models.StoredMusic
import kotlinx.coroutines.flow.Flow

interface StoredMusicRepository {
    fun getAllItemsStream(): Flow<List<StoredMusic>>
    fun getItemStream(id: Int): Flow<StoredMusic?>
    fun getByStoragePlaylist(): Flow<List<StoredMusic>>
    fun getByDefaultPlaylist(): Flow<List<StoredMusic>>
    fun getByPlaylist(playlist: String): Flow<List<StoredMusic>>
    suspend fun doesItemExist(itemId: String, playlist: String): Boolean
    suspend fun getItemCount(playlist: String): Int
    suspend fun deleteById(id: String, playlist: String)
    suspend fun insertItem(item: StoredMusic)
    suspend fun insertAll(items: List<StoredMusic>)
    suspend fun deleteItem(item: StoredMusic)
    suspend fun updateItem(item: StoredMusic)
    suspend fun getItemById(id: String, playlist: String): StoredMusic
    suspend fun isSongLiked(id: String): Boolean

    //order 값이 start <= .. < end 사이인 경우 increment 값 만큼 order를 업데이트
    suspend fun updateOrder(start: Int, end: Int, increment: Int)
    suspend fun updateOrder(id: String, order: Int)
}