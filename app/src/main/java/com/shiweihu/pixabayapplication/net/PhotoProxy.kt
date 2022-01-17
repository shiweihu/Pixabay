package com.shiweihu.pixabayapplication.net


import com.shiweihu.pixabayapplication.MyApplication

import com.shiweihu.pixabayapplication.data.Photos
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PhotoProxy {
    @POST("api/")
    suspend fun queryImages(
        @Query("key") key:String = MyApplication.API_KEY,
        @Query("q") q:String = "",
        @Query("id") id:String = "",
        @Query("page") page:Int = 1,
        @Query("per_page") per_page:Int = 20,
        @Query("category") category:String = "",
        @Query("lang") lang:String = MyApplication.lang
    ): Photos
}