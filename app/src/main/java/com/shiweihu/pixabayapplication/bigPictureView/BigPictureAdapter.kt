package com.shiweihu.pixabayapplication.bigPictureView

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.RectF
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.ImageViewBinding
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu
import kotlin.math.round
import kotlin.math.sqrt

class BigPictureAdapter(val argu:BigPictureArgu,val fragment: Fragment): RecyclerView.Adapter<BigPictureAdapter.ImageViewHolder>() {
    class ImageViewHolder(val binding:ImageViewBinding):RecyclerView.ViewHolder(binding.root)

    private var origMatrix:Matrix? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ImageViewBinding.inflate( LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = argu.images?.get(position) ?:""
        holder.binding.imageUrl = imageUrl
        holder.binding.priority = position == argu.currentIndex
        val transitionName = "${BigPictureFragment.SHARE_ELEMENT_NAME}-${position}"
        holder.binding.imageView.transitionName = transitionName
        holder.binding.imageView.tag = transitionName
        holder.binding.doEnd = { state ->
            if(position == argu.currentIndex) {
                fragment.startPostponedEnterTransition()
            }
            if(state){
                holder.binding.root.setOnClickListener {
                    val dialogBuilder = ViewPictureDialog.Companion.Builder()
                    dialogBuilder.show(holder.binding.root.context,imageUrl)
                }
            }
        }


        holder.binding.executePendingBindings()
    }


    override fun getItemCount(): Int {
        return argu.images!!.size
    }
}