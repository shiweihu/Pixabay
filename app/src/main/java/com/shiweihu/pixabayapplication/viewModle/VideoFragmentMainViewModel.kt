package com.shiweihu.pixabayapplication.viewModle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
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


    private var lastFlow: Flow<PagingData<Video>>? = null
    private var lastSearchConditions:String? = null

    fun searchVideo(q:String? = null, adapter: VideosAdapter, category:String? = null){
        viewModelScope.launch {
            if(lastFlow == null || lastSearchConditions != q+category ){
                lastFlow = videoRepository.searchVideo(q,category).cachedIn(viewModelScope)
                lastSearchConditions = q+category
            }
            lastFlow?.collectLatest {
                adapter.submitData(it)
            }
        }
    }

}