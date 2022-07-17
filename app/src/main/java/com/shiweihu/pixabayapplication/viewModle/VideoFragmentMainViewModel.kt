package com.shiweihu.pixabayapplication.viewModle

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.data.PexelsVideos
import com.shiweihu.pixabayapplication.data.Video
import com.shiweihu.pixabayapplication.repository.VideoRepository
import com.shiweihu.pixabayapplication.videoPlayView.VideoPlayFragment
import com.shiweihu.pixabayapplication.videoPlayView.VideoPlayFragmentArgs
import com.shiweihu.pixabayapplication.viewArgu.VideoPlayArgu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class VideoFragmentMainViewModel @Inject constructor (
    private val videoRepository: VideoRepository
): ViewModel()  {


    private var currentQueryValue:String? = null
    private var currentQueryFlow:Flow<PagingData<Video>>? = null

    private var currentQueryValuePexels:String? = null
    private var currentQueryFlowPexels:Flow<PagingData<PexelsVideos>>? = null


    fun searchVideo(q:String):Flow<PagingData<Video>>{
        if(currentQueryValue != q || currentQueryFlow == null){
            currentQueryValue = q
            currentQueryFlow = videoRepository.searchVideo(q).cachedIn(viewModelScope)
        }
        return currentQueryFlow!!
    }
    fun searchVideoFromPexels(q:String):Flow<PagingData<PexelsVideos>>{
        if(currentQueryValuePexels != q || currentQueryFlowPexels == null){
            currentQueryValuePexels = q
            currentQueryFlowPexels = videoRepository.searchVideoFromPexels(q).cachedIn(viewModelScope)
        }
        return currentQueryFlowPexels!!
    }

    fun navigateToVideoPlayback(view: View,position:Int,from:Int,vararg lists:List<String>){
        val argu = VideoPlayArgu(lists[0],lists[1],lists[2],lists[3],lists[4],lists[5],position,from)
        val navController = view.findNavController()
        if(navController.currentDestination?.id == R.id.video_fragment){
            navController.navigate(
                R.id.video_play_fragment, VideoPlayFragmentArgs(argu).toBundle(),null,
                FragmentNavigatorExtras(
                    view to VideoPlayFragment.PLAYER_BACKGROUND
                )
            )
            // navController.navigate(R.id.to_video_play_view,VideoPlayFragmentArgs(argu).toBundle())
        }
    }

}