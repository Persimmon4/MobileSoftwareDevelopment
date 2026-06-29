package com.musiccollect.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.musiccollect.data.dao.FavoriteMusicDao
import com.musiccollect.data.dao.MusicCategoryDao
import com.musiccollect.data.dao.RecentlyPlayedDao
import com.musiccollect.data.entity.FavoriteMusic
import com.musiccollect.data.entity.MusicCategory
import com.musiccollect.data.entity.RecentlyPlayed

@Database(
    entities = [FavoriteMusic::class, MusicCategory::class, RecentlyPlayed::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteMusicDao(): FavoriteMusicDao
    abstract fun musicCategoryDao(): MusicCategoryDao
    abstract fun recentlyPlayedDao(): RecentlyPlayedDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "music_collect_db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
