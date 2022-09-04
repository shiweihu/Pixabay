package com.shiweihu.pixabayapplication

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.android.gms.ads.MobileAds
import com.shiweihu.pixabayapplication.net.NetworkModule
import com.shiweihu.pixabayapplication.utils.DisplayUtils
import dagger.hilt.android.HiltAndroidApp
import java.util.*
import kotlin.collections.HashSet


@HiltAndroidApp
class MyApplication: Application() {

    companion object{
        //pixabay request key
        const val API_KEY = "25109780-7bd3253b1b879d034650fb7f1"
        const val PEXELS_API_KEY = "563492ad6f917000010000014f8e0e71323c406392738971f27849a7"
        val mHandler = Handler(Looper.getMainLooper())
        val lang by lazy {
            Locale.getDefault().language
        }
        val country by lazy {
            Locale.getDefault().getCountry()
        }
        var APP_DEBUG = false

    }

    private fun isApkDebugable(): Boolean {
        return this.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }


    private val appOpenManager = AppOpenManager(this)
    fun getCurrentActivity() = appOpenManager.currentActivity


    override fun onCreate() {


        NetworkModule.file_cache = this.cacheDir

        super.onCreate()

        APP_DEBUG = isApkDebugable()




        //Glide.get(this@MyApplication).clearMemory()
//        CoroutineScope(Dispatchers.IO).launch {
//            Glide.get(this@MyApplication).clearDiskCache()
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            val displayMatrics = getScreenMatricsNew()
            DisplayUtils.ScreenWidth  = displayMatrics.bounds.width()
            DisplayUtils.ScreenHeight = displayMatrics.bounds.height()
        }else{
            val displayMatrics = getScreenMatricsOld()
            DisplayUtils.ScreenWidth  = displayMatrics.widthPixels
            DisplayUtils.ScreenHeight = displayMatrics.heightPixels
        }

        MobileAds.initialize(
            this
        ) { }

        if(APP_DEBUG){
//            StrictMode.setThreadPolicy(
//                ThreadPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()//.penaltyDeath()
//                    .build()
//            )
//            StrictMode.setVmPolicy(
//                VmPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()//.penaltyDeath()
//                    .build()
//            )
        }

//        val mSystemLanguageList= Locale.getAvailableLocales()
//        for (local in mSystemLanguageList){
//            lang += local.country
//        }
        

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getScreenMatricsNew(): WindowMetrics {
        return (getSystemService(WINDOW_SERVICE) as WindowManager).maximumWindowMetrics
    }

    private fun getScreenMatricsOld():DisplayMetrics{
        val matrix = DisplayMetrics()
        val displayMatrics = (getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(matrix)
        return matrix;
    }

    override fun onTerminate() {
        super.onTerminate()
    }



    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when(level){
            TRIM_MEMORY_UI_HIDDEN->{
//                CoroutineScope(Dispatchers.IO).launch {
//                    Glide.get(this@MyApplication).clearDiskCache()
//                }
                Glide.get(this).clearMemory()
            }
        }

    }



    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }



}