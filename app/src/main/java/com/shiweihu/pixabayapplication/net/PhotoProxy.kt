package com.shiweihu.pixabayapplication.net


import com.shiweihu.pixabayapplication.MyApplication

import com.shiweihu.pixabayapplication.data.PixabayData.Photos
import retrofit2.http.*

interface PhotoProxy {
    @Headers("cache-control:max-age=86400")
    @GET("api/")
    suspend fun queryImages(
        @Query("key") key:String = MyApplication.API_KEY,
        @Query("q") q:String = "",
        @Query("id") id:String = "",
        @Query("page") page:Int = 1,
        @Query("per_page") per_page:Int = 200,
        @Query("category") category:String = "",
        @Query("lang") lang:String = MyApplication.lang,
        @Query("safesearch") safesearch:Boolean = true,
        @Query("editors_choice")editors_choice:Boolean = false,
        @Query("order") order:String = "latest"
    ): Photos
}