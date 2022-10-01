package com.shiweihu.pixabayapplication.pagingSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shiweihu.pixabayapplication.MyApplication
import com.shiweihu.pixabayapplication.data.PexelsData.PexelsPhoto
import com.shiweihu.pixabayapplication.net.PexelsPhotoProxy
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
            val languageArray = arrayListOf("en-US","pt-BR","es-ES","ca-ES", "de-DE", "it-IT", "fr-FR" ,"sv-SE","id-ID", "pl-PL", "ja-JP","zh-TW","zh-CN","ko-KR","th-TH","nl-NL","hu-HU","vi-VN","cs-CZ","da-DK","fi-FI","uk-UA","el-GR","ro-RO","nb-NO","sk-SK","tr-TR","ru-RU")
            val language = languageArray.filter {
                it == "${MyApplication.lang}-${MyApplication.country}"
            }.run {
                if(this.isEmpty()){
                    return@run "en-US"
                }else{
                    return@run this[0]
                }
            }

            val photos =  if(query.isEmpty()){
                photoProxy.curatedImages(page = page).photos
            }else{
                photoProxy.queryImages(query = query,page = page, lang = language).photos
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