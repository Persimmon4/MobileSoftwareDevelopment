package com.musiccollect.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.musiccollect.data.network.dto.MusicDto

@Composable
fun MusicCoverImage(
    music: MusicDto,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: Dp = 64.dp
) {
    if (music.coverRes != 0) {
        Image(
            painter = painterResource(id = music.coverRes),
            contentDescription = contentDescription ?: "${music.name}封面",
            modifier = modifier.size(size),
            contentScale = ContentScale.Crop
        )
    } else {
        AsyncImage(
            model = music.cover,
            contentDescription = contentDescription ?: "${music.name}封面",
            modifier = modifier.size(size),
            contentScale = ContentScale.Crop
        )
    }
}
