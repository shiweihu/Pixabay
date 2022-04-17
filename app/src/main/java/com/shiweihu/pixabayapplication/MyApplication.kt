package com.shiweihu.pixabayapplication

import android.annotation.TargetApi
import android.app.Application
import android.opengl.Matrix
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.shiweihu.pixabayapplication.net.NetworkModule
import com.shiweihu.pixabayapplication.utils.DisplayUtils
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
    }

    override fun onCreate() {

        NetworkModule.file_cache = this.cacheDir

        super.onCreate()
        lang = Locale.getDefault().language
//        Glide.get(this@MyApplication).clearMemory()
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






    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }
}