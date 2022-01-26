package com.shiweihu.pixabayapplication.repository


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shiweihu.pixabayapplication.data.ImageInfo
import com.shiweihu.pixabayapplication.data.Video
import com.shiweihu.pixabayapplication.net.VideoProxy
import com.shiweihu.pixabayapplication.pagingSource.SearchPhotoSource
import com.shiweihu.pixabayapplication.pagingSource.SearchVideoSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val videoProxy: VideoProxy
) {
    fun searchVideo(q:String?,category:String?): Flow<PagingData<Video>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 3, initialLoadSize = 20),
            pagingSourceFactory = { SearchVideoSource(videoProxy,q,category) }
        ).flow
    }
}