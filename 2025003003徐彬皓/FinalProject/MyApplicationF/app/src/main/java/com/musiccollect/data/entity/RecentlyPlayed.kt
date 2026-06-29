package com.musiccollect.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_played")
data class RecentlyPlayed(
    @PrimaryKey val musicId: String,
    val name: String,
    val singer: String,
    val album: String,
    val category: String,
    val duration: Int = 0,
    val coverRes: Int = 0,
    val audioRes: Int = 0,
    val playedAt: Long = System.currentTimeMillis()
)
