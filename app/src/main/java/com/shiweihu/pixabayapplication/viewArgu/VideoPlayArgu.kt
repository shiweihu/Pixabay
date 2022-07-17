package com.shiweihu.pixabayapplication.viewArgu

import android.os.Parcel
import android.os.Parcelable
import com.shiweihu.pixabayapplication.bigPictureView.BigPictureFragment

class VideoPlayArgu(
    val videos:List<String>?,
    val profiles:List<String>?,
    val tags:List<String>?,
    val useridArray:List<String>?,
    val userNameArray:List<String>?,
    val pageUrls:List<String>?,
    var currentIndex:Int,
    val from:Int
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(videos)
        parcel.writeStringList(profiles)
        parcel.writeStringList(tags)
        parcel.writeStringList(useridArray)
        parcel.writeStringList(userNameArray)
        parcel.writeStringList(pageUrls)
        parcel.writeInt(currentIndex)
        parcel.writeInt(from)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoPlayArgu> {
        override fun createFromParcel(parcel: Parcel): VideoPlayArgu {
            return VideoPlayArgu(parcel)
        }

        override fun newArray(size: Int): Array<VideoPlayArgu?> {
            return arrayOfNulls(size)
        }
    }
}