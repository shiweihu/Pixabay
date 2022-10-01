package com.shiweihu.pixabayapplication.data.UnSplashData


import com.google.gson.annotations.SerializedName

data class SearchImages(
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int
)