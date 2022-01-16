package com.shiweihu.pixabayapplication.viewModle

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.cachedIn
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.bigPictureView.BigPictureFragment
import com.shiweihu.pixabayapplication.bigPictureView.BigPictureFragmentArgs
import com.shiweihu.pixabayapplication.photos.PhotosAdapter
import com.shiweihu.pixabayapplication.repository.PhotoRepository
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoFragmentMainViewModel @Inject constructor(
    val photosRepository: PhotoRepository
):ViewModel() {

    var sharedElementIndex = 0

   fun searchPhotos(q:String?,adapter:PhotosAdapter,category:String? = null){
       viewModelScope.launch {
           photosRepository.searchPhotos(q,category).cachedIn(viewModelScope).collectLatest {
               adapter.submitData(it)
           }
       }
   }

    fun navigateToBigPicture(view: View,
                             images:List<String>,
                             profiles:List<String>,
                             tags:List<String>,
                             usersID:List<String>,
                             usersName:List<String>,
                             position:Int,
                             fuc:(position:Int)->Unit
                             )
    {
       BigPictureFragment.navigateToBigPicture(view,images,profiles,tags,usersID,usersName,position,fuc)
    }

}