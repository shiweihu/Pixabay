package com.shiweihu.pixabayapplication.net

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shiweihu.pixabayapplication.MyApplication
import com.shiweihu.pixabayapplication.room.AppDatabase
import com.shiweihu.pixabayapplication.room.SysSettingDao
import com.shiweihu.pixabayapplication.utils.MachineLearningUtils
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
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideSysSettingDao(appDatabase: AppDatabase): SysSettingDao {
        return appDatabase.sysSettingDao()
    }

    @Provides
    fun provideMachineLearningUtils(): MachineLearningUtils {
        return MachineLearningUtils()
    }



    companion object{

    }

}