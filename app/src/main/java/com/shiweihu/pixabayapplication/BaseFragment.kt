package com.shiweihu.pixabayapplication

import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint


abstract class BaseFragment:Fragment() {
    open lateinit var backKeyCallBack:OnBackPressedCallback
    override fun onStart() {
        super.onStart()
        backKeyCallBack = requireActivity().onBackPressedDispatcher.addCallback(this,false){
            onBackKeyPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        backKeyCallBack.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        backKeyCallBack.isEnabled = false;
    }


    override fun onStop() {
        super.onStop()
        backKeyCallBack.remove()
    }

    open fun onBackKeyPressed(){

    }
}