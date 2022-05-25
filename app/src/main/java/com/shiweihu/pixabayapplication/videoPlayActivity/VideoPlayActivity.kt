package com.shiweihu.pixabayapplication.videoPlayActivity

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.transformer.Transformer
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.shiweihu.pixabayapplication.MyApplication
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.ActivityVideoPlayBinding
import com.shiweihu.pixabayapplication.databinding.LoadingDialogLayoutBinding
import com.shiweihu.pixabayapplication.viewModle.VideoPlayViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.security.Permission

@AndroidEntryPoint
class VideoPlayActivity(

): AppCompatActivity() {

    init {
        Log.println(Log.DEBUG,"","")
    }


    private val model: VideoPlayViewModel by viewModels()

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
        binding.playerView.controllerAutoShow = false

        binding.shareBtn.setOnClickListener {
            player.pause()
            val builder = AlertDialog.Builder(this,R.style.Theme_PixabayApplication_LoadDialog)
            val loadingBinding = LoadingDialogLayoutBinding.inflate(LayoutInflater.from(this))
            builder.setView(loadingBinding.root)
            builder.setCancelable(false)
            val dialog =builder.create()
            dialog.show()
            model.downloadVideo(this,player.currentMediaItem!!){
                dialog.dismiss()
            }


        }

        binding.playerView.setControllerVisibilityListener {
            binding.appBar.visibility = it
            binding.shareBtn.visibility = if(it == View.GONE) View.VISIBLE else View.GONE
        }



        data.videos?.forEach {
             val item = MediaItem.fromUri(it)
             player.addMediaItem(item)
        }


        Log.println(Log.DEBUG,"video url", data.videos!![data.currentIndex])
        player.seekTo(data.currentIndex,0L)
        player.repeatMode = REPEAT_MODE_ONE
        player.addListener(object: Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                initMenuAction(player.currentMediaItemIndex)
            }

            override fun onMetadata(metadata: Metadata) {
                super.onMetadata(metadata)
            }



            override fun onPlayerError(error: PlaybackException) {
                Toast.makeText(this@VideoPlayActivity,error.message,Toast.LENGTH_LONG).show()
                if(player.hasNextMediaItem()){
                    player.seekToNextMediaItem()
                    mHandler.postDelayed({
                        player.prepare()
                        player.play()
                    },3000)

                }
            }

        })
        player.prepare()
        initMenuAction(data.currentIndex)
        MobileAds.initialize(this) {
            AdRequest.Builder().build().also {
                if(binding.root.isActivated){
                    binding.adView.loadAd(it)
                }
            }
        }


    }


    private fun initMenuAction(position:Int){
        binding.userProfileUrl = data.profiles?.get(position)
        binding.pageProfile.setOnClickListener {
            model.navigateToWeb(this@VideoPlayActivity,data.pageUrls!![position])
        }
        binding.userProfile.setOnClickListener {
            val username = data.userNameArray?.get(position)
            val userid = data.useridArray?.get(position)
            if(username != null && userid!=null){
                model.navigateToUserProfilePage(this@VideoPlayActivity,username,userid)
            }
        }
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
        player.stop()
        player.release()
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
        binding.adView.destroy()
    }

}