package com.shiweihu.pixabayapplication.pagingSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.shiweihu.pixabayapplication.data.ImageInfo
import com.shiweihu.pixabayapplication.net.PhotoProxy
import java.lang.Exception

class SearchPhotoSource(
    private val photoProxy: PhotoProxy,
    private val query:String
): PagingSource<Int, ImageInfo>() {
    override fun getRefreshKey(state: PagingState<Int, ImageInfo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageInfo> {
        val page = params.key ?: 1
        return try {
            val response = photoProxy.queryImages(q = query,page = page, order = if(query.isEmpty()) "latest" else "popular")
            LoadResult.Page(
                data = response.hits,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if(response.hits.isNotEmpty()) page + 1 else null
            )
        }catch (e:Exception){
            Log.println(Log.DEBUG,"searchPhotos",e.toString())
            LoadResult.Error(e)

        }
    }
}