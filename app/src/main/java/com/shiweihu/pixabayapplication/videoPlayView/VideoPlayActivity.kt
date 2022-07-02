package com.shiweihu.pixabayapplication.videoPlayView

import android.os.Bundle
import android.os.PersistableBundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.VideoPlayActivityLayoutBinding
import com.shiweihu.pixabayapplication.viewModle.FragmentComunicationViewModel
import com.shiweihu.pixabayapplication.viewModle.VideoPlayActivityViewModle
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class VideoPlayActivity :AppCompatActivity(){

    companion object{
        const val TRANSITION_NAME = "full_screen_player_background"
    }

    private val player by lazy {
        ExoPlayer.Builder(this).build()
    }

    private val args:VideoPlayActivityArgs by navArgs()
    private val modle: VideoPlayActivityViewModle by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = VideoPlayActivityLayoutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.returnBtn.setOnClickListener {
            this.finish()
            modle.videoPlayerPosition.position.postValue(player.currentPosition)
        }

        binding.playerView.player = player
        binding.playerView.transitionName = TRANSITION_NAME
        binding.playerView.controllerAutoShow = false

        binding.playerView.setControllerVisibilityListener(StyledPlayerView.ControllerVisibilityListener {
            binding.returnBtn.visibility = it
        })

        val mediaItem = MediaItem.Builder().setUri(args.uri).build()
        player.setMediaItem(mediaItem,args.position)

//        TransitionInflater.from(this).inflateTransition(R.transition.video_shared_element_activity_transition).also {
//            window.sharedElementEnterTransition = it
//            window.sharedElementExitTransition = it
//
//        }
        player.repeatMode = REPEAT_MODE_ONE
        player.prepare()

    }

    override fun onResume() {
        super.onResume()
        player.play()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onStop() {
        super.onStop()
        player.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}