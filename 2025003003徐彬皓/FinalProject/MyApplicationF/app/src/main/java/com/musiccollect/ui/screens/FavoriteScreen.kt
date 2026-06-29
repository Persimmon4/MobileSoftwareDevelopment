package com.musiccollect.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.musiccollect.data.database.AppDatabase
import com.musiccollect.data.network.NetworkDataSource
import com.musiccollect.data.repository.MusicRepository
import com.musiccollect.datastore.UserPreferencesRepo
import com.musiccollect.ui.components.EmptyView
import com.musiccollect.ui.components.LoadingView
import androidx.compose.material3.MaterialTheme
import com.musiccollect.ui.components.MusicItem
import com.musiccollect.viewmodel.FavoriteUiState
import com.musiccollect.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    onBackClick: () -> Unit,
    onMusicClick: (String) -> Unit
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val networkDataSource = NetworkDataSource()
    val userPreferencesRepo = UserPreferencesRepo.getInstance(context)
    val repository = MusicRepository(
        networkDataSource = networkDataSource,
        favoriteMusicDao = database.favoriteMusicDao(),
        categoryDao = database.musicCategoryDao()
    )

    val viewModel: MusicViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = MusicViewModel.provideFactory(repository, userPreferencesRepo)
    )

    val favoriteUiState = viewModel.favoriteUiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("我的收藏", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = favoriteUiState.value) {
                is FavoriteUiState.Loading -> {
                    LoadingView(modifier = Modifier.align(Alignment.Center))
                }
                is FavoriteUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(state.favoriteList) { music ->
                            MusicItem(
                                music = com.musiccollect.data.network.dto.MusicDto(
                                    id = music.id,
                                    name = music.name,
                                    singer = music.singer,
                                    album = music.album,
                                    cover = music.cover,
                                    duration = music.duration,
                                    category = "",
                                    audioUrl = music.audioUrl,
                                    coverRes = music.coverRes,
                                    audioRes = music.audioRes
                                ),
                                isFavorite = true,
                                onItemClick = { onMusicClick(music.id) },
                                onFavoriteClick = { viewModel.removeFromFavorite(music) }
                            )
                        }
                    }
                }
                is FavoriteUiState.Empty -> {
                    EmptyView(
                        message = "暂无收藏",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is FavoriteUiState.Error -> {
                    com.musiccollect.ui.components.ErrorView(
                        message = state.message,
                        onRetry = { viewModel.loadFavorites() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
