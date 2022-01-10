package com.shiweihu.pixabayapplication.viewArgu

import android.os.Parcel
import android.os.Parcelable
import com.shiweihu.pixabayapplication.bigPictureView.BigPictureFragment

class BigPictureArgu(
    val images:List<String>?,
    val profiles:List<String>?,
    val tags:List<String>?,
    val useridArray:List<String>?,
    val userNameArray:List<String>?,
    var currentIndex:Int,
    val callBack: BigPictureFragment.BigPictureCallBack? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.readInt(),
        parcel.readSerializable() as BigPictureFragment.BigPictureCallBack?
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(images)
        parcel.writeStringList(profiles)
        parcel.writeStringList(tags)
        parcel.writeStringList(useridArray)
        parcel.writeStringList(userNameArray)
        parcel.writeInt(currentIndex)
        parcel.writeSerializable(callBack)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BigPictureArgu> {
        override fun createFromParcel(parcel: Parcel): BigPictureArgu {
            return BigPictureArgu(parcel)
        }

        override fun newArray(size: Int): Array<BigPictureArgu?> {
            return arrayOfNulls(size)
        }
    }
}