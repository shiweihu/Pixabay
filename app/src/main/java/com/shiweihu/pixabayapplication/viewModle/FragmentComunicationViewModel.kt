package com.shiweihu.pixabayapplication.viewModle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FragmentComunicationViewModel @Inject constructor(

):ViewModel() {
    val pictureItemPosition:MutableLiveData<Int> = MutableLiveData(0)
    val videoItemPosition:MutableLiveData<Int> = MutableLiveData(0)
}