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
import com.shiweihu.pixabayapplication.utils.DisplayUtils
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

//    private var initItemNum = 0
//    private var isWait = false;

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

//        initItemNum = 0
//        isWait = true
    }

    override fun onViewAttachedToWindow(holder: ImageViewHolder) {
        super.onViewAttachedToWindow(holder)

//        if(isWait){
//            initItemNum++
//        }
    }

    private fun ifStartEnterTransition(){
//        if(isWait){
//            initItemNum --
//            if(initItemNum == 0){
//                fragment.startPostponedEnterTransition()
//                isWait = false;
//            }
//        }
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        getItem(position)?.also {
            holder.binding.authorName = it.user.trim()
            holder.binding.imageUrl = it.webformatURL
            holder.binding.imageView.transitionName = "${BigPictureFragment.SHARE_ELEMENT_NAME}-${position}"
            holder.binding.imageView.setOnClickListener { view ->
                navigateToBigPicture(view,position)
            }
            holder.binding.priority = position == viewModle.sharedElementIndex
            if(position == viewModle.sharedElementIndex){
                holder.binding.doEnd = {
                    fragment.startPostponedEnterTransition()
                }
            }
            holder.binding.executePendingBindings()
            val width = View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
            val height = View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
//调用measure方法之后就可以获取宽高
//调用measure方法之后就可以获取宽高
            holder.binding.root.measure(width, height)
            val widthValue = holder.binding.root.measuredWidth // 获取宽度
            val heightValue = holder.binding.root.measuredHeight // 获取高度

            val widthPx = DisplayUtils.dp2px(holder.binding.root.context,widthValue.toFloat())
            val radio = widthPx.toFloat()/it.webformatWidth.toFloat()
            holder.binding.root.layoutParams.height = (it.webformatHeight*radio).toInt().coerceAtLeast(300)
            //




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
        val pageUrl = ArrayList<String>()
        this.snapshot().forEach { imageInfo ->
            imageInfo?.let { info->
                images.add(info.webformatURL)
                profiles.add(info.userImageURL)
                tags.add(info.tags)
                usersID.add(info.userId.toString())
                usersName.add(info.user)
                pageUrl.add(info.pageURL)
            }
        }
        viewModle.sharedElementIndex = position
        viewModle.navigateToBigPicture(view,images,profiles,tags,usersID,usersName,pageUrl,position,func)
    }

}