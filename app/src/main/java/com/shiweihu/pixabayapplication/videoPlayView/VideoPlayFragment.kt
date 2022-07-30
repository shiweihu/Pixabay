package com.shiweihu.pixabayapplication.videoPlayView

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.shiweihu.pixabayapplication.BaseFragment
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.ActivityVideoPlayBinding
import com.shiweihu.pixabayapplication.databinding.LoadingDialogLayoutBinding
import com.shiweihu.pixabayapplication.viewModle.FragmentComunicationViewModel
import com.shiweihu.pixabayapplication.viewModle.VideoPlayViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoPlayFragment:BaseFragment(

) {

    private val model: VideoPlayViewModel by viewModels()
    private val sharedModel: FragmentComunicationViewModel by activityViewModels()

    private val args:VideoPlayFragmentArgs by navArgs()

    private var binding:ActivityVideoPlayBinding? = null

    private fun mHandler() = binding!!.root.handler





    private val data by lazy {
        args.data
    }

    private val player by lazy {
         ExoPlayer.Builder(this.requireContext()).build()
    }

    private val requestPermissionLauncher =registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showLoadingDialog()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Snackbar.make(binding!!.root,R.string.permission_deny_notice,Snackbar.LENGTH_LONG).setAction(R.string.go){
                    getAppDetailSettingIntent()
                }.show()
            }
        }

    /**
     * navigate to application setting view.
     */
    private fun getAppDetailSettingIntent() {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.parse("package:${requireActivity().packageName}")
        startActivity(intent)
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityVideoPlayBinding.inflate(inflater,container,false).also{ binding ->


            ViewCompat.setTransitionName(binding.playerView, VideoPlayFragment.PLAYER_BACKGROUND)
            if(args.data.from == 1){
                 binding.pageProfile.setImageResource(R.drawable.ic_pexels)
            }
            binding.toolBar.setNavigationOnClickListener {
                sharedModel.videoItemPosition.postValue(player.currentMediaItemIndex)
                this.findNavController().navigateUp()
            }

            binding.fullScreenBtn.setOnClickListener {
                player.pause()
                player.currentMediaItem?.let { mediaItem ->
                    val uri = mediaItem.localConfiguration?.uri
                    val position = player.currentPosition
                    uri?.let {
                        model.navigateToFullScreen(binding.playerView,it,position)
                    }

                }
            }



            binding.shareBtn.setOnClickListener {
                player.pause()
                val permissionState = ActivityCompat.checkSelfPermission(this.requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if(permissionState == PackageManager.PERMISSION_GRANTED){
                    showLoadingDialog()
                }else{
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }


            }




            binding.playerView.player = player
            binding.playerView.controllerAutoShow = false
//            binding.playerView.setControllerVisibilityListener(StyledPlayerView.ControllerVisibilityListener {
//                binding.appBar.visibility = it
//                binding.shareBtn.visibility = it
//                binding.adView.visibility = it
//            })
            data.videos?.forEach {
                val item = MediaItem.fromUri(it)
                player.addMediaItem(item)
            }

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

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if(playbackState == STATE_READY){
                        //startPostponedEnterTransition()
                    }

                }



                override fun onPlayerError(error: PlaybackException) {
                    Toast.makeText(this@VideoPlayFragment.requireContext(),error.message,Toast.LENGTH_LONG).show()
                    if(player.hasNextMediaItem()){
                        player.seekToNextMediaItem()
                        mHandler().postDelayed({
                            player.prepare()
                            player.play()
                        },3000)

                    }
                }

            })

            AdRequest.Builder().build().also {
                binding.adView.loadAd(it)
            }
        }
        initMenuAction(data.currentIndex)
        player.prepare()
        //postponeEnterTransition()
        return binding?.root
    }

    private fun showLoadingDialog(){
        val builder = AlertDialog.Builder(this.requireContext(),R.style.Theme_PixabayApplication_LoadDialog)
        val loadingBinding = LoadingDialogLayoutBinding.inflate(LayoutInflater.from(this.requireContext()))
        builder.setView(loadingBinding.root)
        builder.setCancelable(false)
        val dialog =builder.create()
        dialog.show()
        model.downloadVideo(this.requireContext(),player.currentMediaItem!!){
            dialog.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        sharedElementEnterTransition = TransitionInflater.from(this.requireContext()).inflateTransition(R.transition.video_shared_element_transition).also {
//            it.addListener(object: android.transition.Transition.TransitionListener {
//                override fun onTransitionStart(p0: android.transition.Transition?) {
//
//                }
//
//                override fun onTransitionEnd(p0: android.transition.Transition?) {
//                    player.play()
//                }
//
//                override fun onTransitionCancel(p0: android.transition.Transition?) {
//
//                }
//
//                override fun onTransitionPause(p0: android.transition.Transition?) {
//
//                }
//
//                override fun onTransitionResume(p0: android.transition.Transition?) {
//
//                }
//
//            })
//        }

        TransitionInflater.from(this.requireContext()).inflateTransition(R.transition.video_shared_element_transition).also {
            sharedElementReturnTransition = it
        }
        model.videoPlayerPosition.position.value = 0
        model.videoPlayerPosition.position.observe(this){
            player.seekTo(it)
        }


    }


    private fun initMenuAction(position:Int){
        binding?.userProfileUrl = data.profiles?.get(position)
        binding?.pageProfile?.setOnClickListener {
            model.navigateToWeb(this@VideoPlayFragment.requireContext(),data.pageUrls!![position])
        }
        binding?.userProfile?.setOnClickListener {
            when(args.data.from){
                0 ->{
                    val username = data.userNameArray?.get(position)
                    val userid = data.useridArray?.get(position)
                    if(username != null && userid!=null){
                        model.navigateToUserProfilePage(this@VideoPlayFragment.requireContext(),username,userid)
                    }
                }
                1 ->{
                    val url = data.useridArray?.get(position)
                    if(url != null){
                        model.navigateToUserProfilePageOnPexels(this.requireContext(),url)
                    }
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()

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

    override fun onDestroyView() {
        super.onDestroyView()
        player.stop()
        player.release()
        binding?.adView?.destroy()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    companion object{
        const val PLAYER_BACKGROUND = "video_background"
        const val PREMISSION_REQUEST_CODE = 123
    }

}