package com.shiweihu.pixabayapplication.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SysSettingDao {
    @Query("select * from SysSetting limit 1")
    suspend fun getSystemSetting():SysSetting?

    @Insert
    suspend fun initSystemSetting(setting: SysSetting):Long

    @Query("update SysSetting set startTimes = :time")
    suspend fun updateSystemSetting(time: Int):Int

    @Query("update SysSetting set isRating = :isRating")
    suspend fun updateIsRating(isRating: Boolean):Int
}