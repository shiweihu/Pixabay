package com.shiweihu.pixabayapplication.photos

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.ViewTransition
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shiweihu.pixabayapplication.bigPictureView.BigPictureFragment
import com.shiweihu.pixabayapplication.data.ImageInfo
import com.shiweihu.pixabayapplication.databinding.CardImageLayoutBinding
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu
import com.shiweihu.pixabayapplication.viewModle.PhotoFragmentMainViewModel

class PhotosAdapter(val viewModle: PhotoFragmentMainViewModel, val fragment: Fragment, val func:(position:Int)->Unit): PagingDataAdapter<ImageInfo, PhotosAdapter.ImageViewHolder>(ImageDiff()) {
    class ImageViewHolder(val binding:CardImageLayoutBinding):RecyclerView.ViewHolder(binding.root)

    class ImageDiff: DiffUtil.ItemCallback<ImageInfo>(){
        override fun areItemsTheSame(oldItem: ImageInfo, newItem: ImageInfo): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ImageInfo, newItem: ImageInfo): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private var initItemNum = 0
    private var isWait = false;

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        initItemNum = 0
        isWait = true
    }

    override fun onViewAttachedToWindow(holder: ImageViewHolder) {
        super.onViewAttachedToWindow(holder)
        if(isWait){
            initItemNum++
        }
    }

    private fun ifStartEnterTransition(){
        if(isWait){
            initItemNum --
            if(initItemNum == 0){
                fragment.startPostponedEnterTransition()
                isWait = false;
            }
        }
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        getItem(position)?.also {
            holder.binding.authorName = it.user.trim()
            holder.binding.imageUrl = it.previewURL
            holder.binding.imageView.transitionName = "${BigPictureFragment.SHARE_ELEMENT_NAME}-${position}"
            holder.binding.imageView.setOnClickListener { view ->
                navigateToBigPicture(view,position)
            }
            holder.binding.priority = true
            holder.binding.doEnd = {
                ifStartEnterTransition()
            }
            holder.binding.executePendingBindings()
        }
        holder.binding.root.tag = position
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
        this.snapshot().forEach { imageInfo ->
            imageInfo?.let { info->
                images.add(info.largeImageURL)
                profiles.add(info.userImageURL)
                tags.add(info.tags)
                usersID.add(info.userId.toString())
                usersName.add(info.user)
            }
        }
        viewModle.sharedElementIndex = position
        viewModle.navigateToBigPicture(view,images,profiles,tags,usersID,usersName,position,func)
    }

}