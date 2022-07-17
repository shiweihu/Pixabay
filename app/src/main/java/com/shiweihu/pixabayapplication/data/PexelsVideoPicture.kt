package com.shiweihu.pixabayapplication.data


import com.google.gson.annotations.SerializedName

data class PexelsVideoPicture(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nr")
    val nr: Int,
    @SerializedName("picture")
    val picture: String
)