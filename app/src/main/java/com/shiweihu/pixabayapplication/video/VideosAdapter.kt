package com.shiweihu.pixabayapplication.video

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.data.Video
import com.shiweihu.pixabayapplication.databinding.CardImageLayoutBinding
import com.shiweihu.pixabayapplication.videoPlayView.VideoPlayFragment
import com.shiweihu.pixabayapplication.videoPlayView.VideoPlayFragmentArgs
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

    }

    override fun onViewAttachedToWindow(holder: VideosAdapter.CoverViewHolder) {
        super.onViewAttachedToWindow(holder)


    }

    override fun onBindViewHolder(holder: CoverViewHolder, position: Int) {
        getItem(position)?.let { it ->
            holder.binding.authorName = it.user
            val url = "https://i.vimeocdn.com/video/${it.pictureId}_640x360.jpg"
            holder.binding.imageUrl = url
            holder.binding.priority = false
            holder.binding.imageView.layoutParams.height = 360
            holder.binding.doEnd = {
                if(viewModle.videoPosition == position){
                    fragment.startPostponedEnterTransition()
                }
            }
            holder.binding.imageView.transitionName = VideoPlayFragment.PLAYER_BACKGROUND +"-${position}"
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

                viewModle.videoPosition = position
                val argu = VideoPlayArgu(videos,profiles,tags,userid,userNames,pageUrls,position)
                val navController = view.findNavController()
                if(navController.currentDestination?.id == R.id.video_fragment){
                    navController.navigate(R.id.video_play_fragment,VideoPlayFragmentArgs(argu).toBundle(),null,
                        FragmentNavigatorExtras(
                            holder.binding.imageView to VideoPlayFragment.PLAYER_BACKGROUND
                        ))
                   // navController.navigate(R.id.to_video_play_view,VideoPlayFragmentArgs(argu).toBundle())
                }
            }
        }
        holder.binding.executePendingBindings()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoverViewHolder {
        val binding = CardImageLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CoverViewHolder(binding)
    }

}