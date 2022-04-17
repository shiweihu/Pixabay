package com.shiweihu.pixabayapplication.net

import Videos
import com.shiweihu.pixabayapplication.MyApplication
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface VideoProxy {
   @Headers("cache-control:max-age=86400")
   @GET("api/videos/")
   suspend fun searchVideos(
       @Query("key") key:String = MyApplication.API_KEY,
       @Query("q") q:String = "",
       @Query("id") id:String = "",
       @Query("page") page:Int = 1,
       @Query("per_page") per_page:Int = 200,
       @Query("category") category:String = "",
       @Query("lang") lang:String = MyApplication.lang,
       @Query("safesearch") safesearch:Boolean = true,
       @Query("editors_choice")editors_choice:Boolean = false,
       @Query("order") order:String = "popular"
   ):Videos
}