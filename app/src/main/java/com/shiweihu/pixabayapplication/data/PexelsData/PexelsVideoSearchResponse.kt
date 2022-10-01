package com.shiweihu.pixabayapplication.data.PexelsData


import com.google.gson.annotations.SerializedName

data class PexelsVideoSearchResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("total_results")
    val totalResults: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("videos")
    val videos: List<PexelsVideos>
)