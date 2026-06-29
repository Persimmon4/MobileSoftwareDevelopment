package com.musiccollect.player

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.util.Log
import com.musiccollect.data.network.dto.MusicDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class RepeatMode {
    NONE, ALL, ONE
}

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val currentSong: MusicDto? = null,
    val playlist: List<MusicDto> = emptyList(),
    val currentIndex: Int = -1,
    val playbackSpeed: Float = 1.0f,
    val repeatMode: RepeatMode = RepeatMode.ALL
)

class MusicPlayerManager private constructor(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var positionJob: Job? = null

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    var onSongChanged: ((MusicDto) -> Unit)? = null

    fun play(song: MusicDto) {
        releasePlayer()
        setupAndPlay(song)
    }

    fun playFromPlaylist(songs: List<MusicDto>, startIndex: Int) {
        if (startIndex !in songs.indices) return
        val song = songs[startIndex]
        _state.value = _state.value.copy(playlist = songs, currentIndex = startIndex)
        releasePlayer()
        setupAndPlay(song)
    }

    fun playNext(): Boolean {
        val s = _state.value
        if (s.playlist.isEmpty() || s.currentIndex < 0) return false
        if (s.repeatMode == RepeatMode.NONE && s.currentIndex >= s.playlist.lastIndex) return false
        val nextIndex = if (s.currentIndex >= s.playlist.lastIndex) 0 else s.currentIndex + 1
        val song = s.playlist[nextIndex]
        Log.d("MusicPlayer", "playNext: ${s.playlist[s.currentIndex].name} -> ${song.name} (idx $nextIndex)")
        _state.value = _state.value.copy(currentIndex = nextIndex)
        releasePlayer()
        setupAndPlay(song)
        return true
    }

    fun playPrevious(): Boolean {
        val s = _state.value
        if (s.playlist.isEmpty() || s.currentIndex <= 0) {
            Log.w("MusicPlayer", "playPrevious refused: isEmpty=${s.playlist.isEmpty()}, idx=${s.currentIndex}")
            return false
        }
        val prevIndex = s.currentIndex - 1
        val song = s.playlist[prevIndex]
        Log.d("MusicPlayer", "playPrevious: ${s.playlist[s.currentIndex].name} -> ${song.name} (idx $prevIndex)")
        _state.value = _state.value.copy(currentIndex = prevIndex)
        releasePlayer()
        setupAndPlay(song)
        return true
    }

    private fun setupAndPlay(song: MusicDto) {
        try {
            val resId = song.audioRes
            if (resId == 0) {
                Log.w("MusicPlayer", "No audio resource for: ${song.name}")
                _state.value = _state.value.copy(currentSong = song)
                return
            }

            Log.d("MusicPlayer", "Playing: ${song.name}, resId=$resId")
            val mp = MediaPlayer.create(context, resId)
            if (mp == null) {
                Log.e("MusicPlayer", "Failed to create MediaPlayer for: ${song.name}")
                _state.value = _state.value.copy(currentSong = song)
                return
            }

            mediaPlayer = mp
            mp.setOnCompletionListener {
                Log.d("MusicPlayer", "Song completed: ${song.name}")
                if (_state.value.repeatMode == RepeatMode.ONE) {
                    releasePlayer()
                    setupAndPlay(song)
                    return@setOnCompletionListener
                }
                stopPositionTracking()
                _state.value = _state.value.copy(isPlaying = false, currentPosition = 0)
                if (!playNext()) {
                    _state.value = _state.value.copy(currentSong = null, currentIndex = -1)
                }
            }

            mp.start()

            val speed = _state.value.playbackSpeed
            if (speed != 1.0f && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try { mp.playbackParams = PlaybackParams().setSpeed(speed) } catch (_: Exception) {}
            }

            _state.value = _state.value.copy(
                isPlaying = true,
                currentPosition = 0,
                duration = mp.duration.toLong(),
                currentSong = song
            )
            startPositionTracking()

            try {
                onSongChanged?.invoke(song)
            } catch (e: Exception) {
                Log.e("MusicPlayer", "onSongChanged error", e)
            }

        } catch (e: Exception) {
            Log.e("MusicPlayer", "Error playing ${song.name}: ${e.message}", e)
            _state.value = _state.value.copy(currentSong = song)
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer ?: return
        try {
            if (mp.isPlaying) {
                mp.pause()
                stopPositionTracking()
                _state.value = _state.value.copy(isPlaying = false)
            } else {
                mp.start()
                startPositionTracking()
                _state.value = _state.value.copy(isPlaying = true)
            }
        } catch (e: Exception) {
            Log.e("MusicPlayer", "togglePlayPause error", e)
        }
    }

    fun seekTo(positionMillis: Long) {
        try {
            mediaPlayer?.seekTo(positionMillis.toInt())
            _state.value = _state.value.copy(currentPosition = positionMillis)
        } catch (e: Exception) {
            Log.e("MusicPlayer", "seekTo error", e)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _state.value = _state.value.copy(playbackSpeed = speed)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer?.let { mp ->
                    mp.playbackParams = PlaybackParams().setSpeed(speed)
                }
            }
        } catch (e: Exception) {
            Log.e("MusicPlayer", "setPlaybackSpeed error", e)
        }
    }

    fun toggleRepeatMode() {
        _state.value = _state.value.copy(
            repeatMode = when (_state.value.repeatMode) {
                RepeatMode.NONE -> RepeatMode.ALL
                RepeatMode.ALL -> RepeatMode.ONE
                RepeatMode.ONE -> RepeatMode.NONE
            }
        )
    }

    private fun startPositionTracking() {
        positionJob?.cancel()
        positionJob = scope.launch {
            while (true) {
                try {
                    mediaPlayer?.let { mp ->
                        if (mp.isPlaying) {
                            _state.value = _state.value.copy(
                                currentPosition = mp.currentPosition.toLong()
                            )
                        }
                    }
                } catch (_: Exception) {}
                delay(250)
            }
        }
    }

    private fun stopPositionTracking() {
        positionJob?.cancel()
        positionJob = null
    }

    private fun releasePlayer() {
        stopPositionTracking()
        mediaPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
                it.release()
            } catch (_: Exception) {}
        }
        mediaPlayer = null
    }

    companion object {
        @Volatile
        private var INSTANCE: MusicPlayerManager? = null

        fun getInstance(context: Context): MusicPlayerManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MusicPlayerManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}
