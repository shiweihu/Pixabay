package com.shiweihu.pixabayapplication.bindingFunctions

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

@BindingAdapter(value = ["imageUrl","priority","doEnd"],requireAll = false)
fun bindImageFromUrl(view: ImageView, imageUrl: String?,priority:Boolean = false,doEnd:Function0<Unit>?  = null) {
    if (imageUrl != null && imageUrl.isNotEmpty()) {
        var request = Glide.with(view.context)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder)
            .diskCacheStrategy(DiskCacheStrategy.DATA).doOnEnd {
                doEnd?.invoke()
            }
        if(priority){
            request = request.priority(Priority.IMMEDIATE)
        }
        request.into(view)
    }
}



fun <T> RequestBuilder<T>.doOnEnd(body: () -> Unit): RequestBuilder<T> {
    return addListener(object : RequestListener<T> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<T>?,
            isFirstResource: Boolean
        ): Boolean {
            body()
            return false
        }

        override fun onResourceReady(
            resource: T,
            model: Any?,
            target: Target<T>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            body()
            return false
        }
    })
}

