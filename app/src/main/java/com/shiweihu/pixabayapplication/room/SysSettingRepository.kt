package com.shiweihu.pixabayapplication.room

import androidx.lifecycle.asLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SysSettingRepository @Inject constructor(
    val sysSettingDao: SysSettingDao
){
    suspend fun getSysSetting() = sysSettingDao.getSystemSetting()
    suspend fun setSysSetting(times: Int) = sysSettingDao.updateSystemSetting(times)
    suspend fun setIsRating(isRating:Boolean) = sysSettingDao.updateIsRating(isRating)
}