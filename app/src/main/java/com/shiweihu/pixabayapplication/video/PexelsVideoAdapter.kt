package com.shiweihu.pixabayapplication.video

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.data.PexelsVideos
import com.shiweihu.pixabayapplication.data.Video
import com.shiweihu.pixabayapplication.databinding.CardImageLayoutBinding
import com.shiweihu.pixabayapplication.utils.DisplayUtils
import com.shiweihu.pixabayapplication.viewArgu.VideoPlayArgu
import com.shiweihu.pixabayapplication.viewModle.VideoFragmentMainViewModel
import kotlin.math.max

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

    private val recyclerview_span = fragment.context?.resources?.getInteger(R.integer.photo_recyclerview_span) ?: 1
    private val photos_item_margin = fragment.context?.resources?.getDimension(R.dimen.photo_recyclerview_margin) ?: 0F

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
            //holder.binding.imageView.layoutParams.height = 360
            val priority = pageIdex == 0 && sharedElementIndex == position
            holder.binding.priority = priority
            holder.binding.doEnd = {result,view ->
                if(pageIdex == 1 && sharedElementIndex == position){
                    fragment.startPostponedEnterTransition()
                }
                if(result){
                    (view as ImageView).scaleType =  ImageView.ScaleType.FIT_XY
                    view.isEnabled = true
                }
            }
            val transitionName = "PexelsVideo-${position}"
            holder.binding.imageView.tag = transitionName
            holder.binding.imageView.transitionName = transitionName
            holder.binding.imageView.setOnClickListener {view ->
                navigateToPlayBack(view,position)
            }
            holder.binding.imageView.isEnabled = false

            val item_margin =
                fragment.context?.let { it1 -> DisplayUtils.dp2px(it1,photos_item_margin)*2 } ?:0



            val scaleRadio = (DisplayUtils.ScreenWidth.toFloat()-item_margin) / it.width.toFloat()
            var heightPX = (it.height.toFloat()-item_margin)*(scaleRadio)

            heightPX /= recyclerview_span



            //val highDP = DisplayUtils.px2dp(holder.binding.imageView.context,heightPX)

            holder.binding.imageView.layoutParams.height = max(heightPX.toInt(),200)


        }

    }

    override fun onViewRecycled(holder: CoverViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.imageView.scaleType = ImageView.ScaleType.FIT_CENTER
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