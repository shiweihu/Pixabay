package com.shiweihu.pixabayapplication

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.bumptech.glide.Glide
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

@HiltAndroidApp
class MyApplication: Application() {

    companion object{
        const val API_KEY= "25109780-7bd3253b1b879d034650fb7f1"
        val mHandler = Handler(Looper.getMainLooper())
        var lang = "en"
        var cachePath: File? = null
    }
    override fun onCreate() {
        super.onCreate()
        lang = Locale.getDefault().language;
        cachePath = this.cacheDir
        Glide.get(this@MyApplication).clearMemory()
        CoroutineScope(Dispatchers.IO).launch {
            Glide.get(this@MyApplication).clearDiskCache()
        }
//        val mSystemLanguageList= Locale.getAvailableLocales()
//        for (local in mSystemLanguageList){
//            lang += local.country
//        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }
}