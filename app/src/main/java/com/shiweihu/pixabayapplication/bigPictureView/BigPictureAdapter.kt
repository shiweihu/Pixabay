package com.shiweihu.pixabayapplication.bigPictureView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.shiweihu.pixabayapplication.databinding.ImageViewBinding
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu

class BigPictureAdapter(val argu: BigPictureArgu,val fragment: Fragment): RecyclerView.Adapter<BigPictureAdapter.ImageViewHolder>() {
    class ImageViewHolder(val binding:ImageViewBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ImageViewBinding.inflate( LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.binding.imageUrl = argu.images?.get(position) ?:""
        holder.binding.priority = position == argu.currentIndex
        holder.binding.root.tag = position
        holder.binding.imageView.transitionName = "${BigPictureFragment.SHARE_ELEMENT_NAME}-${position}"
        if(position == argu.currentIndex){
            holder.binding.doEnd = {
                fragment.startPostponedEnterTransition()
            }
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return argu.images!!.size
    }
}