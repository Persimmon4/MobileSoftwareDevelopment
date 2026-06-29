package com.musiccollect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.musiccollect.data.entity.FavoriteMusic
import com.musiccollect.data.entity.MusicCategory
import com.musiccollect.data.network.dto.MusicDto
import com.musiccollect.data.repository.MusicRepository
import com.musiccollect.datastore.UserPreferencesRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MusicViewModel(
    private val repository: MusicRepository,
    private val userPreferencesRepo: UserPreferencesRepo
) : ViewModel() {
    private val _musicUiState = MutableStateFlow<MusicUiState>(MusicUiState.Loading)
    val musicUiState: StateFlow<MusicUiState> = _musicUiState.asStateFlow()

    private val _favoriteUiState = MutableStateFlow<FavoriteUiState>(FavoriteUiState.Loading)
    val favoriteUiState: StateFlow<FavoriteUiState> = _favoriteUiState.asStateFlow()

    private val _appUiState = MutableStateFlow(AppUiState())
    val appUiState: StateFlow<AppUiState> = _appUiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepo.isDarkMode.collect { isDark ->
                _appUiState.update { it.copy(darkMode = isDark) }
            }
        }

        loadMusicList()
        loadFavorites()
        initDefaultCategories()
    }

    fun loadMusicList(category: String = "all") {
        _musicUiState.value = MusicUiState.Loading
        viewModelScope.launch {
            val result = repository.getRemoteMusicList(category)
            _musicUiState.value = when {
                result.isSuccess && result.getOrNull().isNullOrEmpty() -> MusicUiState.Empty
                result.isSuccess -> MusicUiState.Success(result.getOrNull() ?: emptyList())
                else -> MusicUiState.Error(result.exceptionOrNull()?.message ?: "加载失败")
            }
        }
    }

    fun loadFavorites() {
        _favoriteUiState.value = FavoriteUiState.Loading
        viewModelScope.launch {
            repository.getAllFavorites().collect { favorites ->
                _favoriteUiState.value = if (favorites.isEmpty()) {
                    FavoriteUiState.Empty
                } else {
                    FavoriteUiState.Success(favorites)
                }
            }
        }
    }

    fun addToFavorite(musicDto: MusicDto) {
        viewModelScope.launch {
            val categoryId = repository.getCategoryIdByName(musicDto.category) ?: 1
            val favoriteMusic = FavoriteMusic(
                id = musicDto.id,
                name = musicDto.name,
                singer = musicDto.singer,
                album = musicDto.album,
                cover = musicDto.cover,
                duration = musicDto.duration,
                categoryId = categoryId,
                audioUrl = musicDto.audioUrl,
                coverRes = musicDto.coverRes,
                audioRes = musicDto.audioRes
            )
            val alreadyFavorite = repository.isFavorite(musicDto.id)
            repository.addToFavorite(favoriteMusic)
            if (alreadyFavorite) {
                showSnackbar("已在收藏列表中")
            } else {
                showSnackbar("收藏成功")
            }
            loadFavorites()
        }
    }

    fun removeFromFavorite(music: FavoriteMusic) {
        viewModelScope.launch {
            repository.removeFromFavorite(music)
            showSnackbar("取消收藏")
            loadFavorites()
        }
    }

    fun removeFavoriteById(musicId: String) {
        viewModelScope.launch {
            val music = repository.getFavoriteById(musicId)
            if (music != null) {
                repository.removeFromFavorite(music)
                showSnackbar("取消收藏")
                loadFavorites()
            }
        }
    }

    fun isFavorite(musicId: String): Boolean {
        return _favoriteUiState.value.let { state ->
            when (state) {
                is FavoriteUiState.Success -> state.favoriteList.any { it.id == musicId }
                else -> false
            }
        }
    }

    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            userPreferencesRepo.saveDarkModePreference(isDark)
            _appUiState.update { it.copy(darkMode = isDark) }
        }
    }

    private fun initDefaultCategories() {
        viewModelScope.launch {
            val categories = listOf(
                MusicCategory(name = "流行"),
                MusicCategory(name = "摇滚"),
                MusicCategory(name = "古典"),
                MusicCategory(name = "民谣"),
                MusicCategory(name = "中国风")
            )
            categories.forEach { category ->
                val existing = repository.getCategoryIdByName(category.name)
                if (existing == null) {
                    repository.addCategory(category)
                }
            }
        }
    }

    fun showSnackbar(message: String) {
        _appUiState.update { it.copy(snackbarMessage = message) }
    }

    fun clearSnackbar() {
        _appUiState.update { it.copy(snackbarMessage = null) }
    }

    companion object {
        fun provideFactory(
            repository: MusicRepository,
            userPreferencesRepo: UserPreferencesRepo
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MusicViewModel::class.java)) {
                        return MusicViewModel(repository, userPreferencesRepo) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
