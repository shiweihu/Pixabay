package com.shiweihu.pixabayapplication.photos

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.imageview.ShapeableImageView
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.data.ImageInfo
import com.shiweihu.pixabayapplication.databinding.CardImageLayoutBinding
import com.shiweihu.pixabayapplication.utils.DisplayUtils
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu
import kotlin.math.max

class PixabayPhotosAdapter(val fragment: Fragment,val clickCallBack:(view:View,args:BigPictureArgu)->Unit): PagingDataAdapter<ImageInfo, PixabayPhotosAdapter.ImageViewHolder>(ImageDiff()) {
    class ImageViewHolder(
        val binding:CardImageLayoutBinding
    ):RecyclerView.ViewHolder(binding.root)

    var pageIdex = -1
    private val recyclerview_span = fragment.context?.resources?.getInteger(R.integer.photo_recyclerview_span) ?: 1
    private val photos_item_margin = fragment.context?.resources?.getDimension(R.dimen.photo_recyclerview_margin) ?: 0F
    var sharedElementIndex:Int = 0

    class ImageDiff: DiffUtil.ItemCallback<ImageInfo>(){
        override fun areItemsTheSame(oldItem: ImageInfo, newItem: ImageInfo): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ImageInfo, newItem: ImageInfo): Boolean {
            return oldItem.id == newItem.id
        }
    }

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

    override fun onViewRecycled(holder: ImageViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.imageView.scaleType = ImageView.ScaleType.FIT_CENTER
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

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        getItem(position)?.also {
            Log.println(Log.INFO,"tag",it.tags)
            holder.binding.authorName = it.user.trim()
            //holder.binding.imageUrl = it.webformatURL.replace("_640","_340")
            holder.binding.imageUrl = it.largeImageURL
            holder.binding.imageView.tag = "PixabayPhotos-${position}"
            holder.binding.imageView.setOnClickListener { view ->
                navigateToBigPicture(view,holder.layoutPosition)
            }
            holder.binding.imageView.isEnabled = false

            holder.binding.imageView.transitionName = "PixabayPhotos-${holder.layoutPosition}"
            holder.binding.priority = (holder.layoutPosition == sharedElementIndex && pageIdex == 0)
            holder.binding.doEnd = {result,view ->
                if(position == sharedElementIndex && pageIdex == 0) {
                    fragment.startPostponedEnterTransition()
                }
                if(result){
                    (view as ImageView).scaleType =  ImageView.ScaleType.FIT_XY
                    view.isEnabled = true
                }
            }

            val item_margin =
                fragment.context?.let { it1 -> DisplayUtils.dp2px(it1,photos_item_margin)*2 } ?:0



            val scaleRadio = (DisplayUtils.ScreenWidth.toFloat()-item_margin) / it.imageWidth.toFloat()
            var heightPX = (it.imageHeight.toFloat()-item_margin)*(scaleRadio)

            heightPX /= recyclerview_span



            //val highDP = DisplayUtils.px2dp(holder.binding.imageView.context,heightPX)

            holder.binding.root.layoutParams.height = max(heightPX.toInt(),200)
        }
        holder.binding.executePendingBindings()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder( CardImageLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
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
                images.add(info.largeImageURL)
                profiles.add(info.userImageURL)
                tags.add(info.tags)
                usersID.add(info.userId.toString())
                usersName.add(info.user)
                pageUrl.add(info.pageURL)
            }
        }
        sharedElementIndex = position
        val args = BigPictureArgu(images,profiles,tags,usersID,usersName,pageUrl,position,0)
        clickCallBack(view,args)
        //viewModel.navigateToBigPicture(view,images,profiles,tags,usersID,usersName,pageUrl,position)
    }

}