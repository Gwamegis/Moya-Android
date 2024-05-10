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
import com.soi.moya.models.MusicInfo
import kotlinx.coroutines.flow.Flow

@Database(entities = [MusicInfo::class], version = 1)
abstract class PlayListDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao

    companion object {
        @Volatile
        private var INSTANCE: PlayListDatabase? = null

        fun getDatabase(context: Context): PlayListDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    PlayListDatabase::class.java,
                    "play_list_database")
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

@Dao
interface MusicDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addMusic(musicInfo: MusicInfo)

    @Delete
    fun deleteMusic(musicInfo: MusicInfo)

    @Query("SELECT * FROM music_info")
    fun getAllMusic(): Flow<List<MusicInfo>>

    @Query("SELECT * FROM music_info WHERE id = :id")
    fun getMusicById(id: String): MusicInfo?

}