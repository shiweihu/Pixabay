package com.shiweihu.pixabayapplication.data.UnSplashData


import com.google.gson.annotations.SerializedName

data class LinksXX(
    @SerializedName("download")
    val download: String,
    @SerializedName("html")
    val html: String,
    @SerializedName("self")
    val self: String
)