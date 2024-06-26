package com.music.stream.neptune.di

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

object SongPlayer {
    private var player: ExoPlayer? = null


    fun playSong(song: String, context: Context) {
        if (player == null) {
            player = ExoPlayer.Builder(context).build()
        }
        val mediaItem = MediaItem.fromUri(song)
        player!!.setMediaItem(mediaItem)
        player!!.prepare()
        player!!.playWhenReady = true


    }

    fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }

    fun play() {
        player!!.play()
    }

    fun pause() {
        player!!.playWhenReady = false
    }

    fun stop() {
        player!!.stop()
    }

    fun seekTo(position: Long) {
        player!!.seekTo(position)
    }

    fun release() {
        player?.release()
        player = null
    }

    fun getDuration(): Long {
        return player?.duration ?: 0L
    }

    fun getCurrentPosition(): Long {
        return player?.currentPosition ?: 0L
    }

    fun isPrepared(): Boolean {
        val playerState = player?.playbackState
        return playerState != null && playerState != ExoPlayer.STATE_IDLE && playerState != ExoPlayer.STATE_ENDED
    }

}
