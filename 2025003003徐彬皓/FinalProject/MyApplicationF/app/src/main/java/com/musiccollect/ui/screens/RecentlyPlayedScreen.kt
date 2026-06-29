package com.musiccollect.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.musiccollect.data.database.AppDatabase
import com.musiccollect.data.entity.RecentlyPlayed
import com.musiccollect.ui.components.EmptyView
import androidx.compose.material3.MaterialTheme
import com.musiccollect.ui.components.MusicItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentlyPlayedScreen(
    onBackClick: () -> Unit,
    onMusicClick: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var recentList by remember { mutableStateOf<List<RecentlyPlayed>>(emptyList()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getInstance(context)
            db.recentlyPlayedDao().getAll().collect { list ->
                recentList = list
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("最近播放", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    if (recentList.isNotEmpty()) {
                        IconButton(onClick = {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    AppDatabase.getInstance(context).recentlyPlayedDao().clearAll()
                                }
                                recentList = emptyList()
                            }
                        }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "清空记录", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
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
            if (recentList.isEmpty()) {
                EmptyView(
                    message = "暂无播放记录",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(recentList) { recent ->
                        val musicDto = com.musiccollect.data.network.dto.MusicDto(
                            id = recent.musicId,
                            name = recent.name,
                            singer = recent.singer,
                            album = recent.album,
                            cover = "",
                            duration = recent.duration,
                            category = recent.category,
                            coverRes = recent.coverRes,
                            audioRes = recent.audioRes
                        )
                        MusicItem(
                            music = musicDto,
                            isFavorite = false,
                            onItemClick = { onMusicClick(recent.musicId) },
                            onFavoriteClick = { }
                        )
                    }
                }
            }
        }
    }
}
