package com.shiweihu.pixabayapplication.photos

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.app.SharedElementCallback
import androidx.core.view.forEach

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.tabs.TabLayout

import com.google.android.material.tabs.TabLayoutMediator
import com.shiweihu.pixabayapplication.BaseFragment

import com.shiweihu.pixabayapplication.R

import com.shiweihu.pixabayapplication.databinding.FragmentMainPhotosBinding
import com.shiweihu.pixabayapplication.viewModle.FragmentComunicationViewModel
import com.shiweihu.pixabayapplication.viewModle.PhotosMainFragmentModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PhotosMainFragment : BaseFragment() {


    private val activeModel:FragmentComunicationViewModel by activityViewModels()
    private val model: PhotosMainFragmentModel by viewModels()


    private var binding:FragmentMainPhotosBinding? = null

    private var sourceIndex = 0

    private val fragmentAdapter:SouceAdapter by lazy {
        SouceAdapter(this,model,activeModel)
    }

    private var tabLayoutMediator:TabLayoutMediator? = null



    private fun initShareElement(){
        this.setExitSharedElementCallback(object: SharedElementCallback(){
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                super.onMapSharedElements(names, sharedElements)

                val recyclerView =  binding?.viewPager?.findViewWithTag<RecyclerView>(sourceIndex)
                var tag = ""
                when(sourceIndex){
                    0 ->{
                        tag = "PixabayPhotos-${fragmentAdapter.shareElementIndex}"
                    }
                    1 ->{
                        tag = "PexelsPhotos-${fragmentAdapter.shareElementIndex}"
                    }
                }
                val view = recyclerView?.findViewWithTag<View>(tag)
                if(names != null && sharedElements != null && view != null){
                    sharedElements[names[0]] = view
                }
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activeModel.pictureQueryText.observe(this){
            fragmentAdapter.startQuery(it)
            this.startPostponedEnterTransition()
        }

        activeModel.pictureItemPosition.observe(this){
            fragmentAdapter.shareElementIndex = it
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainPhotosBinding.inflate(inflater,container,false).also { it ->
//            it.recycleView.adapter = photoAdapter
//            it.recycleView.setItemViewCacheSize(30)
           // initShareElement()
           // it.appBar.setExpanded(false)
            it.viewPager.adapter = fragmentAdapter
            it.viewPager.offscreenPageLimit = 1
            it.viewPager.isSaveEnabled = false
            it.viewPager.isUserInputEnabled = false
            it.viewPager.setCurrentItem(sourceIndex,false)
            fragmentAdapter.setPageIndex(sourceIndex)
            tabLayoutMediator = TabLayoutMediator(it.tabs, it.viewPager) { tab, position ->
                tab.text = getTabTitle(position)
            }.also {
                it.attach()
            }
            it.tabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    sourceIndex = tab.position
                    fragmentAdapter.setPageIndex(tab.position)
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {

                }

                override fun onTabReselected(tab: TabLayout.Tab) {

                }

            })

            initMenu(it.toolBar.menu)
            initShareElement()

        }
        postponeEnterTransition(resources.getInteger(R.integer.post_pone_time).toLong(), TimeUnit.MILLISECONDS)
        return binding?.root
    }



    private fun getTabTitle(position: Int): String? {
        return when (position) {
            PIXABAY_INDEX -> getString(R.string.pixabay_title)
            PEXELS_INDEX -> getString(R.string.pexels_title)
            else -> null
        }
    }

//    var adapterJob: Job? = null
//
//    private fun query(q:String,category:String){
//        adapterJob?.cancel()
//        adapterJob = viewLifecycleOwner.lifecycleScope.launch {
//            model.searchPhotos(q,category).collectLatest {
//                photoAdapter.submitData(it)
//            }
//        }
//    }

    private fun initMenu(menu: Menu){
        menu.forEach {
            when (it.itemId) {
                R.id.action_search -> {
                    val searchView = it.actionView as SearchView
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String): Boolean {
                            activeModel.pictureQueryText.postValue(query)
                            searchView.clearFocus()
                            binding?.root?.requestFocus()
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            return true
                        }
                    })
                }
            }
        }


    }

    override fun onStop() {
        super.onStop()
//        val recyclerView =  binding?.viewPager?.findViewWithTag<RecyclerView>(sourceIndex)
//        val firstPositions = (recyclerView?.layoutManager as StaggeredGridLayoutManager).findFirstCompletelyVisibleItemPositions(null)
//        val lastPositions = (recyclerView?.layoutManager as StaggeredGridLayoutManager).findLastCompletelyVisibleItemPositions(null)
//        firstPosition = firstPositions.minOrNull() ?: 0
//
//        lastPosition = lastPositions.maxOrNull() ?: 0
         fragmentAdapter.onStop()




    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        binding?.viewPager?.adapter = null
        binding = null


    }


    override fun onStart() {
        super.onStart()

    }


    override fun onResume() {
        super.onResume()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //query(queryStr,category)

        exitTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.grid_exit_transition)

        reenterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.grid_exit_transition)
    }


    companion object {
        const val PIXABAY_INDEX = 0
        const val PEXELS_INDEX = 1
    }
}