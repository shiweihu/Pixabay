package com.shiweihu.pixabayapplication.net

import com.shiweihu.pixabayapplication.data.PexelsData.PexelsVideoSearchResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface PexelsVideoProxy {

    @Headers("cache-control:max-age=86400")
    @GET("videos/search")
    suspend fun queryVideos(
        @Query("query") query:String,
        @Query("page") page:Int = 1,
        @Query("per_page") per_page:Int = 80,
        @Query("locale") lang:String = "en-US"
    ): PexelsVideoSearchResponse

    @Headers("cache-control:max-age=86400")
    @GET("videos/popular")
    suspend fun popularVideos(
        @Query("page") page:Int = 1,
        @Query("per_page") per_page:Int = 80,
    ): PexelsVideoSearchResponse

}