package com.shiweihu.pixabayapplication.photos

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.room.Query
import com.shiweihu.pixabayapplication.databinding.RecyclerViewLayoutBinding
import com.shiweihu.pixabayapplication.viewModle.FragmentComunicationViewModel
import com.shiweihu.pixabayapplication.viewModle.PhotosMainFragmentModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SouceAdapter(val fragment:Fragment,
                   val model: PhotosMainFragmentModel,
                   val activityModel: FragmentComunicationViewModel
) : RecyclerView.Adapter<SouceAdapter.RecyclerViewHolder>() {

    class RecyclerViewHolder(
        val binding: RecyclerViewLayoutBinding
    ): RecyclerView.ViewHolder(binding.root)

    private val jobs:MutableList<Job> = mutableListOf()

    var shareElementIndex = 0
    private var query:String = ""


    private var recyclerview:RecyclerView? = null

    private val pixabayPhotosAdapter by lazy {
        PixabayPhotosAdapter(fragment){view,args->
            activityModel.bigPictureArguLiveData.value = args
            model.navigateToBigPicture(view,args.currentIndex)
        }
    }

    private val pexelsPhotosAdapter by lazy {
        PexelsPhotosAdapter(fragment){view,args->
            activityModel.bigPictureArguLiveData.value = args
            model.navigateToBigPicture(view,args.currentIndex)
        }
    }


    fun setPageIndex(pageIndex:Int){
        pixabayPhotosAdapter.pageIdex = pageIndex
        pexelsPhotosAdapter.pageIdex = pageIndex
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(RecyclerViewLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }



    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        when(position){
            0 ->{
                initPixabayRecyclerView(holder)
            }
            1 ->{
                initPexelsRecyclerView(holder)
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun startQuery(query: String){
        if(this.query != query){
            this.query = query
            reloadData()
        }

    }

    fun reloadData(){
        shareElementIndex = 0
        pixabayPhotosAdapter.sharedElementIndex = 0
        pexelsPhotosAdapter.sharedElementIndex=0
        bindingData()
    }
    private fun bindingData(){
        cancelAllJob()
        val job1 = fragment.lifecycleScope.launch {
            model.searchPhotosFromPixabay(query).collectLatest {
                pixabayPhotosAdapter.submitData(it)
            }
        }
        jobs.add(job1)
        val job2 = fragment.lifecycleScope.launch {
            model.searchPhotosFromPexels(query).collectLatest {
                pexelsPhotosAdapter.submitData(it)
            }
        }
        jobs.add(job2)

    }



    private fun initPixabayRecyclerView(holder: RecyclerViewHolder){
        pixabayPhotosAdapter.sharedElementIndex = shareElementIndex
        holder.binding.recycleView.adapter = pixabayPhotosAdapter
        holder.binding.recycleView.tag = 0

    }

    private fun initPexelsRecyclerView(holder: RecyclerViewHolder){
        pexelsPhotosAdapter.sharedElementIndex = shareElementIndex
        holder.binding.recycleView.adapter = pexelsPhotosAdapter
        holder.binding.recycleView.tag = 1
    }

    override fun onViewAttachedToWindow(holder: RecyclerViewHolder) {
        super.onViewAttachedToWindow(holder)

    }

    fun onStop(){

        recyclerview?.children?.forEachIndexed { index, view ->
            if(view is RecyclerView){
                val layoutManager = view.layoutManager as StaggeredGridLayoutManager
                val firstPosition = layoutManager.findFirstCompletelyVisibleItemPositions(null)
                val lastPosition = layoutManager.findLastCompletelyVisibleItemPositions(null)
                when(index){
                    0 ->{
                        pixabayPhotosAdapter.reStoreFirstPosition = firstPosition.firstOrNull() ?: firstPosition.last()
                        pixabayPhotosAdapter.reStoreLastPostion = lastPosition.lastOrNull() ?: lastPosition.first()
                    }
                    1 ->{
                        pexelsPhotosAdapter.reStoreFirstPosition = firstPosition.firstOrNull() ?: firstPosition.last()
                        pexelsPhotosAdapter.reStoreLastPostion = lastPosition.lastOrNull() ?: lastPosition.first()
                    }
                }
            }
        }


    }



    override fun onViewDetachedFromWindow(holder: RecyclerViewHolder) {
        super.onViewDetachedFromWindow(holder)

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerview = recyclerView
        bindingData()
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
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        cancelAllJob()
        clearAdapters(recyclerView)
        recyclerview = null


    }

    override fun getItemCount(): Int {
        return 2
    }


}