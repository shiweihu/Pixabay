package com.shiweihu.pixabayapplication.video

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.bigPictureView.BigPictureFragment
import com.shiweihu.pixabayapplication.data.Video
import com.shiweihu.pixabayapplication.databinding.CardImageLayoutBinding
import com.shiweihu.pixabayapplication.photos.PhotosAdapter
import com.shiweihu.pixabayapplication.videoPlayActivity.VideoPlayActivityArgs
import com.shiweihu.pixabayapplication.viewArgu.VideoPlayArgu
import com.shiweihu.pixabayapplication.viewModle.VideoFragmentMainViewModel

class VideosAdapter(val viewModle: VideoFragmentMainViewModel, val fragment: Fragment, val func:((position:Int)->Unit)? = null): PagingDataAdapter<Video, VideosAdapter.CoverViewHolder>(
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

    override fun onViewDetachedFromWindow(holder: VideosAdapter.CoverViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Glide.with(holder.binding.imageView).clear(holder.binding.imageView)
    }

    override fun onViewAttachedToWindow(holder: VideosAdapter.CoverViewHolder) {
        super.onViewAttachedToWindow(holder)
        getItem(holder.layoutPosition)?.also {
            val url = "https://i.vimeocdn.com/video/${it.pictureId}_960x540.jpg"
            holder.binding.imageUrl = url
            holder.binding.priority = false
            holder.binding.imageView.layoutParams.height = 540
        }

    }

    override fun onBindViewHolder(holder: CoverViewHolder, position: Int) {
        getItem(position)?.let { it ->
            holder.binding.authorName = it.user
            holder.binding.root.setOnClickListener {view->
                val videos = ArrayList<String>()
                val profiles = ArrayList<String>()
                val tags = ArrayList<String>()
                val userid = ArrayList<String>()
                val userNames = ArrayList<String>()
                val pageUrls = ArrayList<String>()


                this.snapshot().forEach { items ->
                    items?.let {video ->
                        videos.add(video.videos.small.url)
                        profiles.add(video.userImageURL)
                        tags.add(video.tags)
                        userid.add(video.userId.toString())
                        userNames.add(video.user)
                        pageUrls.add(video.pageURL)

                    }
                }
                val argu = VideoPlayArgu(videos,profiles,tags,userid,userNames,pageUrls,position)
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