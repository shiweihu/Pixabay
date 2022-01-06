package com.shiweihu.pixabayapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {

    companion object{
       const val API_KEY= "25109780-7bd3253b1b879d034650fb7f1"
    }
}