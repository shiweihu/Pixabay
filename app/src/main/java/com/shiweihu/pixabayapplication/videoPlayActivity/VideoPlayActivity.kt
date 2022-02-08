package com.shiweihu.pixabayapplication.videoPlayActivity

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.util.MimeTypes
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.ActivityVideoPlayBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoPlayActivity(

): AppCompatActivity() {

    init {
        Log.println(Log.DEBUG,"","")
    }



    private val args:VideoPlayActivityArgs by navArgs()

    private lateinit var binding:ActivityVideoPlayBinding

    private val mHandler by lazy {
        binding.root.handler
    }



    private val data by lazy {
        args.data
    }

    private val player by lazy {
         ExoPlayer.Builder(this).build()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.toolBar.setNavigationOnClickListener {
            this.finish()
        }
        binding.playerView.player = player
        binding.playerView.setControllerVisibilityListener {
            binding.toolBar.visibility = it

        }

        data.videos?.forEach {
             val item = MediaItem.fromUri(it)
             player.addMediaItem(item)
        }
        Log.println(Log.DEBUG,"video url", data.videos!![data.currentIndex])
        player.seekTo(data.currentIndex,0L)
        player.repeatMode = REPEAT_MODE_ONE
        player.prepare()
        player.addListener(object: Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                val url =  player.currentMediaItem?.localConfiguration?.uri.toString()
                Toast.makeText(this@VideoPlayActivity,url,Toast.LENGTH_LONG).show()
                if(player.hasNextMediaItem()){
                    player.seekToNextMediaItem()
                    mHandler.postDelayed({
                        player.prepare()
                        player.play()
                    },3000)

                }
            }

        })
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onRestart() {
        super.onRestart()
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
        mHandler.removeCallbacksAndMessages(null)
        player.stop()
        player.release()
    }

}