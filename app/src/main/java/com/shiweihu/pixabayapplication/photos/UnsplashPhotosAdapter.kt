package com.shiweihu.pixabayapplication.photos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.data.PixabayData.ImageInfo
import com.shiweihu.pixabayapplication.data.UnSplashData.ListPhotosItem
import com.shiweihu.pixabayapplication.data.UnSplashData.UnSplashItem
import com.shiweihu.pixabayapplication.databinding.CardImageLayoutBinding
import com.shiweihu.pixabayapplication.utils.DisplayUtils
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu
import kotlin.math.max

class UnsplashPhotosAdapter(val fragment: Fragment, val clickCallBack:(view: View, args: BigPictureArgu)->Unit): PagingDataAdapter<UnSplashItem, UnsplashPhotosAdapter.ImageViewHolder>(
    UnsplashPhotosAdapter.ImageDiff()
) {
    class ImageDiff: DiffUtil.ItemCallback<UnSplashItem>(){
        override fun areItemsTheSame(oldItem: UnSplashItem, newItem: UnSplashItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: UnSplashItem, newItem: UnSplashItem): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class ImageViewHolder(
        val binding: CardImageLayoutBinding
    ): RecyclerView.ViewHolder(binding.root)


    private val recyclerview_span = fragment.context?.resources?.getInteger(R.integer.photo_recyclerview_span) ?: 1
    private val photos_item_margin = fragment.context?.resources?.getDimension(R.dimen.photo_recyclerview_margin) ?: 0F
    var reStoreFirstPosition = 0
    var reStoreLastPostion = 0

    private var recyclerView:RecyclerView? = null


    init {
        this.addLoadStateListener{
            if(it.refresh == LoadState.Loading){
                //init position
                recyclerView?.scrollToPosition(0)
                recyclerView?.visibility = View.INVISIBLE
            }else if(it.refresh == LoadState.NotLoading(false)){
                val delayTime = recyclerView?.context?.resources?.getInteger(R.integer.post_pone_time)?.toLong() ?: 0L
                recyclerView?.handler?.postDelayed({
                    recyclerView?.visibility = View.VISIBLE
                },delayTime)
            }
        }
    }


    var sharedElementIndex:Int = 0
    var pageIdex = -1


    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        getItem(position)?.also {
            holder.binding.authorName = it.userName.trim()
            //holder.binding.imageUrl = it.webformatURL.replace("_640","_340")
            holder.binding.imageUrl = it.urls.regular
            holder.binding.blurHash = it.blurHash
            holder.binding.pxLog.setImageResource(R.drawable.unsplash_icon)
            holder.binding.imageView.tag = "UnsplashPhotos-${position}"
            holder.binding.imageView.setOnClickListener { view ->
                navigateToBigPicture(view,position)
            }
            holder.binding.imageView.isEnabled = false

            holder.binding.imageView.transitionName = "UnsplashPhotos-${holder.layoutPosition}"
            holder.binding.priority = (holder.layoutPosition == sharedElementIndex && pageIdex == 0)
            holder.binding.doEnd = {result,view ->
                if(position == sharedElementIndex && pageIdex == 0) {
                    fragment.startPostponedEnterTransition()
                }
                if(result){
                    view.isEnabled = true
                }
            }

            val item_margin =
                fragment.context?.let { it1 -> DisplayUtils.dp2px(it1,photos_item_margin)*2 } ?:0



            val scaleRadio = (DisplayUtils.ScreenWidth.toFloat()-item_margin) / it.width.toFloat()
            var heightPX = (it.height.toFloat()-item_margin)*(scaleRadio)

            heightPX /= recyclerview_span



            //val highDP = DisplayUtils.px2dp(holder.binding.imageView.context,heightPX)
            val height = max(heightPX.toInt(),200)
            holder.binding.root.layoutParams.height = height
            holder.binding.height = height

            var widthPX = (it.width.toFloat()-item_margin)*(scaleRadio) / recyclerview_span
            holder.binding.width = widthPX.toInt()
        }
        //holder.binding.executePendingBindings()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder( CardImageLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        if(pageIdex != 2){
            recyclerView.scrollToPosition(reStoreFirstPosition)
        }else{
            if(sharedElementIndex < reStoreFirstPosition || sharedElementIndex>reStoreLastPostion){
                recyclerView.scrollToPosition(sharedElementIndex)
            }else{
                recyclerView.scrollToPosition(reStoreFirstPosition)
            }
        }
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun onViewRecycled(holder: ImageViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.imageView.setImageDrawable(null)
    }



    private fun navigateToBigPicture(view: View, position:Int){
        val images = ArrayList<String>()
        val profiles = ArrayList<String>()
        val tags = ArrayList<String>()
        val usersID = ArrayList<String>()
        val usersName = ArrayList<String>()
        val pageUrl = ArrayList<String>()
        this.snapshot().forEach { imageInfo ->
            imageInfo?.let { info->
                images.add(info.urls.regular)
                profiles.add("")
                tags.add(info.download_location)
                usersID.add(info.photographerHtml)
                usersName.add(info.userName)
                pageUrl.add(info.imageHtml)
            }
        }
        sharedElementIndex = position
        val args = BigPictureArgu(images,profiles,tags,usersID,usersName,pageUrl,position,2)
        clickCallBack(view,args)
        //viewModel.navigateToBigPicture(view,images,profiles,tags,usersID,usersName,pageUrl,position)
    }
}