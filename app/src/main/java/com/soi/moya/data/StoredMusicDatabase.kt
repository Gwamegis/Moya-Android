package com.soi.moya.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.soi.moya.models.StoredMusic
import kotlinx.coroutines.flow.Flow

@Database(entities = [StoredMusic::class], version = 1, exportSchema = false)
abstract class StoredMusicDatabase: RoomDatabase() {
    abstract fun storedMusicDao(): StoredMusicDao

    companion object{
        @Volatile
        private var Instance: StoredMusicDatabase? = null

        fun getDatabase(context: Context): StoredMusicDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, StoredMusicDatabase::class.java, "stored_music_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

@Dao
interface StoredMusicDao{
    @Query("SELECT*FROM stored_music")
    fun getAllItems(): Flow<List<StoredMusic>>

    @Query("SELECT * from stored_music WHERE songId = :id")
    fun getItem(id: Int): Flow<StoredMusic>

    @Query("SELECT*FROM stored_music WHERE playlist_title LIKE :playlist ORDER BY date ASC")
    fun getByStoragePlaylist(playlist: String = "favorite"): Flow<List<StoredMusic>>

    @Query("SELECT*FROM stored_music WHERE playlist_title LIKE :playlist ORDER BY `order` ASC")
    fun getByDefaultPlaylist(playlist: String = "default"): Flow<List<StoredMusic>>

    @Query("SELECT*FROM stored_music WHERE playlist_title LIKE :playlist ORDER BY `order` ASC")
    fun getByPlaylist(playlist: String): Flow<List<StoredMusic>>

    @Query("SELECT EXISTS(SELECT 1 FROM stored_music WHERE songId = :itemId AND playlist_title = :playlist)")
    suspend fun doesItemExist(itemId: String, playlist: String): Boolean

    @Query("SELECT COUNT(*) FROM stored_music WHERE playlist_title = :playlist")
    suspend fun getItemCount(playlist: String): Int

    @Query("DELETE FROM stored_music WHERE songId = :id AND playlist_title = :playlist")
    suspend fun deleteById(id: String, playlist: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(storedMusic: StoredMusic)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(storedMusics: List<StoredMusic>)

    @Update
    fun update(storedMusic: StoredMusic)

    @Delete
    fun delete(storedMusic: StoredMusic)

    @Query("DELETE FROM stored_music")
    fun deleteAll()
    @Query("SELECT * from stored_music WHERE songId = :id AND playlist_title = :playlist")
    fun getItemById(id: String, playlist: String): StoredMusic?

    @Query("SELECT EXISTS(SELECT 1 FROM stored_music WHERE songId = :id AND playlist_title = 'favorite')")
    suspend fun isSongInFavorite(id: String): Boolean
    @Query("UPDATE stored_music SET `order` = `order` + :increment WHERE playlist_title = 'default' AND `order` >= :start AND `order` < :end")
    fun updateOrder(start: Int, end: Int, increment: Int)

    @Query("UPDATE stored_music SET `order` = :order WHERE playlist_title = 'default' AND songId = :id")
    fun updateOrder(id: String, order: Int)
}