package com.musiccollect.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musiccollect.data.database.AppDatabase
import com.musiccollect.data.entity.FavoriteMusic
import com.musiccollect.data.entity.RecentlyPlayed
import com.musiccollect.data.network.NetworkDataSource
import com.musiccollect.data.network.dto.MusicDto
import com.musiccollect.player.MusicPlayerManager
import com.musiccollect.ui.components.LoadingView
import com.musiccollect.ui.components.PlayerControls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    musicId: String,
    onBackClick: () -> Unit
) {
    var music by remember { mutableStateOf<MusicDto?>(null) }
    var isFavorite by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val playerManager = MusicPlayerManager.getInstance(context)
    val playerState by playerManager.state.collectAsState()

    LaunchedEffect(musicId) {
        withContext(Dispatchers.IO) {
            val networkSource = NetworkDataSource()
            music = networkSource.getMusicById(musicId)
            val db = AppDatabase.getInstance(context)
            isFavorite = db.favoriteMusicDao().isMusicFavorite(musicId) > 0
        }
        isLoading = false
    }

    val currentPlayingId = playerState.currentSong?.id
    LaunchedEffect(currentPlayingId) {
        if (currentPlayingId != null) {
            withContext(Dispatchers.IO) {
                try {
                    val song = playerState.currentSong ?: return@withContext
                    AppDatabase.getInstance(context).recentlyPlayedDao().insert(
                        RecentlyPlayed(
                            musicId = song.id,
                            name = song.name,
                            singer = song.singer,
                            album = song.album,
                            category = song.category,
                            duration = song.duration,
                            coverRes = song.coverRes,
                            audioRes = song.audioRes
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { playerState.currentSong?.id }
            .drop(1)
            .collect { songId ->
                if (songId != null) {
                    withContext(Dispatchers.IO) {
                        music = NetworkDataSource().getMusicById(songId)
                        isFavorite = AppDatabase.getInstance(context).favoriteMusicDao().isMusicFavorite(songId) > 0
                    }
                }
            }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("音乐详情", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    if (music != null) {
                        IconButton(onClick = {
                            val db = AppDatabase.getInstance(context)
                            val musicItem = music ?: return@IconButton
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    try {
                                        val entity = FavoriteMusic(
                                            id = musicItem.id,
                                            name = musicItem.name,
                                            singer = musicItem.singer,
                                            album = musicItem.album,
                                            cover = musicItem.cover,
                                            duration = musicItem.duration,
                                            categoryId = 1,
                                            audioUrl = musicItem.audioUrl,
                                            coverRes = musicItem.coverRes,
                                            audioRes = musicItem.audioRes
                                        )
                                        if (isFavorite) {
                                            db.favoriteMusicDao().delete(entity)
                                        } else {
                                            db.favoriteMusicDao().insert(entity)
                                        }
                                        isFavorite = db.favoriteMusicDao().isMusicFavorite(musicId) > 0
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "取消收藏" else "收藏",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            LoadingView()
        } else if (music != null) {
            val song = music!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                com.musiccollect.ui.components.MusicCoverImage(
                    music = song,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Text(
                    text = song.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "歌手: ${song.singer}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "专辑: ${song.album}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "分类: ${song.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (playerState.currentSong != null) {
                    PlayerControls()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        try {
                            val allSongs = NetworkDataSource().getAllMusic()
                            val currentId = song.id
                            val index = allSongs.indexOfFirst { it.id == currentId }
                            if (index >= 0) {
                                playerManager.playFromPlaylist(allSongs, index)
                            } else {
                                playerManager.play(song)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                    val btnText = if (playerState.currentSong?.id == song.id) "正在播放" else "播放"
                    Text(
                        text = btnText,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("未找到该音乐", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
