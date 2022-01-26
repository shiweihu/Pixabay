
import com.google.gson.annotations.SerializedName
import com.shiweihu.pixabayapplication.data.Video

data class Videos(
    @SerializedName("hits")
    val hits: List<Video>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("totalHits")
    val totalHits: Int
)


data class VideosX(
    @SerializedName("large")
    val large: Large,
    @SerializedName("medium")
    val medium: Medium,
    @SerializedName("small")
    val small: Small,
    @SerializedName("tiny")
    val tiny: Tiny
)

data class Large(
    @SerializedName("height")
    val height: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Int
)

data class Medium(
    @SerializedName("height")
    val height: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Int
)

data class Small(
    @SerializedName("height")
    val height: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Int
)

data class Tiny(
    @SerializedName("height")
    val height: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Int
)