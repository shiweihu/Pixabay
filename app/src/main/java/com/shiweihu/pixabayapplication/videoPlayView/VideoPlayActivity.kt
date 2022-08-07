package com.shiweihu.pixabayapplication.videoPlayView

import android.os.Bundle
import android.os.PersistableBundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.jeremyliao.liveeventbus.LiveEventBus
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.VideoPlayActivityLayoutBinding
import com.shiweihu.pixabayapplication.event.ExoPlayerEvent
import com.shiweihu.pixabayapplication.viewModle.FragmentComunicationViewModel
import com.shiweihu.pixabayapplication.viewModle.VideoPlayActivityViewModle
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class VideoPlayActivity :AppCompatActivity(){

    companion object{
        const val TRANSITION_NAME = "full_screen_player_background"
    }

    private var player:ExoPlayer? = null

   // private val args:VideoPlayActivityArgs by navArgs()
    private val modle: VideoPlayActivityViewModle by viewModels()

    private var bindingView:VideoPlayActivityLayoutBinding? = null
    private val binding:VideoPlayActivityLayoutBinding by lazy {
        bindingView!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingView = VideoPlayActivityLayoutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.returnBtn.setOnClickListener {
            this.finish()
        }

        binding.playerView.transitionName = TRANSITION_NAME




//        TransitionInflater.from(this).inflateTransition(R.transition.video_shared_element_activity_transition).also {
//            window.sharedElementEnterTransition = it
//            window.sharedElementExitTransition = it
//
//        }

        LiveEventBus.get(ExoPlayerEvent::class.java).observeSticky(this){
            player = it.player
            binding.playerView.player = player
            player?.play()
            binding.playerView.controllerAutoShow = false
            binding.playerView.setControllerVisibilityListener(StyledPlayerView.ControllerVisibilityListener {
                binding.returnBtn.visibility = it
            })
        }


    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onStop() {
        super.onStop()
    }


    override fun onDestroy() {
        super.onDestroy()
        bindingView?.playerView?.player = null
        bindingView = null
        player = null
      //  LiveEventBus.get(ExoPlayerEvent::class.java).removeObserver(observer)
    }
}