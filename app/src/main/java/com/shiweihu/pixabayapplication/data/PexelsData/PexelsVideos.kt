package com.shiweihu.pixabayapplication.data.PexelsData


import com.google.gson.annotations.SerializedName

data class PexelsVideos(
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("height")
    val height: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("user")
    val user: PexelsUser,
    @SerializedName("video_files")
    val videoFiles: List<PexelsVideoFile>,
    @SerializedName("video_pictures")
    val videoPictures: List<PexelsVideoPicture>,
    @SerializedName("width")
    val width: Int
)