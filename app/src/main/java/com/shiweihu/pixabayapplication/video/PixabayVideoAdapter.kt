package com.shiweihu.pixabayapplication.video

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shiweihu.pixabayapplication.data.Video
import com.shiweihu.pixabayapplication.databinding.CardImageLayoutBinding
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu
import com.shiweihu.pixabayapplication.viewArgu.VideoPlayArgu
import com.shiweihu.pixabayapplication.viewModle.VideoFragmentMainViewModel

class PixabayVideoAdapter(val fragment: Fragment,val clickCallBack:(view:View,args: VideoPlayArgu)->Unit): PagingDataAdapter<Video, PixabayVideoAdapter.CoverViewHolder>(
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

        if(pageIdex != 0){
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

    override fun onViewRecycled(holder: CoverViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.imageView.scaleType = ImageView.ScaleType.FIT_CENTER
    }

    override fun onBindViewHolder(holder: CoverViewHolder, position: Int) {
        getItem(position)?.let { it ->
            holder.binding.authorName = it.user
            val url = "https://i.vimeocdn.com/video/${it.pictureId}_640x360.jpg"
            holder.binding.imageUrl = url
            val priority = pageIdex == 0 && sharedElementIndex == position
            holder.binding.priority = priority
            holder.binding.imageView.layoutParams.height = 360
            holder.binding.doEnd = {result,view ->
                if(pageIdex == 0 && sharedElementIndex == position){
                    fragment.startPostponedEnterTransition()
                }
                if(result){
                    (view as ImageView).scaleType =  ImageView.ScaleType.FIT_XY
                    view.isEnabled = true
                }
            }
            val transitionName = "PixabayVideo-${position}"
            holder.binding.imageView.transitionName = transitionName
            holder.binding.imageView.tag = transitionName
            holder.binding.imageView.setOnClickListener {view->
                navigateToPlayBack(view,position)
            }
            holder.binding.imageView.isEnabled = false
        }
        //holder.binding.executePendingBindings()

    }

    private fun navigateToPlayBack(view: View, position:Int){
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
        val args = VideoPlayArgu(videos,profiles,tags,userid,userNames,pageUrls,position,0)
        clickCallBack(view,args)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoverViewHolder {
        val binding = CardImageLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CoverViewHolder(binding)
    }

}