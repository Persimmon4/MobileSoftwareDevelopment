package com.musiccollect.viewmodel

import com.musiccollect.data.entity.FavoriteMusic
import com.musiccollect.data.network.dto.MusicDto

sealed interface MusicUiState {
    object Loading : MusicUiState
    data class Success(val musicList: List<MusicDto>) : MusicUiState
    data class Error(val message: String) : MusicUiState
    object Empty : MusicUiState
}

sealed interface FavoriteUiState {
    object Loading : FavoriteUiState
    data class Success(val favoriteList: List<FavoriteMusic>) : FavoriteUiState
    data class Error(val message: String) : FavoriteUiState
    object Empty : FavoriteUiState
}

data class AppUiState(
    val darkMode: Boolean = false,
    val isLoading: Boolean = false,
    val snackbarMessage: String? = null
)
