package com.shiweihu.pixabayapplication

import android.app.Application
import android.os.Handler
import android.os.Looper
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp
class MyApplication: Application() {

    companion object{
        const val API_KEY= "25109780-7bd3253b1b879d034650fb7f1"
        val mHandler = Handler(Looper.getMainLooper())
        var lang = "en"
    }
    override fun onCreate() {
        super.onCreate()
        lang = Locale.getDefault().language;
//        val mSystemLanguageList= Locale.getAvailableLocales()
//        for (local in mSystemLanguageList){
//            lang += local.country
//        }
    }
}