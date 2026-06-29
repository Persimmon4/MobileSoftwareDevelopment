package com.musiccollect.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.musiccollect.data.entity.RecentlyPlayed
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyPlayedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recent: RecentlyPlayed)

    @Query("SELECT * FROM recently_played ORDER BY playedAt DESC LIMIT 50")
    fun getAll(): Flow<List<RecentlyPlayed>>

    @Query("DELETE FROM recently_played WHERE musicId = :musicId")
    suspend fun deleteByMusicId(musicId: String)

    @Query("DELETE FROM recently_played")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM recently_played WHERE musicId = :musicId")
    suspend fun countByMusicId(musicId: String): Int
}
