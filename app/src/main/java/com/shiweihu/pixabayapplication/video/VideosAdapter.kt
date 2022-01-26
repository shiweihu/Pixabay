package com.shiweihu.pixabayapplication.video

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shiweihu.pixabayapplication.data.Video
import com.shiweihu.pixabayapplication.databinding.CardImageLayoutBinding
import com.shiweihu.pixabayapplication.viewModle.VideoFragmentMainViewModel

class VideosAdapter(val viewModle: VideoFragmentMainViewModel, val fragment: Fragment, val func:(position:Int)->Unit): PagingDataAdapter<Video, VideosAdapter.CoverViewHolder>(
    VideosAdapter.VideoDiff()
) {

    class CoverViewHolder(val binding: CardImageLayoutBinding): RecyclerView.ViewHolder(binding.root)

    class VideoDiff: DiffUtil.ItemCallback<Video>(){
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onBindViewHolder(holder: CoverViewHolder, position: Int) {
        getItem(position)?.let {
            val url = "https://i.vimeocdn.com/video/${it.pictureId}_960x540.jpg"
            holder.binding.imageUrl = url
            holder.binding.priority = false
            holder.binding.authorName = it.user
            holder.binding.imageView.scaleType = ImageView.ScaleType.CENTER

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoverViewHolder {
        val binding = CardImageLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CoverViewHolder(binding)
    }

}