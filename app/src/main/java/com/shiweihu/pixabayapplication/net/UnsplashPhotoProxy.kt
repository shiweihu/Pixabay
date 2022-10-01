package com.shiweihu.pixabayapplication.net

import com.shiweihu.pixabayapplication.data.PixabayData.Photos
import com.shiweihu.pixabayapplication.data.UnSplashData.ListPhotos
import com.shiweihu.pixabayapplication.data.UnSplashData.SearchImages
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface UnsplashPhotoProxy {
    @GET("/photos")
    suspend fun listImages(
        @Query("page") page:Int = 1,
        @Query("per_page") per_page:Int = 30,
        @Query("order_by") order_by:String = "popular"
    ): ListPhotos

    @GET("/search/photos")
    suspend fun queryImages(
        @Query("query") query:String = "",
        @Query("page") page:Int = 1,
        @Query("per_page") per_page:Int = 30,
        @Query("order_by") order_by:String = "relevant"
    ): SearchImages

    @GET
    fun downloadEnd(@Url url: String):Call<ResponseBody>

}