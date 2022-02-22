package com.shiweihu.pixabayapplication.repository

import android.graphics.ImageDecoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shiweihu.pixabayapplication.data.ImageInfo
import com.shiweihu.pixabayapplication.net.PhotoProxy
import com.shiweihu.pixabayapplication.pagingSource.SearchPhotoSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class PhotoRepository @Inject constructor(
    private val photoProxy: PhotoProxy
) {
    fun searchPhotos(q:String?,category:String?): Flow<PagingData<ImageInfo>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 3, initialLoadSize = 20),
            pagingSourceFactory = { SearchPhotoSource(photoProxy,q,category) }
        ).flow
    }
}