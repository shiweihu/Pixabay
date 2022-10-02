package com.shiweihu.pixabayapplication.net


import android.util.Log
import com.shiweihu.pixabayapplication.MyApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    private val myHttpProxy: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient.Builder().also {

            it.cache(file_cache?.let { it1 -> Cache(it1, 86400L) })


            it.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            it.addInterceptor { chain ->
                val origin  = chain.request()
                val response = chain.proceed(origin)
                if(MyApplication.APP_DEBUG){
                    for(header in response.headers){
                        MyApplication.mHandler.post {
                            Log.println(Log.DEBUG,"pixabay response header","${header.first}:${header.second}")
                        }
                    }
                }
                return@addInterceptor response
            }
            it.connectTimeout(30L, TimeUnit.SECONDS)
            it.readTimeout(20L,TimeUnit.SECONDS)
        }.build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val myPexelsHttpProxy: Retrofit = Retrofit.Builder()
        .baseUrl(PEXELS_BASE_URL)
        .client(OkHttpClient.Builder().also {

            it.cache(file_cache?.let { it1 -> Cache(it1, 86400L) })


            it.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            it.addInterceptor { chain ->
                val origin  = chain.request()
//                    val newRequest = origin.newBuilder()
//                        .method(origin.method,origin.body)
//                        .build()

                val newRequest = origin.newBuilder()
                    .header("Authorization"," ${MyApplication.PEXELS_API_KEY}")
                    .build()
                val response = chain.proceed(newRequest)
                if(MyApplication.APP_DEBUG){
                    for(header in response.headers){
                        MyApplication.mHandler.post {
                            Log.println(Log.DEBUG,"pexels response header","${header.first}:${header.second}")
                        }
                    }
                }
                return@addInterceptor response
            }
            it.connectTimeout(30L, TimeUnit.SECONDS)
            it.readTimeout(20L,TimeUnit.SECONDS)
        }.build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val myUnsplashHttpProxy: Retrofit = Retrofit.Builder()
        .baseUrl(UNSPLASH_BASE_URL)
        .client(OkHttpClient.Builder().also {

            it.cache(file_cache?.let { it1 -> Cache(it1, 86400L) })


            it.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            it.addInterceptor { chain ->
                val origin  = chain.request()
//                    val newRequest = origin.newBuilder()
//                        .method(origin.method,origin.body)
//                        .build()

                val newRequest = origin.newBuilder()
                    .header("Authorization","Client-ID ${MyApplication.UNSPLASH_API_KEY}")
                    .build()
                val response = chain.proceed(newRequest)
                if(MyApplication.APP_DEBUG){
                    for(header in response.headers){
                        MyApplication.mHandler.post {
                            Log.println(Log.DEBUG,"UnsplashResponseHeader","${header.first}:${header.second}")
                        }
                    }
                }
                return@addInterceptor response
            }
            it.connectTimeout(30L, TimeUnit.SECONDS)
            it.readTimeout(20L,TimeUnit.SECONDS)
        }.build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    @Singleton
    @Provides
    fun providePhotoProxy(): PhotoProxy {
        return myHttpProxy.create(PhotoProxy::class.java)
    }
    @Singleton
    @Provides
    fun provideVideoProxy():VideoProxy{
        return myHttpProxy.create(VideoProxy::class.java)
    }

    @Singleton
    @Provides
    fun providePexelsPhotoProxy():PexelsPhotoProxy{
        return myPexelsHttpProxy.create(PexelsPhotoProxy::class.java)
    }

    @Singleton
    @Provides
    fun providePexelsVideoProxy():PexelsVideoProxy{
        return myPexelsHttpProxy.create(PexelsVideoProxy::class.java)
    }

    @Singleton
    @Provides
    fun provideUnsplashPhotoProxy():UnsplashPhotoProxy{
        return myUnsplashHttpProxy.create(UnsplashPhotoProxy::class.java)
    }



    companion object{
        private const val BASE_URL = "https://pixabay.com/"
        private const val PEXELS_BASE_URL = "https://api.pexels.com/"
        private const val UNSPLASH_BASE_URL = "https://api.unsplash.com/"
        var file_cache: File? = null
    }


}