package com.musiccollect.data.repository

import com.musiccollect.data.dao.FavoriteMusicDao
import com.musiccollect.data.dao.MusicCategoryDao
import com.musiccollect.data.entity.FavoriteMusic
import com.musiccollect.data.entity.MusicCategory
import com.musiccollect.data.network.NetworkDataSource
import com.musiccollect.data.network.dto.MusicDto
import kotlinx.coroutines.flow.Flow

class MusicRepository(
    private val networkDataSource: NetworkDataSource,
    private val favoriteMusicDao: FavoriteMusicDao,
    private val categoryDao: MusicCategoryDao
) {
    suspend fun getRemoteMusicList(category: String = "all"): Result<List<MusicDto>> {
        return networkDataSource.getMusicList(category)
    }

    suspend fun addToFavorite(music: FavoriteMusic) {
        favoriteMusicDao.insert(music)
    }

    suspend fun removeFromFavorite(music: FavoriteMusic) {
        favoriteMusicDao.delete(music)
    }

    fun getAllFavorites(): Flow<List<FavoriteMusic>> {
        return favoriteMusicDao.getAllFavorites()
    }

    suspend fun isFavorite(musicId: String): Boolean {
        return favoriteMusicDao.isMusicFavorite(musicId) > 0
    }

    suspend fun getFavoriteById(musicId: String): FavoriteMusic? {
        return favoriteMusicDao.getMusicById(musicId)
    }

    suspend fun addCategory(category: MusicCategory) {
        categoryDao.insert(category)
    }

    fun getAllCategories(): Flow<List<MusicCategory>> {
        return categoryDao.getAllCategories()
    }

    suspend fun getCategoryIdByName(name: String): Int? {
        return categoryDao.getCategoryIdByName(name)?.id
    }
}
