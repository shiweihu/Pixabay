package com.shiweihu.pixabayapplication.videoPlayActivity

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.shiweihu.pixabayapplication.databinding.ActivityVideoPlayBinding


class VideoPlayActivity : AppCompatActivity() {


    private val args:VideoPlayActivityArgs by navArgs()

    private val data by lazy {
        args.data
    }

    private val player by lazy {
         ExoPlayer.Builder(this).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityVideoPlayBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.playerView.player = player

        data.videos?.forEach {
             val uri = Uri.parse(it)
             val item = MediaItem.fromUri(uri)
             player.addMediaItem(item)
        }
        player.prepare()
        player.play()




    }

}