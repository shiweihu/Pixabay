package com.shiweihu.pixabayapplication.net


import android.util.Log
import com.shiweihu.pixabayapplication.MyApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Dispatcher
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

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



    companion object{
        private const val BASE_URL = "https://pixabay.com/"
        val myHttpProxy: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().also {
                it.addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                })
                it.addInterceptor { chain ->
                    val origin  = chain.request()
//                    val newRequest = origin.newBuilder()
//                        .method(origin.method,origin.body)
//                        .build()
                    val response = chain.proceed(origin)
                    for(header in response.headers){
                        MyApplication.mHandler.post {
                            Log.println(Log.DEBUG,"response header","${header.first}:${header.second}")
                        }
                    }
                    return@addInterceptor response
                }
                it.cache(MyApplication.cachePath?.let { cache -> Cache(cache, 86400L) })
                it.connectTimeout(30L, TimeUnit.SECONDS)
                it.readTimeout(20L,TimeUnit.SECONDS)
            }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}