package com.shiweihu.pixabayapplication.photos

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
import com.google.android.material.imageview.ShapeableImageView
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.data.PexelsPhoto
import com.shiweihu.pixabayapplication.databinding.CardImageLayoutBinding
import com.shiweihu.pixabayapplication.utils.DisplayUtils
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu
import java.lang.RuntimeException
import kotlin.math.max

class PexelsPhotosAdapter(val fragment: Fragment,val clickCallBack:(view:View,args:BigPictureArgu)->Unit):
    PagingDataAdapter<PexelsPhoto, PexelsPhotosAdapter.ImageViewHolder>(ImageDiff())
{



    var sharedElementIndex:Int = 0
    var pageIdex = -1
    private val recyclerview_span = fragment.context?.resources?.getInteger(R.integer.photo_recyclerview_span) ?: 1
    private val photos_item_margin = fragment.context?.resources?.getDimension(R.dimen.photo_recyclerview_margin) ?: 0F
    private var recyclerView:RecyclerView? = null
    init {
        this.addLoadStateListener {
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



    class ImageViewHolder(
        val binding: CardImageLayoutBinding
    ): RecyclerView.ViewHolder(binding.root)

    class ImageDiff: DiffUtil.ItemCallback<PexelsPhoto>(){
        override fun areItemsTheSame(oldItem: PexelsPhoto, newItem: PexelsPhoto): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PexelsPhoto, newItem: PexelsPhoto): Boolean {
            return oldItem.id == newItem.id
        }
    }

    var reStoreFirstPosition = 0
    var reStoreLastPostion = 0



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

    override fun onViewRecycled(holder: ImageViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.imageView.scaleType = ImageView.ScaleType.FIT_CENTER
    }





    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        getItem(position)?.also{ it ->
            Log.println(Log.INFO,"alt",it.alt ?: "")
            holder.binding.imageUrl = it.src?.large
            holder.binding.authorName = it.photographer
            holder.binding.imageView.transitionName = "PexelsPhotos-${position}"
            holder.binding.imageView.tag = "PexelsPhotos-${position}"
            holder.binding.pxLog.setImageResource(R.drawable.ic_pexels)
            holder.binding.priority = (position == sharedElementIndex && pageIdex == 1)
            holder.binding.imageView.setOnClickListener { view ->
                navigateToBigPicture(view,holder.layoutPosition)
            }
            holder.binding.imageView.isEnabled = false
            holder.binding.doEnd = {result,view ->
                if(position == sharedElementIndex && pageIdex == 1){
                    fragment.startPostponedEnterTransition()
                }
                if(result){
                    (view as ImageView).scaleType =  ImageView.ScaleType.FIT_XY
                    view.isEnabled = true
                }
            }
            val item_margin =
                fragment.context?.let { it1 -> DisplayUtils.dp2px(it1,photos_item_margin)*2 } ?:0



            val scaleRadio = (DisplayUtils.ScreenWidth.toFloat()-item_margin) / it.width.toFloat()
            var heightPX = (it.height.toFloat()-item_margin)*(scaleRadio)

            heightPX /= recyclerview_span



            //val highDP = DisplayUtils.px2dp(holder.binding.imageView.context,heightPX)

            holder.binding.root.layoutParams.height = max(heightPX.toInt(),200)


        }
        holder.binding.executePendingBindings()
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageViewHolder {
        val binding =  CardImageLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ImageViewHolder(binding)
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
                images.add(info.src!!.large!!)
                profiles.add("")
                tags.add(info.alt ?: "")
                usersID.add(info.photographer_url!!)
                usersName.add(info.photographer!!)
                pageUrl.add(info.url!!)
            }
        }
        sharedElementIndex = position
        val args = BigPictureArgu(images,profiles,tags,usersID,usersName,pageUrl,position,1)
        clickCallBack(view,args)
    }


}