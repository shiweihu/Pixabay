package com.shiweihu.pixabayapplication.net

import android.content.Context
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
}