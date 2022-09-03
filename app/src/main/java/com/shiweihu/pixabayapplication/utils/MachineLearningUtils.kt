package com.shiweihu.pixabayapplication.utils

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.util.*
import kotlin.collections.HashMap

class MachineLearningUtils {
    fun process(bitmap:Bitmap,callBack:(scoreMap:Map<String,Float>?)->Unit){
        val image = InputImage.fromBitmap(bitmap, 0)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        labeler.process(image)
            .addOnSuccessListener { labels ->
                val map = HashMap<String,Float>()
                val list = mutableListOf<String>()
                for (label in labels) {
                    val text = label.text
                    val confidence = label.confidence
                    map[text] = confidence
                }
                callBack(map)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                callBack(null)
            }
    }
    fun getOneLabel(bitmap:Bitmap,callBack: (label:String?) -> Unit){
        process(bitmap){ map ->
            if(map != null){
                var test = ""
                var score = 0f
                map.forEach{
                    if(it.value > score){
                        test = it.key
                        score = it.value
                    }
                }
                if(test.isNotEmpty()) callBack(test) else callBack(null)
            }else{
                callBack(null)
            }
        }
    }
}