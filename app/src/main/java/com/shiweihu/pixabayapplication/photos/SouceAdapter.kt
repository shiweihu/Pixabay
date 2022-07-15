package com.shiweihu.pixabayapplication.photos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.shiweihu.pixabayapplication.FragmentsAdapter
import com.shiweihu.pixabayapplication.databinding.CardImageLayoutBinding
import com.shiweihu.pixabayapplication.databinding.RecyclerViewLayoutBinding
import com.shiweihu.pixabayapplication.viewModle.PhotosMainFragmentModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.zip.Inflater


class SouceAdapter(val fragment:Fragment,
                   val model: PhotosMainFragmentModel) : RecyclerView.Adapter<SouceAdapter.RecyclerViewHolder>() {

    class RecyclerViewHolder(
        val binding: RecyclerViewLayoutBinding
    ): RecyclerView.ViewHolder(binding.root)

    private val jobs:MutableList<Job> = mutableListOf()

    var shareElementIndex = 0
    var query:String = ""


    private var recyclerview:RecyclerView? = null

    private val pixabayPhotosAdapter by lazy {
        PixabayPhotosAdapter(fragment){view,position,args->
           model.navigateToBigPicture(view,args,position,0)
        }
    }

    private val pexelsPhotosAdapter by lazy {
        PexelsPhotosAdapter(fragment){view,position,args->
            model.navigateToBigPicture(view,args,position,1)
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

    fun reloadData(){
        cancelAllJob()
        shareElementIndex = 0
        val job1 = fragment.lifecycleScope.launch {
            model.searchPhotosFromPixabay(query).collectLatest {
                pixabayPhotosAdapter.submitData(it)
            }
        }
        jobs.add(job1)
        pixabayPhotosAdapter.sharedElementIndex = 0
        val job2 = fragment.lifecycleScope.launch {
            model.searchPhotosFromPexels(query).collectLatest {
                pexelsPhotosAdapter.submitData(it)
            }
        }
        jobs.add(job2)
        pexelsPhotosAdapter.sharedElementIndex=0

    }


    fun initPixabayRecyclerView(holder: RecyclerViewHolder){
        pixabayPhotosAdapter.sharedElementIndex = shareElementIndex
        holder.binding.recycleView.adapter = pixabayPhotosAdapter
//        //holder.binding.recycleView.isSaveEnabled = true
//        val job = fragment.lifecycleScope.launch {
//            model.searchPhotosFromPixabay(query).collectLatest {
//                pixabayPhotosAdapter.submitData(it)
//            }
//        }
//        jobs.add(job)
        holder.binding.recycleView.tag = 0

    }

    fun initPexelsRecyclerView(holder: RecyclerViewHolder){
        pexelsPhotosAdapter.sharedElementIndex = shareElementIndex
        holder.binding.recycleView.adapter = pexelsPhotosAdapter
//        //holder.binding.recycleView.isSaveEnabled = true
//        val job = fragment.lifecycleScope.launch {
//            model.searchPhotosFromPexels(query).collectLatest {
//                pexelsPhotosAdapter.submitData(it)
//            }
//        }
//        jobs.add(job)
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
                        pixabayPhotosAdapter.reStoreFirstPosition = firstPosition.min()
                        pixabayPhotosAdapter.reStoreLastPostion = lastPosition.max()
                    }
                    1 ->{
                        pexelsPhotosAdapter.reStoreFirstPosition = firstPosition.min()
                        pexelsPhotosAdapter.reStoreLastPostion = lastPosition.max()
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