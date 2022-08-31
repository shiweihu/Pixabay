package com.shiweihu.pixabayapplication.viewModle

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.AndroidViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.shiweihu.pixabayapplication.MyApplication
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.FragmentBigPictureBinding
import com.shiweihu.pixabayapplication.util.CustomTabActivityHelper
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject
import kotlin.Comparator

@HiltViewModel
class BigPictureViewModel @Inject constructor(
  private val myApplication: MyApplication
) :AndroidViewModel(myApplication) {


    var bigPictureArgu: BigPictureArgu? = null

    private val customTabActivityHelper = CustomTabActivityHelper()

    fun navigateToUserProfilePage(context: Context, username:String, userid:String){
        navigateToWeb(context,"https://pixabay.com/users/${username}-${userid}/")
    }

    fun navigateToUserProfilePagePexils(context: Context,userUrl:String){
        navigateToWeb(context,userUrl)
    }


    fun navigateToWeb(context:Context,url:String){
//        val uri = Uri.parse(url)
//        context.startActivity(Intent(Intent.ACTION_VIEW,uri))

        val intentBuilder = CustomTabsIntent.Builder()
        val defaultColors = CustomTabColorSchemeParams.Builder()
            .build()
        intentBuilder.setDefaultColorSchemeParams(defaultColors)
        intentBuilder.setShareState(CustomTabsIntent.SHARE_STATE_ON)
        intentBuilder.setShowTitle(true)
        CustomTabActivityHelper.openCustomTab(
            context, intentBuilder.build(), Uri.parse(url)){context, uri ->
            if(Intent(Intent.ACTION_VIEW,uri).resolveActivity(context.packageManager)!=null){
                context.startActivity(Intent(Intent.ACTION_VIEW,uri))
            }
        }
    }

    fun onBindingCostomTabSever(context: Context){
        customTabActivityHelper.bindCustomTabsService(context)
    }

    fun onUnBindingCostomTabSever(context: Context){
        customTabActivityHelper.unbindCustomTabsService(context)
    }

    fun showInterstitialAd(activity: Activity){
        val adRequest = AdRequest.Builder().build()
        val adUnitid =  activity.getString(R.string.Interstitial_ads_big_picture)
        InterstitialAd.load(activity, adUnitid, adRequest,AdCallBack(activity))
    }

    fun onPageChange(binding:FragmentBigPictureBinding,position:Int,activity: Activity){
        binding.userProfileUrl = bigPictureArgu?.profiles?.get(position) ?: ""
        binding.priority = true
        checkIfShowAd(activity)
    }
    private var scrollPageCount = 0
    private fun checkIfShowAd(activity:Activity){
        scrollPageCount++
        if(scrollPageCount > 20){
            scrollPageCount = 0
            showInterstitialAd(activity)
        }

    }

    fun userProfileOnClick(context: Context,index:Int){
        val username = bigPictureArgu?.userNameArray?.get(index)
        val userid = bigPictureArgu?.useridArray?.get(index)

        when(bigPictureArgu?.from){
            0 ->{
                if(username != null && userid!=null){
                    navigateToUserProfilePage(context,username,userid)
                }
            }
            1 ->{
                navigateToUserProfilePagePexils(context,userid!!)
            }

        }
    }

//    fun relativeBtnClick(context: Context,index:Int):String{
//        var keyTerms = ""
//        val tag =  bigPictureArgu?.tags?.get(index)
//        if(tag != null && tag.isNotEmpty()){
//            when(bigPictureArgu?.from){
//                0 ->{
//                    keyTerms = tag
//                }
//                1 ->{
//                    keyTerms = tag
//                }
//            }
//        }
//        return keyTerms
//    }
    fun relativeBtnClick(context: Context,index:Int,callBack:(keyTerm:List<String>)->Unit){
       val url = bigPictureArgu?.images?.get(index) ?: ""
        Glide.with(context).asBitmap().load(url).addListener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                callBack(listOf())
                return true
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                resource?.let {bitmap ->
                    val image = InputImage.fromBitmap(bitmap, 0)
                    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                    labeler.process(image)
                        .addOnSuccessListener { labels ->
                            val map = TreeMap<Float,String>(){ fl: Float, fl1: Float ->
                                fl1.compareTo(fl)
                            }
                            val list = mutableListOf<String>()
                            for (label in labels) {
                                val text = label.text
                                val confidence = label.confidence
                                Log.println(Log.DEBUG,"ML result","text:${text}-confidence:${confidence}-index:${index}")
                                map[confidence] = text
                            }
                            map.forEach{
                                if(list.size < 1){
                                    list.add(it.value)
                                }
                            }
                            callBack(list)
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                            callBack(listOf())
                        }
                }
                return true
            }

        }).submit()
    }

    fun pageProfileOnClick(context: Context,index:Int){
        bigPictureArgu?.pageUrls?.get(index)?.also { pageUrl ->
            navigateToWeb(context,pageUrl)
        }
    }

    fun getShareUrl(index:Int):String?{
        return bigPictureArgu?.images?.get(index)
    }

    fun getAdRequest(index:Int):AdRequest{
        var builder =  AdRequest.Builder()
        builder = bigPictureArgu?.tags?.get(index)?.let {
            it.split(",").forEach { keyWord ->
                builder = if(keyWord.isEmpty()) builder else builder.addKeyword(keyWord)
            }
            builder
        } ?: builder
        return builder.build()
    }




    companion object{

        private class AdCallBack(activity: Activity):InterstitialAdLoadCallback(){
            private val contextReference:WeakReference<Activity>
            init {
                contextReference = WeakReference(activity)
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                contextReference.get()?.let {
                    interstitialAd.show(it)
                }
                Log.i("Interstitial ads", "onAdLoaded")
            }
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // Handle the error
                Log.i("Interstitial ads", loadAdError.message)
            }
        }

    }


}