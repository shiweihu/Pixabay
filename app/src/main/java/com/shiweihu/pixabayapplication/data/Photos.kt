package com.shiweihu.pixabayapplication.data
import com.google.gson.annotations.SerializedName



data class Photos(
    @SerializedName("hits")
    val hits: List<ImageInfo>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("totalHits")
    val totalHits: Int
)

