package com.shiweihu.pixabayapplication.viewModle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shiweihu.pixabayapplication.photos.PhotosAdapter
import com.shiweihu.pixabayapplication.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoFragmentMainViewModel @Inject constructor(
    val photosRepository: PhotoRepository
):ViewModel() {
   fun searchPhotos(q:String?,adapter:PhotosAdapter){
       viewModelScope.launch {
           photosRepository.searchPhotos(q).cachedIn(viewModelScope).collectLatest {
               adapter.submitData(it)
           }
       }
   }
}