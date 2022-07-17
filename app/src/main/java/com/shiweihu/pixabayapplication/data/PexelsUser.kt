package com.shiweihu.pixabayapplication.data


import com.google.gson.annotations.SerializedName

data class PexelsUser(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)