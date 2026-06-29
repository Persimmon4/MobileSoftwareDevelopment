package com.musiccollect.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.musiccollect.data.entity.FavoriteMusic
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMusicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(music: FavoriteMusic)

    @Update
    suspend fun update(music: FavoriteMusic)

    @Delete
    suspend fun delete(music: FavoriteMusic)

    @Query("SELECT * FROM favorite_music ORDER BY addTime DESC")
    fun getAllFavorites(): Flow<List<FavoriteMusic>>

    @Query("SELECT * FROM favorite_music WHERE id = :musicId")
    suspend fun getMusicById(musicId: String): FavoriteMusic?

    @Query("SELECT * FROM favorite_music WHERE categoryId = :categoryId")
    fun getFavoritesByCategory(categoryId: Int): Flow<List<FavoriteMusic>>

    @Query("SELECT COUNT(*) FROM favorite_music WHERE id = :musicId")
    suspend fun isMusicFavorite(musicId: String): Int
}
