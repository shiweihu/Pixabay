package com.shiweihu.pixabayapplication.videoPlayView

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
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
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.google.android.exoplayer2.ui.StyledPlayerView.SHOW_BUFFERING_WHEN_PLAYING
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.jeremyliao.liveeventbus.LiveEventBus
import com.shiweihu.pixabayapplication.BaseFragment
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.ActivityVideoPlayBinding
import com.shiweihu.pixabayapplication.databinding.LoadingDialogLayoutBinding
import com.shiweihu.pixabayapplication.event.ExoPlayerEvent
import com.shiweihu.pixabayapplication.viewArgu.VideoPlayArgu
import com.shiweihu.pixabayapplication.viewModle.FragmentComunicationViewModel
import com.shiweihu.pixabayapplication.viewModle.VideoPlayViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoPlayFragment:BaseFragment(

) {

    private val model: VideoPlayViewModel by viewModels()
    private val sharedModel: FragmentComunicationViewModel by activityViewModels()

    //private val args:VideoPlayFragmentArgs by navArgs()

    private var binding:ActivityVideoPlayBinding? = null

    private fun mHandler() = binding!!.root.handler

    private var data: VideoPlayArgu? = null


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

            binding.toolBar.setNavigationOnClickListener {
                sharedModel.videoItemPosition.postValue(player.currentMediaItemIndex)
                this.findNavController().navigateUp()
            }


            binding.shareBtn.setOnClickListener {
                player.pause()
                val permissionState = ActivityCompat.checkSelfPermission(this.requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if(permissionState == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                    showLoadingDialog()
                }else{
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }

            binding.fullScreenBtn.setOnClickListener {
                player.pause()
//                player.currentMediaItem?.let { mediaItem ->
//                    val uri = mediaItem.localConfiguration?.uri
//                    val position = player.currentPosition
//                    uri?.let {
//                        model.navigateToFullScreen(binding.playerView,it,position)
//                    }
//                }
                binding?.playerView?.player = null
                model.navigateToFullScreen(binding.playerView)
                LiveEventBus.get(ExoPlayerEvent::class.java).post(ExoPlayerEvent(player))
            }



            binding.playerView.player = player
            binding.playerView.controllerAutoShow = false



            player.repeatMode = REPEAT_MODE_ONE
            player.addListener(object: Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    initMenuAction(player.currentMediaItemIndex)
                    getAdRequest(player.currentMediaItemIndex).also {
                        binding.adView.loadAd(it)
                    }
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
        }
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

        TransitionInflater.from(this.requireContext()).inflateTransition(R.transition.video_shared_element_transition).also {
            sharedElementReturnTransition = it
        }
        sharedModel.videoPlayArguLiveData.observe(this){args ->
            this.data = args
            if(args.from == 1){
                binding?.pageProfile?.setImageResource(R.drawable.ic_pexels)
            }
            args.videos?.forEach {
                val item = MediaItem.fromUri(it)
                player.addMediaItem(item)
            }
            player.seekTo(args.currentIndex,0L)
            initMenuAction(args.currentIndex)
            player.prepare()
        }
    }

    fun getAdRequest(index:Int):AdRequest{
        var builder =  AdRequest.Builder()
        builder = data?.tags?.get(index)?.let {
            it.split(",").forEach { keyWord ->
                builder = if(keyWord.isEmpty()) builder else builder.addKeyword(keyWord)
            }
            builder
        } ?: builder
        return builder.build()
    }


    private fun initMenuAction(position:Int){
        binding?.userProfileUrl = data?.profiles?.get(position)
        binding?.pageProfile?.setOnClickListener {
            model.navigateToWeb(this@VideoPlayFragment.requireContext(),data?.pageUrls!![position])
        }
        binding?.userProfile?.setOnClickListener {
            when(data?.from){
                0 ->{
                    val username = data?.userNameArray?.get(position)
                    val userid = data?.useridArray?.get(position)
                    if(username != null && userid!=null){
                        model.navigateToUserProfilePage(this@VideoPlayFragment.requireContext(),username,userid)
                    }
                }
                1 ->{
                    val url = data?.useridArray?.get(position)
                    if(url != null){
                        model.navigateToUserProfilePageOnPexels(this.requireContext(),url)
                    }
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        model.onBindingCostomTabSever(this.requireActivity())
    }




    override fun onResume() {
        super.onResume()
        binding?.playerView?.player = player
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
        model.onUnBindingCostomTabSever(this.requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.stop()
        binding?.adView?.destroy()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    companion object{
        const val PLAYER_BACKGROUND = "video_background"
        const val PREMISSION_REQUEST_CODE = 123
    }

}