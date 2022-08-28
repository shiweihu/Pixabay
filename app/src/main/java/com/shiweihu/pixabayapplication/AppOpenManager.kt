package com.shiweihu.pixabayapplication

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import kotlin.system.exitProcess


class AppOpenManager(private val myApplication: MyApplication): LifecycleEventObserver,Application.ActivityLifecycleCallbacks {

    init {
        this.myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    private var appOpenAd: AppOpenAd? = null
    private val loadCallback: AppOpenAdLoadCallback? = null

    var currentActivity: Activity? = null

    private val AD_UNIT_ID by lazy {
        myApplication.getString(R.string.app_open_ad)
    }

    private var isShowingAd = false

    private var loadTime: Long = 0
    private val activityList:MutableList<Activity> = mutableListOf()
    private var lastCreateTime:Long = System.currentTimeMillis()


    /** Shows the ad if one isn't already showing.  */
    fun showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable) {
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null
                        isShowingAd = false
                        fetchAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {}
                    override fun onAdShowedFullScreenContent() {
                        isShowingAd = true
                    }
                }
            appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
            currentActivity?.let { appOpenAd?.show(it) }
        } else {
            fetchAd()
        }
    }


    /** Request an ad  */
    fun fetchAd() {
        // Have unused ad, no need to fetch another.
        if (isAdAvailable) {
            return;
        }

        val loadCallback = object: AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(p0: AppOpenAd) {
                    super.onAdLoaded(p0)
                    appOpenAd= p0
                    loadTime = System.currentTimeMillis()

                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                }

        }
        AppOpenAd.load(
            myApplication, AD_UNIT_ID, adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    /** Creates and returns ad request.  */
    private val adRequest: AdRequest
        get() = AdRequest.Builder().build()

    /** Utility method that checks if ad exists and can be shown.  */
    val isAdAvailable: Boolean
        get() = (appOpenAd != null && wasLoadTimeLessThanNHoursAgo(2))


    /** Utility method to check if ad was loaded more than n hours ago.  */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long =  System.currentTimeMillis() - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        activityList.add(p0)
    }

    override fun onActivityStarted(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {
        if(p0 == currentActivity){
            currentActivity = null
        }
        activityList.remove(p0)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if(event.targetState == Lifecycle.State.STARTED){
         //  showAdIfAvailable()
        }
        if(event == Lifecycle.Event.ON_START){
            checkIfdataExpired()
        }

    }

    private fun checkIfdataExpired(){
        val timeDiff = System.currentTimeMillis() - lastCreateTime
        //If the startup time is 20 hours longer than the last startup time，
        //it should restart the MainActivity,because the pixabay reset image url after 24 hours.
        if( timeDiff/1000/60/60 >= 20){
           lastCreateTime = System.currentTimeMillis()
           Intent(myApplication.applicationContext,MainActivity::class.java).also{
               it.flags = FLAG_ACTIVITY_NEW_TASK
               myApplication.applicationContext.startActivity(it)
           }
           for(activity in activityList){
                activity.finish()
           }
           activityList.clear()
        }
    }


}
