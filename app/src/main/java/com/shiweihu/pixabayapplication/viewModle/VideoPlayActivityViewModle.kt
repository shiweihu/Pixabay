package com.shiweihu.pixabayapplication.viewModle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shiweihu.pixabayapplication.net.ApplicationModule
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoPlayActivityViewModle @Inject constructor(
    val videoPlayerPosition: ApplicationModule.Companion.VideoPlayerPosition
) :ViewModel() {

}