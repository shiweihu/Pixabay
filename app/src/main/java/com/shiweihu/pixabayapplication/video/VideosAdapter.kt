package com.shiweihu.pixabayapplication.video

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.data.Video
import com.shiweihu.pixabayapplication.databinding.CardImageLayoutBinding
import com.shiweihu.pixabayapplication.videoPlayActivity.VideoPlayActivityArgs
import com.shiweihu.pixabayapplication.viewArgu.VideoPlayArgu
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
        getItem(position)?.let { it ->
            val url = "https://i.vimeocdn.com/video/${it.pictureId}_960x540.jpg"
            holder.binding.imageUrl = url
            holder.binding.priority = false
            holder.binding.authorName = it.user
            holder.binding.imageView.scaleType = ImageView.ScaleType.CENTER
            holder.binding.root.setOnClickListener {view->
                val videos = ArrayList<String>();

                this.snapshot().forEach { items ->
                    items?.let {video ->
                        videos.add(video.videos.medium.url)
                    }
                }
                val argu = VideoPlayArgu(videos,null,null,null,null,null,0)

                val navController = view.findNavController()
                if(navController.currentDestination?.id == R.id.video_fragment){
                    navController.navigate(R.id.to_video_play_view,VideoPlayActivityArgs(argu).toBundle())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoverViewHolder {
        val binding = CardImageLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CoverViewHolder(binding)
    }

}