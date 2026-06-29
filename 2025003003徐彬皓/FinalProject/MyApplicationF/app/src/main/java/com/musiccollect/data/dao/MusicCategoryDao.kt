package com.musiccollect.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.musiccollect.data.entity.MusicCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicCategoryDao {
    @Insert
    suspend fun insert(category: MusicCategory)

    @Query("SELECT * FROM music_category")
    fun getAllCategories(): Flow<List<MusicCategory>>

    @Query("SELECT * FROM music_category WHERE name = :name")
    suspend fun getCategoryIdByName(name: String): MusicCategory?
}
