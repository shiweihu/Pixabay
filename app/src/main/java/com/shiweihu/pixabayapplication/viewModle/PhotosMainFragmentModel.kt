package com.shiweihu.pixabayapplication.viewModle

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shiweihu.pixabayapplication.bigPictureView.BigPictureFragment
import com.shiweihu.pixabayapplication.data.ImageInfo
import com.shiweihu.pixabayapplication.data.PexelsPhoto
import com.shiweihu.pixabayapplication.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PhotosMainFragmentModel @Inject constructor(
    private val photoRepository: PhotoRepository
): ViewModel() {

    private var currentQueryValue:String? = null
    private var currentQueryValuePexels:String? = null
    private var currentQueryFlow: Flow<PagingData<ImageInfo>>? = null
    private var currentQueryFlowPexels: Flow<PagingData<PexelsPhoto>>? = null

    fun searchPhotosFromPixabay(q:String): Flow<PagingData<ImageInfo>> {
        if(currentQueryValue != q || currentQueryFlow == null){
            currentQueryValue = q
            currentQueryFlow = photoRepository.searchPhotos(q).cachedIn(viewModelScope)
        }
        return currentQueryFlow!!
    }

    fun searchPhotosFromPexels(q:String): Flow<PagingData<PexelsPhoto>> {
        if(currentQueryValuePexels != q || currentQueryFlowPexels == null){
            currentQueryValuePexels = q
            currentQueryFlowPexels = photoRepository.searchPhotosFromPexels(q).cachedIn(viewModelScope)
        }
        return currentQueryFlowPexels!!
    }

    fun navigateToBigPicture(view: View,
                             args:List<List<String>>,
                             position:Int,
                             from:Int
    )
    {
        BigPictureFragment.navigateToBigPicture(view,args[0],args[1],args[2],args[3],args[4],args[5],position,from)
    }
}