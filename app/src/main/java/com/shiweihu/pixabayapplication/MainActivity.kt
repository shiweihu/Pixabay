package com.shiweihu.pixabayapplication

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.ads.*
import com.google.android.material.tabs.TabLayoutMediator
import com.shiweihu.pixabayapplication.databinding.ActivityMainBinding
import com.shiweihu.pixabayapplication.room.SysSetting
import com.shiweihu.pixabayapplication.utils.DisplayUtils
import com.shiweihu.pixabayapplication.viewModle.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.RuntimeException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    val model:MainActivityViewModel by viewModels()

    private val icons=  listOf(R.drawable.image_search_selector,R.drawable.vedio_icon_selector)
    private val names by lazy {
        this.resources.getStringArray(R.array.bottom_text)
    }

    private val adView:AdView
        get() {
            return binding.root.findViewWithTag("adView")
        }

    private val adSize: AdSize by lazy {
        val density =  this.resources.displayMetrics.density

        var adWidthPixels = binding.adViewLayout.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = DisplayUtils.ScreenWidth.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewPage.isUserInputEnabled = false
        binding.viewPage.adapter = FragmentsAdapter(this)
        TabLayoutMediator(binding.bottomNavigate,binding.viewPage){tab,position ->
            tab.setIcon(icons[position])
            tab.text = names[position]
        }.attach()

        lifecycleScope.launch {
            val setting = model.getSyssetting()
            if(setting == null){
                model.initSysSetting()
            }else{
                model.setTimes(this@MainActivity,setting)
            }

        }
        initAdMob()
    }

    private fun initAdMob(){
        AdRequest.Builder().build().also { request ->
            val adView = AdView(this)
            adView.setAdSize(adSize)
            adView.adUnitId = this.resources.getString(R.string.photo_main_banner)
            adView.adListener = object : AdListener(){
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                }
            }
            binding.adViewLayout.addView(adView)
            adView.loadAd(request)
            adView.tag = "adView"
        }
    }

    override fun onPause() {
        super.onPause()
        adView.pause()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }



    override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }


}