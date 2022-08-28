package com.shiweihu.pixabayapplication.net

import com.shiweihu.pixabayapplication.MyApplication
import com.shiweihu.pixabayapplication.data.PexelsPhotosSearchResponse
import com.shiweihu.pixabayapplication.data.Photos
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface PexelsPhotoProxy {
    @Headers("cache-control:max-age=86400")
    @GET("v1/search")
    suspend fun queryImages(
        @Query("query") query:String = "cat",
        @Query("page") page:Int = 1,
        @Query("per_page") per_page:Int = 80,
        @Query("locale") lang:String = "en-US"
    ): PexelsPhotosSearchResponse

    @Headers("cache-control:max-age=86400")
    @GET("v1/curated")
    suspend fun curatedImages(
        @Query("page") page:Int = 1,
        @Query("per_page") per_page:Int = 80
    ): PexelsPhotosSearchResponse

//    @Query("orientation") orientation:String = "",
//    @Query("size") size:String = "",
//    @Query("color") color:String = "",
//    @Query("locale") locale:String = "",
}