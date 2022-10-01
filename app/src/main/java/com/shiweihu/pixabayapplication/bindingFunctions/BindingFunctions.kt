package com.shiweihu.pixabayapplication.bindingFunctions

import android.graphics.drawable.BitmapDrawable
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
import kotlinx.coroutines.*


@BindingAdapter(value = ["imageUrl","priority","BlurHash","width","height","doEnd"],requireAll = false)
fun bindImageFromUrl(view: ImageView, imageUrl: String?,priority:Boolean = false,BlurHash:String?,width:Int?,height:Int?,doEnd:((result:Boolean,view: View)->Unit)?) {
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
        if(BlurHash.isNullOrEmpty()){
            request = request.placeholder(R.drawable.placeholder)
            request.into(view).clearOnDetach()
        }else{
            if(!BlurHash.isEmpty() && width!=null && height != null){
                CoroutineScope(Dispatchers.Main).launch {
                    val placeholder = withContext(Dispatchers.IO){
                        BitmapDrawable(view.context.resources,BlurHashDecoder.decode(BlurHash,
                            (width / 100).coerceAtLeast(20), (height / 100).coerceAtLeast(20)
                        ))
                    }

                    request  = request.placeholder(placeholder)
                    request.into(view).clearOnDetach()
                }
            }
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

