package com.shiweihu.pixabayapplication.viewModle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shiweihu.pixabayapplication.data.ImageInfo
import com.shiweihu.pixabayapplication.data.Video
import com.shiweihu.pixabayapplication.photos.PhotosAdapter
import com.shiweihu.pixabayapplication.repository.VideoRepository
import com.shiweihu.pixabayapplication.video.VideosAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoFragmentMainViewModel @Inject constructor (
    private val videoRepository: VideoRepository
): ViewModel()  {


    private var currentQueryValue:String? = null
    private var currentQueryFlow:Flow<PagingData<Video>>? = null
    var videoPosition = 0



    fun searchVideo(q:String? = null, category:String? = null):Flow<PagingData<Video>>{
        if(currentQueryValue != q+category || currentQueryFlow == null){
            currentQueryValue = q+category
            currentQueryFlow = videoRepository.searchVideo(q,category).cachedIn(viewModelScope)
        }
        return currentQueryFlow!!
    }

}