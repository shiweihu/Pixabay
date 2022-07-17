package com.shiweihu.pixabayapplication.video

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.LoadState
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

class PixabayVideoAdapter(val viewModle: VideoFragmentMainViewModel, val fragment: Fragment): PagingDataAdapter<Video, PixabayVideoAdapter.CoverViewHolder>(
    VideoDiff()
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
    var sharedElementIndex = 0
    var pageIdex:Int = 0
    var reStoreFirstPosition = 0
    var reStoreLastPostion = 0


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        if(pageIdex != 0){
            recyclerView.scrollToPosition(reStoreFirstPosition)
        }else{
            if(sharedElementIndex < reStoreFirstPosition || sharedElementIndex>reStoreLastPostion){
                recyclerView.scrollToPosition(sharedElementIndex)
            }else if( sharedElementIndex >= reStoreFirstPosition && sharedElementIndex<= reStoreLastPostion){
                recyclerView.scrollToPosition(reStoreFirstPosition)
            }
        }

        this.addLoadStateListener {
            if(it.refresh == LoadState.Loading){
                //init position
                recyclerView.scrollToPosition(0)
            }
        }


    }

    override fun onBindViewHolder(holder: CoverViewHolder, position: Int) {
        getItem(position)?.let { it ->
            holder.binding.authorName = it.user
            val url = "https://i.vimeocdn.com/video/${it.pictureId}_640x360.jpg"
            holder.binding.imageUrl = url
            val priority = pageIdex == 0 && sharedElementIndex == position
            holder.binding.priority = priority
            holder.binding.imageView.layoutParams.height = 360
            holder.binding.doEnd = {
                if(pageIdex == 0 && sharedElementIndex == position){
                    fragment.startPostponedEnterTransition()
                }
            }
            val transitionName = "PixabayVideo-${position}"
            holder.binding.imageView.transitionName = transitionName
            holder.binding.imageView.tag = transitionName
            holder.binding.imageView.setOnClickListener {view->
                navigateToPlayBack(view,position)
            }
        }
        holder.binding.executePendingBindings()

    }

    fun navigateToPlayBack(view: View, position:Int){
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
        viewModle.navigateToVideoPlayback(view,position,0,videos,profiles,tags,userid,userNames,pageUrls)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoverViewHolder {
        val binding = CardImageLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CoverViewHolder(binding)
    }

}