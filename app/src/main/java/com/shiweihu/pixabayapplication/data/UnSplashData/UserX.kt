package com.shiweihu.pixabayapplication.data.UnSplashData


import com.google.gson.annotations.SerializedName

data class UserX(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("instagram_username")
    val instagramUsername: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("links")
    val links: LinksXXX,
    @SerializedName("name")
    val name: String,
    @SerializedName("portfolio_url")
    val portfolioUrl: String,
    @SerializedName("profile_image")
    val profileImage: ProfileImageX,
    @SerializedName("twitter_username")
    val twitterUsername: String,
    @SerializedName("username")
    val username: String
)