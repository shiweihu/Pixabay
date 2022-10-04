package com.shiweihu.pixabayapplication.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shiweihu.pixabayapplication.data.PixabayData.ImageInfo
import com.shiweihu.pixabayapplication.data.PexelsData.PexelsPhoto
import com.shiweihu.pixabayapplication.net.PexelsPhotoProxy
import com.shiweihu.pixabayapplication.net.PhotoProxy
import com.shiweihu.pixabayapplication.pagingSource.PexelsSearchPhotoSource
import com.shiweihu.pixabayapplication.pagingSource.SearchPhotoSource
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Call
import javax.inject.Inject


class PhotoRepository @Inject constructor(
    private val photoProxy: PhotoProxy,
    private val PexelsPhotoProxy:PexelsPhotoProxy
) {
    fun searchPhotos(q:String): Flow<PagingData<ImageInfo>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 3, initialLoadSize = 20),
            pagingSourceFactory = { SearchPhotoSource(photoProxy,q) }
        ).flow
    }
    fun searchPhotosFromPexels(q:String):Flow<PagingData<PexelsPhoto>>{
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 3, initialLoadSize = 20),
            pagingSourceFactory = { PexelsSearchPhotoSource(PexelsPhotoProxy,q) }
        ).flow
    }
}