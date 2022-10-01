package com.shiweihu.pixabayapplication.data.UnSplashData

data class UnSplashItem(
    val id: String,
    val blurHash: String,
    val height: Int,
    val width: Int,
    val urls: Urls,
    val userName:String,
    val imageHtml:String,
    val photographerHtml:String,
    //when user download image,program must call this link
    val download_location:String
)
