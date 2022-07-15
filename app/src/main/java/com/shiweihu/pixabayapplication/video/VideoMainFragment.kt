package com.shiweihu.pixabayapplication.video

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.app.SharedElementCallback
import androidx.core.view.forEachIndexed
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.FragmentMainVideoBinding
import com.shiweihu.pixabayapplication.photos.CategoryAdapter
import com.shiweihu.pixabayapplication.viewModle.FragmentComunicationViewModel
import com.shiweihu.pixabayapplication.viewModle.VideoFragmentMainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VideoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class VideoFragment : Fragment() {

    private var binding:FragmentMainVideoBinding? = null

    private val model:VideoFragmentMainViewModel by viewModels()
    private val sharedModel: FragmentComunicationViewModel by activityViewModels()

    private var queryStr:String = ""

    private var category:String = ""
    private var job:Job? = null

    private var firstVisiblePosition = 0
    private var lastVisiblePosition = 0


    private val videosAdapter by lazy {
        VideosAdapter(model,this)
            .also {adapter ->
            adapter.loadStateFlow.distinctUntilChangedBy {
                it.refresh
            }.filter {
                it.refresh is LoadState.NotLoading
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = TransitionInflater.from(this.requireContext()).inflateTransition(R.transition.video_shared_element_transition)
        reenterTransition = TransitionInflater.from(this.requireContext()).inflateTransition(R.transition.video_shared_element_transition)

        sharedModel.videoItemPosition.observe(this){
            model.videoPosition = it


            if(model.videoPosition < firstVisiblePosition || model.videoPosition>lastVisiblePosition){
                binding?.recycleView?.scrollToPosition(model.videoPosition)
            }
        }

        this.setExitSharedElementCallback(object: SharedElementCallback(){
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                super.onMapSharedElements(names, sharedElements)
                val viewHolder = binding?.recycleView?.findViewHolderForAdapterPosition(model.videoPosition)
                if(sharedElements != null && names != null && viewHolder !=null){
                    sharedElements[names[0]] = (viewHolder as VideosAdapter.CoverViewHolder).binding.imageView
                }

            }
        })


    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainVideoBinding.inflate(inflater,container,false).also{ binding ->
            binding.appBar.setExpanded(false)
            binding.categoryGrid.adapter = CategoryAdapter(this.requireContext()){
                category = it
                query(queryStr,category = category)
            }.also {
                it.checkedItem = category
            }
            binding.recycleView.adapter = videosAdapter
            initMenu(binding.toolBar.menu)
        }
        postponeEnterTransition()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        query(queryStr,category = category)
    }

    override fun onStop() {
        super.onStop()
        val layoutManager = (binding?.recycleView?.layoutManager as GridLayoutManager)
        firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
        job = null
        binding?.recycleView?.adapter = null
        binding = null
    }



    private fun query(q:String? = null , category: String = ""){
        job?.cancel()
        job =  viewLifecycleOwner.lifecycleScope.launch {
            model.searchVideo(q?.trim() ?: "", category).collectLatest {
                videosAdapter.submitData(it)
            }
        }
    }

    private fun initMenu(menu: Menu){
        menu.forEachIndexed { _, item ->
            when (item.itemId) {
                R.id.action_search -> {
                    val searchView = item.actionView as SearchView
//                    searchView.setOnQueryTextFocusChangeListener { view, b ->
//                        isInput = b
//                    }
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String): Boolean {
                            binding?.recycleView?.scrollToPosition(0)
                            query(query,category)
                            queryStr = query
                            searchView.clearFocus()
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

    companion object {

    }
}