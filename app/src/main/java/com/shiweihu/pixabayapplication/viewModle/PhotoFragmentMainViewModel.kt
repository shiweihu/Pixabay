package com.shiweihu.pixabayapplication.viewModle

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.bigPictureView.BigPictureFragment
import com.shiweihu.pixabayapplication.bigPictureView.BigPictureFragmentArgs
import com.shiweihu.pixabayapplication.data.ImageInfo
import com.shiweihu.pixabayapplication.photos.PhotosAdapter
import com.shiweihu.pixabayapplication.repository.PhotoRepository
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoFragmentMainViewModel @Inject constructor(
    private val photosRepository: PhotoRepository
):ViewModel() {

    var sharedElementIndex = 0

    private  var lastDataFlow:Flow<PagingData<ImageInfo>>? = null
    private  var lastSearchConditions:String? = null


   fun searchPhotos(q:String?,adapter:PhotosAdapter,category:String? = null){
       viewModelScope.launch {
           if(lastDataFlow == null || lastSearchConditions != q+category){
               lastDataFlow = photosRepository.searchPhotos(q,category).cachedIn(viewModelScope)
               lastSearchConditions = q+category
           }
           lastDataFlow?.collectLatest {
               adapter.submitData(it)
           }
       }
   }

    override fun onCleared() {
        super.onCleared()
        lastDataFlow = null
    }

    fun navigateToBigPicture(view: View,
                             images:List<String>,
                             profiles:List<String>,
                             tags:List<String>,
                             usersID:List<String>,
                             usersName:List<String>,
                             pageUrls:List<String>,
                             position:Int,
                             fuc:(position:Int)->Unit
                             )
    {
       BigPictureFragment.navigateToBigPicture(view,images,profiles,tags,usersID,usersName,pageUrls,position,fuc)
    }

}