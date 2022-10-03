package com.shiweihu.pixabayapplication.bindingFunctions

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.util.BlurHashDecoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@BindingAdapter(value = ["imageUrl","priority","preview","doEnd"],requireAll = false)
fun bindImageFromUrl(view: ImageView, imageUrl: String?,priority:Boolean = false,preview:String? = null,doEnd:((result:Boolean,view: View)->Unit)?) {
    if (imageUrl != null && imageUrl.isNotEmpty()) {
        var request = Glide.with(view.context)
            .load(imageUrl)
            .dontTransform()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        if(priority){
            request = request.priority(Priority.IMMEDIATE)
        }
        request.doOnEnd {result ->
            doEnd?.invoke(result,view)
        }
        if(!preview.isNullOrEmpty()){
            if(preview.length <= 8){
                val color = Color.parseColor(preview)
                val drawable = ColorDrawable(color)
                request = request.placeholder(drawable)
                request.into(view).clearOnDetach()
            }else if(preview.length == 28 && !preview.contains("http")){
                CoroutineScope(Dispatchers.Main).launch {
                    val drawable = withContext(Dispatchers.IO){
                        BitmapDrawable(view.context.resources,BlurHashDecoder.decode(preview,10,10))
                    }
                    request = request.placeholder(drawable)
                    request.into(view).clearOnDetach()
                }
            }else{
                request = request.thumbnail(Glide.with(view).load(preview).priority(Priority.HIGH).override(20))
                request.into(view).clearOnDetach()
            }
        }else{
            request = request.placeholder(R.drawable.placeholder)
            request.into(view).clearOnDetach()
        }



    }
}




fun <T> RequestBuilder<T>.doOnEnd(body: (result:Boolean) -> Unit): RequestBuilder<T> {
    return addListener(object : RequestListener<T> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<T>?,
            isFirstResource: Boolean
        ): Boolean {
            body(false)
            return true
        }

        override fun onResourceReady(
            resource: T,
            model: Any?,
            target: Target<T>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            body(true)
            return false
        }
    })
}

