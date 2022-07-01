package com.shiweihu.pixabayapplication.bigPictureView

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.ads.AdRequest
import com.shiweihu.pixabayapplication.BaseFragment
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.FragmentBigPictureBinding
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu
import com.shiweihu.pixabayapplication.viewModle.BigPictureViewModel
import com.shiweihu.pixabayapplication.viewModle.FragmentComunicationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.set


@AndroidEntryPoint
class BigPictureFragment : BaseFragment() {


    private val modle:BigPictureViewModel by viewModels()
    private val activeModel: FragmentComunicationViewModel by activityViewModels()

    private val args:BigPictureFragmentArgs by navArgs()

    private  var binding:FragmentBigPictureBinding? = null

    private var scrollPageCount:Int = 0


    private val shareToInstagram = registerForActivityResult(object :ActivityResultContract<Bitmap,Unit>(){
        var temp_input:Uri? = null

        fun getOutPutUri(bitmap:Bitmap):Uri{
            val content = ContentValues()
            content.put(MediaStore.Images.Media.DISPLAY_NAME,UUID.randomUUID().toString())
            content.put(MediaStore.Images.Media.MIME_TYPE,"image/png")
            val outputUri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,content)

            if (outputUri != null) {
                requireContext().contentResolver.openOutputStream(outputUri).also { output ->
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,output)
                }
            }
            return outputUri!!
        }


        override fun createIntent(context: Context, input: Bitmap): Intent {
             val intent = Intent(Intent.ACTION_SEND).also {
                 it.type = "image/*"
                 // Add the URI to the Intent.
                 val outputUri = getOutPutUri(input)
                 it.putExtra(Intent.EXTRA_STREAM, outputUri)
                 temp_input = outputUri
             }
            return Intent.createChooser(intent,this@BigPictureFragment.resources.getString(R.string.app_choser_title))
        }

        override fun parseResult(resultCode: Int, intent: Intent?) {
            if(temp_input != null){
                this@BigPictureFragment.requireContext().contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,temp_input?.query,null)
            }

        }

    }) {

    }


    override fun onBackKeyPressed() {
        super.onBackKeyPressed()
        navigateUp(binding!!)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun checkIfShowAd(){
        scrollPageCount++
        if(scrollPageCount > 20){
            scrollPageCount = 0
            modle.showInterstitialAd(this.requireActivity())
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentBigPictureBinding.inflate(inflater,container,false).also {
            val pageAdapter = BigPictureAdapter(args.pictureResult,this){ action ->
                when(action){
                    MotionEvent.ACTION_MOVE->{
                        it.viewPage.isUserInputEnabled = false
                    }
                    MotionEvent.ACTION_UP->{
                        it.viewPage.isUserInputEnabled = true
                    }
                }
            }
            it.viewPage.adapter = pageAdapter
            it.viewPage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    it.userProfileUrl = args.pictureResult.profiles?.get(position) ?: ""
                    it.priority = true
                    checkIfShowAd()
                }
            })
            it.userProfile.setOnClickListener { view ->
                val username = args.pictureResult.userNameArray?.get(it.viewPage.currentItem)
                val userid = args.pictureResult.useridArray?.get(it.viewPage.currentItem)
                if(username != null && userid!=null){
                    modle.navigateToUserProfilePage(view.context,username,userid)
                }
            }
            it.pageProfile.setOnClickListener { view ->
                args.pictureResult.pageUrls?.get(it.viewPage.currentItem)?.also { pageUrl ->
                    modle.navigateToWeb(view.context,pageUrl)
                }
            }

            it.viewPage.setCurrentItem(args.pictureResult.currentIndex,false)
            it.viewPage.offscreenPageLimit = 4
            it.toolBar.setNavigationOnClickListener { _ ->
                navigateUp(it)
            }

            it.shareToInstagramFeed.setOnClickListener {_ ->
                val url = args.pictureResult.images?.get(it.viewPage.currentItem)
                if (url != null) {
                    shareImageToInstagram(url)
                }
            }
            val adRequest = AdRequest.Builder().build()
            it.adView.loadAd(adRequest)

            initTransition()
        }
        postponeEnterTransition(resources.getInteger(R.integer.post_pone_time).toLong(), TimeUnit.MILLISECONDS)
        return binding?.root
    }

    private fun shareImageToInstagram(url:String){
        Uri.parse(url).also {
            Glide.with(this.requireContext()).asBitmap().load(it).into(object:CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    shareToInstagram.launch(resource)
                }
                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
        }
    }



    private fun navigateUp(binding:FragmentBigPictureBinding){
        activeModel.pictureItemPosition.postValue(binding.viewPage.currentItem)
        findNavController().navigateUp()
    }

    private fun initTransition(){
        setEnterSharedElementCallback(object:SharedElementCallback(){
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                super.onMapSharedElements(names, sharedElements)
                val view:ImageView? = binding?.viewPage?.findViewWithTag<View>(binding?.viewPage?.currentItem)?.findViewById(R.id.image_view)
                if(names != null && sharedElements!=null && view!=null){
                    sharedElements[names[0]] = view
                }
            }
        })
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.image_shared_element_transition)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.image_shared_element_transition)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.viewPage?.adapter = null
        binding?.adView?.destroy()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()

    }



    companion object {
        const val SHARE_ELEMENT_NAME = "image_view_transition_name"
        fun navigateToBigPicture(view: View,images:List<String>,
                                 profiles:List<String>,
                                 tags:List<String>,
                                 usersID:List<String>,
                                 usersName:List<String>,
                                 pageUrls:List<String>,
                                 position:Int){
            val navController = view.findNavController()
            val argu = BigPictureArgu(images,profiles,tags,usersID,usersName,pageUrls,position)
            if(navController.currentDestination?.id == R.id.photos_fragment){
                navController.navigate(R.id.to_big_picture,
                    BigPictureFragmentArgs(argu).toBundle(),
                    null,
                    FragmentNavigatorExtras(
                        view to SHARE_ELEMENT_NAME+"-${position}"
                    )
                )
            }
        }
    }
}