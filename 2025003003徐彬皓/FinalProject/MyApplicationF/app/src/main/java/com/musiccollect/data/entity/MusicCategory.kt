package com.musiccollect.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_category")
data class MusicCategory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val icon: String = "default_icon"
)
