package com.shiweihu.pixabayapplication.bigPictureView

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.shiweihu.pixabayapplication.BaseFragment
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.FragmentBigPictureBinding
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu
import com.shiweihu.pixabayapplication.viewModle.BigPictureViewModle
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BigPictureFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class BigPictureFragment : BaseFragment() {

     open class BigPictureCallBack() :Parcelable{
        constructor(parcel: Parcel) : this() {
        }

         open fun callBack(position:Int){

         }

        override fun writeToParcel(parcel: Parcel, flags: Int) {

        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<BigPictureCallBack> {
            override fun createFromParcel(parcel: Parcel): BigPictureCallBack {
                return BigPictureCallBack(parcel)
            }

            override fun newArray(size: Int): Array<BigPictureCallBack?> {
                return arrayOfNulls(size)
            }
        }


    }

    private val modle:BigPictureViewModle by viewModels()

    private val args:BigPictureFragmentArgs by navArgs()

    private lateinit  var binding:FragmentBigPictureBinding


    override fun onBackKeyPressed() {
        super.onBackKeyPressed()
        navigateUp(binding)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentBigPictureBinding.inflate(inflater,container,false).also {
            it.viewPage.adapter = BigPictureAdapter(args.pictureResult,this)
            it.viewPage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    it.userProfileUrl = args.pictureResult.profiles?.get(position) ?: ""
                    it.priority = true
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
            initTransition(it)
        }
        binding.executePendingBindings()
        postponeEnterTransition(resources.getInteger(R.integer.post_pone_time).toLong(), TimeUnit.MILLISECONDS)
        return binding.root
    }



    private fun navigateUp(binding:FragmentBigPictureBinding){
        args.pictureResult.callBack?.callBack(binding.viewPage.currentItem)
        findNavController().navigateUp()
    }

    private fun initTransition(binding:FragmentBigPictureBinding){
        setEnterSharedElementCallback(object:SharedElementCallback(){
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                super.onMapSharedElements(names, sharedElements)

                val view:ImageView? = binding.viewPage.findViewWithTag<View>(binding.viewPage.currentItem)?.findViewById(R.id.image_view)
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
                                 position:Int,
                                 callBackFuc:(position:Int)->Unit){
            val navController = view.findNavController()
            val argu = BigPictureArgu(images,profiles,tags,usersID,usersName,pageUrls,position,object:BigPictureCallBack() {
                override fun callBack(position: Int) {
                    callBackFuc(position)
                }
            })
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