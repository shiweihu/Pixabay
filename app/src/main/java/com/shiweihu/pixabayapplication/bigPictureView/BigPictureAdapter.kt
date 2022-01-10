package com.shiweihu.pixabayapplication.bigPictureView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shiweihu.pixabayapplication.databinding.ImageViewBinding
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu

class BigPictureAdapter(val argu: BigPictureArgu): RecyclerView.Adapter<BigPictureAdapter.ImageViewHolder>() {
    class ImageViewHolder(val binding:ImageViewBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ImageViewBinding.inflate( LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.binding.imageUrl = argu.images?.get(position) ?:""
        holder.binding.priority = position == argu.currentIndex
        holder.binding.executePendingBindings()
        holder.binding.root.tag = position
        if(position == argu.currentIndex){
            holder.binding.imageView.transitionName = "${BigPictureFragment.SHARE_ELEMENT_NAME}-${position}"
        }
    }

    override fun getItemCount(): Int {
        return argu.images!!.size
    }
}