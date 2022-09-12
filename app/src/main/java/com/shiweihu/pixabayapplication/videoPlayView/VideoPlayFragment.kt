package com.shiweihu.pixabayapplication.videoPlayView

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.forEachIndexed
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import com.jeremyliao.liveeventbus.LiveEventBus
import com.shiweihu.pixabayapplication.BaseFragment
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.ActivityVideoPlayBinding
import com.shiweihu.pixabayapplication.databinding.LoadingDialogLayoutBinding
import com.shiweihu.pixabayapplication.event.ExoPlayerEvent
import com.shiweihu.pixabayapplication.utils.DisplayUtils
import com.shiweihu.pixabayapplication.video.VideoFragmentDirections
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

    private val viewBinding:ActivityVideoPlayBinding
    get() {
        return binding!!
    }
    private var adView:AdView? = null

    private val adSize: AdSize by lazy {
        val density =  this.requireContext().resources.displayMetrics.density

        var adWidthPixels = viewBinding.adViewLayout.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = DisplayUtils.ScreenWidth.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this.requireContext(), adWidth)
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
    ): View {
        binding = ActivityVideoPlayBinding.inflate(inflater,container,false).also{ binding ->


            ViewCompat.setTransitionName(binding.playerView, VideoPlayFragment.PLAYER_BACKGROUND)

            binding.toolBar.setNavigationOnClickListener {
                sharedModel.videoItemPosition.postValue(player.currentMediaItemIndex)
                this.findNavController().navigateUp()
            }
            initMenu(binding.toolBar.menu)


            binding.fullScreenBtn.setOnClickListener {
                binding.playerView.player = null
                model.navigateToFullScreen(binding.playerView)
                LiveEventBus.get(ExoPlayerEvent::class.java).post(ExoPlayerEvent(player))
            }

            binding.playerView.player = player
            binding.playerView.controllerAutoShow = false
            player.repeatMode = REPEAT_MODE_ONE
            player.addListener(object: Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
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
                        this@VideoPlayFragment.viewBinding.root.handler?.postDelayed({
                            player.prepare()
                            player.play()
                        },3000)

                    }
                }

            })
        }
        return viewBinding.root
    }

    private fun initAdMob(){
        getAdRequest().also {
            adView = AdView(this.requireContext())
            adView?.setAdSize(adSize)
            adView?.adUnitId = this.requireContext().getString(R.string.banner_id_for_video_play)
            adView?.loadAd(it)
            adView?.adListener = object : AdListener(){
                override fun onAdClosed() {
                    super.onAdClosed()
                    viewBinding.adViewLayout.visibility = View.GONE
                }
            }
            viewBinding.adViewLayout.addView(adView)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdMob()
    }

    private fun onShareAction(){
        player.pause()
        val permissionState = ActivityCompat.checkSelfPermission(this.requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(permissionState == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
            showLoadingDialog()
        }else{
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun onViewWebPage(){
        model.goToPage(this.requireContext(),player.currentMediaItemIndex)
    }
    private fun onUserWebPage(){
        model.goToUserPage(this.requireContext(),player.currentMediaItemIndex)
    }

    private fun viewVideoInSystem(uri:Uri){
        val intent = Intent(Intent.ACTION_VIEW).also {
            it.setDataAndType(uri,"video/mp4")
        }
        startActivity(intent)
    }
    private fun afterDownLoad(uri: Uri?){
        if(uri != null){
            Snackbar.make(viewBinding.root,R.string.download_successfully, Snackbar.LENGTH_LONG).setAction(R.string.view_it){
                viewVideoInSystem(uri)
            }.show()
        }else{
            Toast.makeText(this.requireContext(),R.string.download_failed,Toast.LENGTH_SHORT).show()
        }
    }
    private fun onDownloadAction(){
        val dialog = createLoadingDialog()
        dialog.show()
        player.currentMediaItem?.let {
            model.downloadVideo(this.requireContext(), it){ uri ->
                dialog.dismiss()
                afterDownLoad(uri)
            }
        }
    }

    private fun initMenu(menu: Menu){
        menu.forEachIndexed{_,item ->
            when(item.itemId){
                R.id.action_view_image_page ->{
                    item.setOnMenuItemClickListener {
                        onViewWebPage()
                        true
                    }
                }
                R.id.action_view_photographer_page ->{
                    item.setOnMenuItemClickListener {
                        onUserWebPage()
                        true
                    }
                }
                R.id.action_download ->{
                    item.setOnMenuItemClickListener {
                        onDownloadAction()
                        true
                    }
                }
                R.id.action_share_image ->{
                    item.setOnMenuItemClickListener {
                        onShareAction()
                        true
                    }
                }
            }
        }
    }

    private fun createLoadingDialog():AlertDialog{
        val builder = AlertDialog.Builder(this.requireContext(),R.style.Theme_PixabayApplication_LoadDialog)
        val loadingBinding = LoadingDialogLayoutBinding.inflate(LayoutInflater.from(this.requireContext()))
        builder.setView(loadingBinding.root)
        builder.setCancelable(false)
        return builder.create()
    }

    private fun showLoadingDialog(){
        val dialog = createLoadingDialog()
        dialog.show()
        model.shareVideo(this.requireContext(),player.currentMediaItem!!){
            dialog.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TransitionInflater.from(this.requireContext()).inflateTransition(R.transition.video_shared_element_transition).also {
            sharedElementReturnTransition = it
        }
        sharedModel.videoPlayArguLiveData.observe(this){args ->
            model.videoData = args
            initPlayer()
        }
    }
    private fun initPlayer(){
        model.videoData?.videos?.forEach {
            val item = MediaItem.fromUri(it)
            player.addMediaItem(item)
        }
        player.seekTo(model.videoData?.currentIndex!!,0L)
        player.prepare()
    }

    fun getAdRequest():AdRequest{
        return AdRequest.Builder().build()
    }




    override fun onStart() {
        super.onStart()
        model.onBindingCostomTabSever(this.requireActivity())
    }




    override fun onResume() {
        super.onResume()
        viewBinding.playerView.player = player
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
        viewBinding.root.handler?.removeCallbacksAndMessages(null)
        adView?.destroy()
        adView = null
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