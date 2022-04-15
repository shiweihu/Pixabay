package com.shiweihu.pixabayapplication.pagingSource


import Videos
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shiweihu.pixabayapplication.data.ImageInfo
import com.shiweihu.pixabayapplication.data.Video
import com.shiweihu.pixabayapplication.net.PhotoProxy
import com.shiweihu.pixabayapplication.net.VideoProxy
import java.lang.Exception

class SearchVideoSource(
    private val videoProxy: VideoProxy,
    private val query:String?,
    private val category:String?
): PagingSource<Int, Video>() {
    override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
        val page = params.key ?: 1
        return try {
            val response = videoProxy.searchVideos(q = query ?: "", category = category ?: "",page = page,order = if(query == null || query.isEmpty()) "latest" else "popular")
            LoadResult.Page(
                data = response.hits,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if(response.hits.isNotEmpty()) page + 1 else null
            )
        }catch (e: Exception){
            Log.println(Log.DEBUG,"searchVideo",e.toString())
            LoadResult.Error(e)
        }
    }


}