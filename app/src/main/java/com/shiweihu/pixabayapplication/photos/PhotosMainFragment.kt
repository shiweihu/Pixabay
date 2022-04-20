package com.shiweihu.pixabayapplication.photos

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionInflater
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment

import androidx.appcompat.widget.SearchView

import androidx.core.app.SharedElementCallback
import androidx.core.view.forEachIndexed

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.shiweihu.pixabayapplication.BaseFragment

import com.shiweihu.pixabayapplication.R

import com.shiweihu.pixabayapplication.databinding.FragmentMainPhotosBinding
import com.shiweihu.pixabayapplication.viewModle.PhotoFragmentMainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Integer.min
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PhotosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class PhotosMainFragment : BaseFragment() {


    private val model:PhotoFragmentMainViewModel by viewModels()

    private var queryStr:String = ""
    private var isInput = false

    private var binding:FragmentMainPhotosBinding? = null
    private var category:String = ""

    private val handler = Handler(Looper.getMainLooper())

    private var firstPosition = 0
    private var lastPosition = 0

    private var testbinding:WeakReference<View>? = null

    private val photoAdapter by lazy {
        PhotosAdapter(model,this){
            model.sharedElementIndex = it
        }.also {adapter ->
                adapter.loadStateFlow.distinctUntilChangedBy {
                    it.refresh
                }.filter {
                    it.refresh is LoadState.NotLoading
                }
            }
    }

    private fun initShareElement(){
        this.setExitSharedElementCallback(object: SharedElementCallback(){
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                super.onMapSharedElements(names, sharedElements)

                val viewHolder = binding?.recycleView?.findViewHolderForAdapterPosition(model.sharedElementIndex)
                if(sharedElements != null && names != null && viewHolder !=null){
                    sharedElements[names[0]] = (viewHolder as PhotosAdapter.ImageViewHolder).binding.imageView
                }
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val test = testbinding?.get()
        binding = FragmentMainPhotosBinding.inflate(inflater,container,false).also { it ->
            it.appBar.setExpanded(false)
            it.categoryGrid.adapter = CategoryAdapter(this.requireContext()){category ->
                this.category = category
                if(!isInput){
                    query(queryStr,this.category)
                }
            }.also {
                it.checkedItem = this.category
            }
            it.recycleView.adapter = photoAdapter

            it.recycleView.setItemViewCacheSize(50)

            if(model.sharedElementIndex < firstPosition || model.sharedElementIndex > lastPosition){
                it.recycleView.scrollToPosition(model.sharedElementIndex)
            }
            //it.recycleView.setItemViewCacheSize(if(model.sharedElementIndex>2) model.sharedElementIndex else 2)
            //it.recycleView.setItemViewCacheSize(20)
            initShareElement()
            initMenu(it.toolBar.menu)
        }
        testbinding = WeakReference(binding?.root)
        return binding?.root
    }

    var adapterJob: Job? = null

    private fun query(q:String,category:String){
        adapterJob?.cancel()
        adapterJob = viewLifecycleOwner.lifecycleScope.launch {
            model.searchPhotos(q,category).collectLatest {
                photoAdapter.submitData(it)
            }
        }
    }

    private fun initMenu(menu: Menu){
        menu.forEachIndexed { index, item ->
            when (item.itemId) {
                R.id.action_search -> {
                    val searchView = item.actionView as SearchView
                    searchView.setOnQueryTextFocusChangeListener { view, b ->
                        isInput = b
                    }
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String): Boolean {
                            binding?.recycleView?.scrollToPosition(0)
                            query(query,this@PhotosMainFragment.category)
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

    override fun onStop() {
        super.onStop()
        val firstPositions = (binding?.recycleView?.layoutManager as StaggeredGridLayoutManager).findFirstCompletelyVisibleItemPositions(null)
        val lastPositions = (binding?.recycleView?.layoutManager as StaggeredGridLayoutManager).findLastCompletelyVisibleItemPositions(null)
        firstPosition = firstPositions.minOrNull() ?: 0

        lastPosition = lastPositions.maxOrNull() ?: 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapterJob?.cancel()
        binding?.recycleView?.adapter = null
        binding = null

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onStart() {
        super.onStart()
        postponeEnterTransition(resources.getInteger(R.integer.post_pone_time).toLong(),TimeUnit.MILLISECONDS)
    }


    override fun onResume() {
        super.onResume()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        query(queryStr,category)

        exitTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.grid_exit_transition)

        reenterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.grid_exit_transition)
    }


    companion object {

    }
}