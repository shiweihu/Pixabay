package com.shiweihu.pixabayapplication.pagingSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shiweihu.pixabayapplication.data.ImageInfo
import com.shiweihu.pixabayapplication.data.PexelsPhoto
import com.shiweihu.pixabayapplication.net.PexelsPhotoProxy
import com.shiweihu.pixabayapplication.net.PhotoProxy
import java.lang.Exception

class PexelsSearchPhotoSource(
    private val photoProxy: PexelsPhotoProxy,
    private val query:String
): PagingSource<Int, PexelsPhoto>() {
    override fun getRefreshKey(state: PagingState<Int, PexelsPhoto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PexelsPhoto> {
        val page = params.key ?: 1
        return try {

           val photos =  if(query.isEmpty()){
                photoProxy.curatedImages(page = page).photos
            }else{
                photoProxy.queryImages(query = query,page = page).photos
            }

            LoadResult.Page(
                data = photos,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if(photos.isNotEmpty()) page + 1 else null
            )
        }catch (e:Exception){
            Log.println(Log.DEBUG,"PexelsSearchPhotos",e.toString())
            LoadResult.Error(e)
        }
    }
}