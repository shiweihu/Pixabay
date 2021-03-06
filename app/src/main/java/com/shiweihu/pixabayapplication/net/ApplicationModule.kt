package com.shiweihu.pixabayapplication.net

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shiweihu.pixabayapplication.MyApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApplicationModule {

    @Singleton
    @Provides
    fun provideMyApplication(@ApplicationContext context: Context): MyApplication {
        return context as MyApplication
    }

    @Singleton
    @Provides
    fun provideVideoPlayerProsition(): VideoPlayerPosition{
        return VideoPlayerPosition(MutableLiveData(0))
    }

    companion object{

         class VideoPlayerPosition(
            val position:MutableLiveData<Long>
        )
    }

}