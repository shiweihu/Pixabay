package com.shiweihu.pixabayapplication.video

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.data.PexelsVideos
import com.shiweihu.pixabayapplication.data.Video
import com.shiweihu.pixabayapplication.databinding.CardImageLayoutBinding
import com.shiweihu.pixabayapplication.viewArgu.VideoPlayArgu
import com.shiweihu.pixabayapplication.viewModle.VideoFragmentMainViewModel

class PexelsVideoAdapter(val fragment: Fragment,val clickCallBack:(view:View,args: VideoPlayArgu)->Unit): PagingDataAdapter<PexelsVideos, PexelsVideoAdapter.CoverViewHolder>(
PexelsVideoAdapter.VideoDiff()
)  {
    class CoverViewHolder(val binding: CardImageLayoutBinding): RecyclerView.ViewHolder(binding.root)

    class VideoDiff: DiffUtil.ItemCallback<PexelsVideos>(){
        override fun areItemsTheSame(oldItem: PexelsVideos, newItem: PexelsVideos): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PexelsVideos, newItem: PexelsVideos): Boolean {
            return oldItem.id == newItem.id
        }
    }

    var sharedElementIndex = 0
    var pageIdex:Int = 0
    var reStoreFirstPosition = 0
    var reStoreLastPostion = 0

    private var recyclerView:RecyclerView? = null
    init {
        this.addLoadStateListener {
            if(it.refresh == LoadState.Loading){
                //init position
                recyclerView?.scrollToPosition(0)
            }
        }
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        if(pageIdex != 1){
            recyclerView.scrollToPosition(reStoreFirstPosition)
        }else{
            if(sharedElementIndex < reStoreFirstPosition || sharedElementIndex>reStoreLastPostion){
                recyclerView.scrollToPosition(sharedElementIndex)
            }else if( sharedElementIndex >= reStoreFirstPosition && sharedElementIndex<= reStoreLastPostion){
                recyclerView.scrollToPosition(reStoreFirstPosition)
            }
        }
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun onBindViewHolder(holder:CoverViewHolder, position: Int) {
        getItem(position)?.also{
            holder.binding.imageUrl = it.image
            holder.binding.pxLog.setImageResource(R.drawable.ic_pexels)
            holder.binding.authorName = it.user.name
            holder.binding.imageView.layoutParams.height = 360
            val priority = pageIdex == 0 && sharedElementIndex == position
            holder.binding.priority = priority
            holder.binding.doEnd = {_,_ ->
                if(pageIdex == 1 && sharedElementIndex == position){
                    fragment.startPostponedEnterTransition()
                }
            }
            val transitionName = "PexelsVideo-${position}"
            holder.binding.imageView.tag = transitionName
            holder.binding.imageView.transitionName = transitionName
            holder.binding.imageView.setOnClickListener {view ->
                navigateToPlayBack(view,position)
            }
        }

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
                videos.add(video.videoFiles[0].link)
                profiles.add("")
                tags.add("")
                userid.add(video.user.url)
                userNames.add(video.user.name)
                pageUrls.add(video.url)
            }
        }
        val args = VideoPlayArgu(videos,profiles,tags,userid,userNames,pageUrls,position,1)
        clickCallBack(view,args)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CoverViewHolder {
        return CoverViewHolder( CardImageLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false) )
    }
}