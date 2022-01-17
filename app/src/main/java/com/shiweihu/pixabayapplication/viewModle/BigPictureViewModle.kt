package com.shiweihu.pixabayapplication.viewModle

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BigPictureViewModle @Inject constructor(

) :ViewModel() {

    fun navigateToUserProfilePage(context: Context, username:String, userid:String){
        navigateToWeb(context,"https://pixabay.com/users/${username}-${userid}/")
    }

    fun navigateToWeb(context:Context,url:String){
        val uri = Uri.parse(url)
        context.startActivity(Intent(Intent.ACTION_VIEW,uri))
    }
}