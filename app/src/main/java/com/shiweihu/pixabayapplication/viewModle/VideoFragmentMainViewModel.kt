package com.shiweihu.pixabayapplication.viewModle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shiweihu.pixabayapplication.photos.PhotosAdapter
import com.shiweihu.pixabayapplication.repository.VideoRepository
import com.shiweihu.pixabayapplication.video.VideosAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoFragmentMainViewModel @Inject constructor (
    private val videoRepository: VideoRepository
): ViewModel()  {

    fun feedData(q:String? = null, adapter: VideosAdapter, category:String? = null){
        viewModelScope.launch {
            videoRepository.searchVideo(q,category).cachedIn(viewModelScope).collectLatest {
                adapter.submitData(it)
            }
        }

    }

}