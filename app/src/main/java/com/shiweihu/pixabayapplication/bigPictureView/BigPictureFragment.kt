package com.shiweihu.pixabayapplication.bigPictureView

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.shiweihu.pixabayapplication.BaseFragment
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.FragmentBigPictureBinding
import com.shiweihu.pixabayapplication.viewModle.BigPictureViewModel
import com.shiweihu.pixabayapplication.viewModle.FragmentComunicationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.set


@AndroidEntryPoint
class BigPictureFragment : BaseFragment() {


    private val modle:BigPictureViewModel by viewModels()
    private val activeModel: FragmentComunicationViewModel by activityViewModels()

    //private val args:BigPictureFragmentArgs by navArgs()

    private  var binding:FragmentBigPictureBinding? = null

    private var scrollPageCount:Int = 0

    private val myTrace = Firebase.performance.newTrace("BigPicture view trace")



    private val requestPermissionLauncher =registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            shareOnClick()
        } else {
            // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
            Snackbar.make(binding!!.root,R.string.permission_deny_notice, Snackbar.LENGTH_LONG).setAction(R.string.go){
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
        intent.resolveActivity(this.requireActivity().packageManager)?.let {
            startActivity(intent)
        }



    }



    private val shareToInstagram = registerForActivityResult(object :ActivityResultContract<Uri,Unit>(){
        override fun createIntent(context: Context, input: Uri): Intent {
            val intent = Intent(Intent.ACTION_SEND).also {
                    it.type = "image/*"
                    // Add the URI to the Intent.
                    it.putExtra(Intent.EXTRA_STREAM, input)
            }
            return Intent.createChooser(intent,this@BigPictureFragment.resources.getString(R.string.app_choser_title))
        }

        override fun parseResult(resultCode: Int, intent: Intent?) {
        }

    }) {

    }


    override fun onBackKeyPressed() {
        super.onBackKeyPressed()
        navigateUp()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activeModel.bigPictureArguLiveData.observe(this){
            modle.bigPictureArgu = it
            binding?.from = it.from
            val pageAdapter = BigPictureAdapter(it,this)
            binding?.viewPage?.adapter = pageAdapter
            binding?.viewPage?.setCurrentItem(it.currentIndex,false)
        }

    }

    private fun loadBanerAd(index:Int){
        modle.getAdRequest(index).also { adRequest ->
            binding?.adView?.loadAd(adRequest)
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myTrace.start()
        // Inflate the layout for this fragment
        binding =  FragmentBigPictureBinding.inflate(inflater,container,false).also {


            it.viewPage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    modle.onPageChange(it,position,this@BigPictureFragment.requireActivity())
                    loadBanerAd(position)
                }
            })
            it.userProfile.setOnClickListener { view ->
                modle.userProfileOnClick(view.context,it.viewPage.currentItem)
            }
            it.pageProfile.setOnClickListener { view ->
                modle.pageProfileOnClick(view.context,it.viewPage.currentItem)
            }


            it.viewPage.offscreenPageLimit = 4
            it.toolBar.setNavigationOnClickListener { _ ->
                navigateUp()
            }

            it.shareToInstagramFeed.setOnClickListener {_ ->
                shareOnClick()
            }
            initTransition()
        }
        postponeEnterTransition(resources.getInteger(R.integer.post_pone_time).toLong(), TimeUnit.MILLISECONDS)
        return binding?.root
    }

    private fun shareOnClick(){
        val permissionState = ActivityCompat.checkSelfPermission(this.requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(permissionState == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
            modle.getShareUrl(binding!!.viewPage.currentItem)?.also { url ->
                shareImageToInstagram(url)
            }
        }else{
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }


    fun getOutPutUri(bitmap:Bitmap):Uri?{
        val content = ContentValues()
        content.put(MediaStore.Images.Media.DISPLAY_NAME,UUID.randomUUID().toString())
        content.put(MediaStore.Images.Media.MIME_TYPE,"image/png")
        val outputUri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,content)
        if (outputUri != null) {
            requireContext().contentResolver.openOutputStream(outputUri).also { output ->
                bitmap.compress(Bitmap.CompressFormat.PNG,100,output)
            }
        }
        return outputUri
    }

    private fun shareImageToInstagram(url:String){
        Uri.parse(url).also {
            Glide.with(this.requireContext()).asBitmap().load(it).into(object:CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    getOutPutUri(resource)?.also { uri ->
                        shareToInstagram.launch(uri)
                    }
                }
                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
        }
    }



    private fun navigateUp(){
        activeModel.pictureItemPosition.postValue(binding?.viewPage?.currentItem)
        findNavController().navigateUp()
    }

    private fun initTransition(){
        setEnterSharedElementCallback(object:SharedElementCallback(){
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                super.onMapSharedElements(names, sharedElements)
                val view = binding?.viewPage?.findViewWithTag<ImageView>( "${BigPictureFragment.SHARE_ELEMENT_NAME}-${binding?.viewPage?.currentItem}")
                if(names != null && sharedElements!=null && view!=null){
                    sharedElements[names[0]] = view
                }
            }
        })
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.image_shared_element_transition)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.image_shared_element_transition)


    }

    override fun onStart() {
        super.onStart()
        modle.onBindingCostomTabSever(this.requireActivity())
    }

    override fun onStop() {
        super.onStop()
        modle.onUnBindingCostomTabSever(this.requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.viewPage?.adapter = null
        binding?.adView?.destroy()
        binding = null
    }

    override fun onResume() {
        super.onResume()
        myTrace.stop()
    }

    override fun onDestroy() {
        super.onDestroy()

    }



    companion object {
        const val SHARE_ELEMENT_NAME = "image_view_transition_name"
    }
}