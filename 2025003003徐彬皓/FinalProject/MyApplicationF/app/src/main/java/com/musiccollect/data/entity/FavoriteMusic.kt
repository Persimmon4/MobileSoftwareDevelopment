package com.musiccollect.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_music")
data class FavoriteMusic(
    @PrimaryKey val id: String,
    val name: String,
    val singer: String,
    val album: String,
    val cover: String,
    val duration: Int,
    val categoryId: Int,
    val audioUrl: String = "",
    val coverRes: Int = 0,
    val audioRes: Int = 0,
    val addTime: Long = System.currentTimeMillis()
)
