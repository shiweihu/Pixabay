package com.shiweihu.pixabayapplication.viewModle

import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.bigPictureView.BigPictureFragment
import com.shiweihu.pixabayapplication.data.PixabayData.ImageInfo
import com.shiweihu.pixabayapplication.data.PexelsData.PexelsPhoto
import com.shiweihu.pixabayapplication.data.UnSplashData.ListPhotos
import com.shiweihu.pixabayapplication.data.UnSplashData.UnSplashItem
import com.shiweihu.pixabayapplication.repository.PhotoRepository
import com.shiweihu.pixabayapplication.utils.MachineLearningUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PhotosMainFragmentModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val machineLearningUtils: MachineLearningUtils
): ViewModel() {

    private var currentQueryValue:String? = null
    private var currentQueryFlow: Flow<PagingData<ImageInfo>>? = null
    private var currentQueryValuePexels:String? = null
    private var currentQueryFlowPexels: Flow<PagingData<PexelsPhoto>>? = null
    private var currentQueryValueUnSplash:String? = null
    private var currentQueryFlowUnSplash: Flow<PagingData<UnSplashItem>>? = null



    fun searchPhotosFromPixabay(q:String): Flow<PagingData<ImageInfo>> {
        var query = q.replace(", ","+")
        if(currentQueryValue != query || currentQueryFlow == null){
            currentQueryValue = query
            currentQueryFlow = photoRepository.searchPhotos(query).cachedIn(viewModelScope)
        }
        return currentQueryFlow!!
    }

    fun classifyPicture(bitmap: Bitmap,callBack: (label:String?) -> Unit){
        machineLearningUtils.getOneLabel(bitmap,callBack)
    }

    fun searchPhotosFromPexels(q:String): Flow<PagingData<PexelsPhoto>> {
        val query = q.replace(", "," ")
        if(currentQueryValuePexels != q || currentQueryFlowPexels == null){
            currentQueryValuePexels = q
            currentQueryFlowPexels = photoRepository.searchPhotosFromPexels(q).cachedIn(viewModelScope)
        }
        return currentQueryFlowPexels!!
    }

    fun searchPhotosFromUnsplash(q:String): Flow<PagingData<UnSplashItem>> {
        val query = q.replace(", "," ")
        if(currentQueryValueUnSplash != q || currentQueryFlowUnSplash == null){
            currentQueryValueUnSplash = q
            currentQueryFlowUnSplash = photoRepository.searchPhotosFromUnsplash(q).cachedIn(viewModelScope)
        }
        return currentQueryFlowUnSplash!!
    }


    fun navigateToBigPicture(view: View,
                             position:Int,
    )
    {
        val navController = view.findNavController()
        if(navController.currentDestination?.id == R.id.photos_fragment){
            navController.navigate(
                R.id.to_big_picture,
                null,
                null,
                FragmentNavigatorExtras(
                    view to BigPictureFragment.SHARE_ELEMENT_NAME +"-${position}"
                )
            )
        }
    }
}