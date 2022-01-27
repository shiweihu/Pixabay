package com.shiweihu.pixabayapplication.videoPlayActivity

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes
import com.shiweihu.pixabayapplication.R
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

        binding.toolBar.setNavigationOnClickListener {
            this.finish()
        }

        binding.playerView.player = player

        data.videos?.forEach {
             val uri = Uri.parse(it)
             val item = MediaItem.fromUri(uri)
             player.addMediaItem(item)
        }

//        val item = MediaItem.Builder().also {
//            it.setUri(data.videos?.get(0))
//            it.setMimeType(MimeTypes.APPLICATION_MP4)
//        }
//        player.addMediaItem(item.build())

        player.seekTo(data.currentIndex,0L)

        player.prepare()

    }

    override fun onResume() {
        super.onResume()
        player.play()
    }

    override fun onPause() {
        super.onPause()
        if(player.isPlaying){
            player.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        if(player.isPlaying){
            player.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(player.isPlaying){
            player.stop()
        }

    }

}