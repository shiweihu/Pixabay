package com.shiweihu.pixabayapplication.repository


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shiweihu.pixabayapplication.data.ImageInfo
import com.shiweihu.pixabayapplication.data.PexelsVideos
import com.shiweihu.pixabayapplication.data.Video
import com.shiweihu.pixabayapplication.net.PexelsVideoProxy
import com.shiweihu.pixabayapplication.net.VideoProxy
import com.shiweihu.pixabayapplication.pagingSource.PexelsSearchVideoSource
import com.shiweihu.pixabayapplication.pagingSource.SearchPhotoSource
import com.shiweihu.pixabayapplication.pagingSource.SearchVideoSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val videoProxy: VideoProxy,
    private val pexelsVideoProxy: PexelsVideoProxy
) {
    fun searchVideo(q:String): Flow<PagingData<Video>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 3, initialLoadSize = 20),
            pagingSourceFactory = { SearchVideoSource(videoProxy,q) }
        ).flow
    }

    fun searchVideoFromPexels(q:String): Flow<PagingData<PexelsVideos>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 3, initialLoadSize = 20),
            pagingSourceFactory = { PexelsSearchVideoSource(pexelsVideoProxy,q) }
        ).flow
    }
}