package com.shiweihu.pixabayapplication.pagingSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shiweihu.pixabayapplication.data.PixabayData.ImageInfo
import com.shiweihu.pixabayapplication.data.UnSplashData.ListPhotos
import com.shiweihu.pixabayapplication.data.UnSplashData.UnSplashItem
import com.shiweihu.pixabayapplication.net.PhotoProxy
import com.shiweihu.pixabayapplication.net.UnsplashPhotoProxy
import java.lang.Exception

class UnsplashSearchPhotoSource(
    private val unsplashPhotoProxy: UnsplashPhotoProxy,
    private val query:String
): PagingSource<Int, UnSplashItem>() {
    override fun getRefreshKey(state: PagingState<Int, UnSplashItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnSplashItem> {
        val page = params.key ?: 1
        return try {
            val listItems = mutableListOf<UnSplashItem>()
            if(query.isEmpty()){
               unsplashPhotoProxy.listImages(page).forEach {
                   val item = UnSplashItem(it.id,it.blurHash,it.height,it.width,it.urls,it.user.name,it.links.html,it.user.links.html,it.links.downloadLocation)
                   listItems.add(item)
               }
            }else{
                unsplashPhotoProxy.queryImages(query=query,page=page).results.forEach {
                    val item = UnSplashItem(it.id,it.blurHash,it.height,it.width,it.urls,it.user.name,it.links.html,it.user.links.html,"")
                    listItems.add(item)
                }
            }

            return LoadResult.Page(
                data = listItems,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if(listItems.isNotEmpty()) page + 1 else null
            )
        }catch (e: Exception){
            Log.println(Log.DEBUG,"unsplashSearchPhotos",e.toString())
            LoadResult.Error(e)
        }
    }

}