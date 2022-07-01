package com.shiweihu.pixabayapplication.photos

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.ViewTransition
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.bigPictureView.BigPictureFragment
import com.shiweihu.pixabayapplication.data.ImageInfo
import com.shiweihu.pixabayapplication.databinding.CardImageLayoutBinding
import com.shiweihu.pixabayapplication.utils.DisplayUtils
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu
import com.shiweihu.pixabayapplication.viewModle.PhotoFragmentMainViewModel
import kotlin.math.max
import kotlin.math.min

class PhotosAdapter(val viewModle: PhotoFragmentMainViewModel, val fragment: Fragment): PagingDataAdapter<ImageInfo, PhotosAdapter.ImageViewHolder>(ImageDiff()) {
    class ImageViewHolder(
        val binding:CardImageLayoutBinding
    ):RecyclerView.ViewHolder(binding.root)

    private val recyclerview_span = fragment.context?.resources?.getInteger(R.integer.photo_recyclerview_span) ?: 1
    private val photos_item_margin = fragment.context?.resources?.getDimension(R.dimen.photo_recyclerview_margin) ?: 0F

    class ImageDiff: DiffUtil.ItemCallback<ImageInfo>(){
        override fun areItemsTheSame(oldItem: ImageInfo, newItem: ImageInfo): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ImageInfo, newItem: ImageInfo): Boolean {
            return oldItem.id == newItem.id
        }
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
//        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                if(newState == RecyclerView.SCROLL_STATE_IDLE){
//                    Glide.with(recyclerView.context).resumeRequests()
//                }else if(!Glide.with(recyclerView.context).isPaused){
//                    Glide.with(recyclerView.context).pauseRequests()
//                }
//            }
//        })
    }



    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
    }



    override fun onViewDetachedFromWindow(holder: ImageViewHolder) {
        super.onViewDetachedFromWindow(holder)
        //Glide.with(holder.binding.imageView).clear(holder.binding.imageView)
        //Glide.with(holder.binding.imageView).pauseRequests();

    }


    override fun onViewAttachedToWindow(holder: ImageViewHolder) {
        super.onViewAttachedToWindow(holder)
        //Glide.with(holder.binding.imageView).resumeRequests()
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        getItem(position)?.also {
            holder.binding.authorName = it.user.trim()
            holder.binding.imageUrl = it.webformatURL.replace("_640","_340")
            holder.binding.imageView.setOnClickListener { view ->
                navigateToBigPicture(view,holder.layoutPosition)
            }
            holder.binding.imageView.transitionName = "${BigPictureFragment.SHARE_ELEMENT_NAME}-${holder.layoutPosition}"
            holder.binding.priority = holder.layoutPosition == viewModle.sharedElementIndex
            holder.binding.doEnd = {loadState ->
                if(holder.layoutPosition == viewModle.sharedElementIndex) {
                    fragment.startPostponedEnterTransition()
                }
            }

            val item_margin =
                fragment.context?.let { it1 -> DisplayUtils.dp2px(it1,photos_item_margin)*2 } ?:0



            val scaleRadio = (DisplayUtils.ScreenWidth.toFloat()-item_margin) / it.imageWidth.toFloat()
            var heightPX = (it.imageHeight.toFloat()-item_margin)*(scaleRadio)

            heightPX /= recyclerview_span



            //val highDP = DisplayUtils.px2dp(holder.binding.imageView.context,heightPX)

            holder.binding.imageView.layoutParams.height = max(heightPX.toInt(),200)
        }
        holder.binding.executePendingBindings()
        holder.binding.root
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
        viewModle.sharedElementIndex = position
        viewModle.navigateToBigPicture(view,images,profiles,tags,usersID,usersName,pageUrl,position)
    }

}