package com.shiweihu.pixabayapplication.video

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shiweihu.pixabayapplication.databinding.RecyclerViewLayoutBinding
import com.shiweihu.pixabayapplication.photos.SouceAdapter
import com.shiweihu.pixabayapplication.viewModle.FragmentComunicationViewModel
import com.shiweihu.pixabayapplication.viewModle.PhotosMainFragmentModel
import com.shiweihu.pixabayapplication.viewModle.VideoFragmentMainViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VideoSourceAdapter(val fragment: Fragment,
                         val model: VideoFragmentMainViewModel,
                         val activityModel: FragmentComunicationViewModel
) : RecyclerView.Adapter<VideoSourceAdapter.RecyclerViewHolder>()  {

    class RecyclerViewHolder(
        val binding: RecyclerViewLayoutBinding
    ): RecyclerView.ViewHolder(binding.root)

    private val jobs:MutableList<Job> = mutableListOf()

    var shareElementIndex = 0
    var query:String = ""


    private var recyclerview:RecyclerView? = null

    private val pixabayVideoAdapyer by lazy {
        PixabayVideoAdapter(fragment){view , args ->
            activityModel.videoPlayArguLiveData.value = args
            model.navigateToVideoPlayback(view,args)
        }
    }

    private val pexelsVideoAdapter by lazy {
        PexelsVideoAdapter(fragment){view , args ->
            activityModel.videoPlayArguLiveData.value = args
            model.navigateToVideoPlayback(view,args)

        }
    }

    fun setPageIndex(pageIndex:Int){
        pixabayVideoAdapyer.pageIdex = pageIndex
        pexelsVideoAdapter.pageIdex = pageIndex
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerview = recyclerView
        bindingData()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        cancelAllJob()
        clearAdapters(recyclerView)
        recyclerview = null
    }

    fun reloadData(){

        shareElementIndex = 0
        pexelsVideoAdapter.sharedElementIndex=0
        pixabayVideoAdapyer.sharedElementIndex = 0
        bindingData()
    }

    private fun bindingData(){
        cancelAllJob()
        val job1 = fragment.lifecycleScope.launch {
            model.searchVideo(query).collectLatest {
                pixabayVideoAdapyer.submitData(it)
            }
        }

        jobs.add(job1)
        val job2 = fragment.lifecycleScope.launch {
            model.searchVideoFromPexels(query).collectLatest {
                pexelsVideoAdapter.submitData(it)
            }
        }
        jobs.add(job2)
    }

    private fun cancelAllJob(){
        for(job in jobs){
            job.cancel()
        }
        jobs.clear()
    }
    private fun clearAdapters(recyclerView: RecyclerView){
        recyclerView.children.forEachIndexed { index, view ->
            if(view is RecyclerView){
                view.adapter = null
            }
        }
    }

    fun onStop(){

        recyclerview?.children?.forEachIndexed { index, view ->
            if(view is RecyclerView){
                val layoutManager = view.layoutManager as GridLayoutManager
                val firstPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                val lastPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                when(index){
                    0 ->{
                        pixabayVideoAdapyer.reStoreFirstPosition = firstPosition
                        pixabayVideoAdapyer.reStoreLastPostion = lastPosition
                    }
                    1 ->{
                        pexelsVideoAdapter.reStoreFirstPosition = firstPosition
                        pexelsVideoAdapter.reStoreLastPostion = lastPosition
                    }
                }
            }
        }


    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val holder = RecyclerViewHolder(RecyclerViewLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        //for video,the each cover of video is the same size,so change the layout manager
        holder.binding.recycleView.layoutManager = GridLayoutManager(parent.context,2)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
       when(position){
           0 -> initPixabayRecyclerView(holder)
           1 -> initPexelsRecyclerView(holder)
       }
    }

    fun initPixabayRecyclerView(holder: RecyclerViewHolder){
        pixabayVideoAdapyer.sharedElementIndex = shareElementIndex
        holder.binding.recycleView.adapter = pixabayVideoAdapyer
        holder.binding.recycleView.tag = 0
    }

    fun initPexelsRecyclerView(holder: RecyclerViewHolder){
        pexelsVideoAdapter.sharedElementIndex = shareElementIndex
        holder.binding.recycleView.adapter = pexelsVideoAdapter
        holder.binding.recycleView.tag = 1
    }

    override fun getItemCount(): Int {
        return 2
    }
}