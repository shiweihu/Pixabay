package com.shiweihu.pixabayapplication.viewModle

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.shiweihu.pixabayapplication.MyApplication
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.FragmentBigPictureBinding
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class BigPictureViewModel @Inject constructor(
  private val myApplication: MyApplication
) :AndroidViewModel(myApplication) {


    var bigPictureArgu: BigPictureArgu? = null

    fun navigateToUserProfilePage(context: Context, username:String, userid:String){
        navigateToWeb(context,"https://pixabay.com/users/${username}-${userid}/")
    }

    fun navigateToUserProfilePagePexils(context: Context,userUrl:String){
        navigateToWeb(context,userUrl)
    }

    fun navigateToWeb(context:Context,url:String){
        val uri = Uri.parse(url)
        context.startActivity(Intent(Intent.ACTION_VIEW,uri))
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