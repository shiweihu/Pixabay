package com.shiweihu.pixabayapplication.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SysSetting")
data class SysSetting(
    @PrimaryKey val id:Int,
    val startTimes:Int = 0,
    val isRating:Boolean = false
) {
}