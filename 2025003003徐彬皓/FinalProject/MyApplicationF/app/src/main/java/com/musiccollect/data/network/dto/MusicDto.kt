package com.musiccollect.data.network.dto

data class MusicResponse(
    val code: Int,
    val data: List<MusicDto>
)

data class MusicDto(
    val id: String,
    val name: String,
    val singer: String,
    val album: String,
    val cover: String,
    val duration: Int,
    val category: String,
    val audioUrl: String = "",
    val coverRes: Int = 0,
    val audioRes: Int = 0
)
