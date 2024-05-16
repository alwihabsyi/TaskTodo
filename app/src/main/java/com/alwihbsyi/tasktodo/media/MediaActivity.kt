package com.alwihbsyi.tasktodo.media

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.alwihbsyi.tasktodo.R
import com.alwihbsyi.tasktodo.databinding.ActivityMediaBinding

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra(VID_URL)?.let { url ->
            val mediaItem = MediaItem.fromUri(url)
            val player = ExoPlayer.Builder(this).build().also { exoPlayer ->
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
            }
            binding.playerView.player = player
        }
    }

    companion object {
        const val VID_URL = "url"
    }
}