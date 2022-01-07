package com.shiweihu.pixabayapplication.photos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shiweihu.pixabayapplication.data.ImageInfo
import com.shiweihu.pixabayapplication.databinding.CardImageLayoutBinding

class PhotosAdapter: PagingDataAdapter<ImageInfo, PhotosAdapter.ImageViewHolder>(ImageDiff()) {
    class ImageViewHolder(val binding:CardImageLayoutBinding):RecyclerView.ViewHolder(binding.root)

    class ImageDiff: DiffUtil.ItemCallback<ImageInfo>(){
        override fun areItemsTheSame(oldItem: ImageInfo, newItem: ImageInfo): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ImageInfo, newItem: ImageInfo): Boolean {
            return oldItem.id == newItem.id
        }

    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        getItem(position)?.also {
            holder.binding.authorName = it.user.trim()
            holder.binding.imageUrl = it.previewURL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder( CardImageLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

}