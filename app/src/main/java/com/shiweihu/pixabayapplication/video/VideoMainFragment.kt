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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.FragmentMainVideoBinding
import com.shiweihu.pixabayapplication.photos.PhotosMainFragment
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

    private var tabLayoutMediator:TabLayoutMediator? = null

    private var sourceIndex = 0

    private val adapter:VideoSourceAdapter by lazy {
        VideoSourceAdapter(this,model)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = TransitionInflater.from(this.requireContext()).inflateTransition(R.transition.video_shared_element_transition)
        reenterTransition = TransitionInflater.from(this.requireContext()).inflateTransition(R.transition.video_shared_element_transition)

        sharedModel.videoItemPosition.observe(this){
            adapter.shareElementIndex = it
        }
        sharedModel.videoQueryText.observe(this){
            adapter.query = it
            adapter.reloadData()
        }

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
                        tag = "PixabayVideo-${adapter.shareElementIndex}"
                    }
                    1 ->{
                        tag = "PexelsVideo-${adapter.shareElementIndex}"
                    }
                }
                val view = recyclerView?.findViewWithTag<View>(tag)
                if(names != null && sharedElements != null && view != null){
                    sharedElements[names[0]] = view
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
            initMenu(binding.toolBar.menu)
            binding.viewPager.adapter = adapter
            binding.viewPager.offscreenPageLimit = 1
            binding.viewPager.isSaveEnabled = false
            binding.viewPager.isUserInputEnabled = false
            binding.viewPager.registerOnPageChangeCallback(object:
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    sourceIndex = position
                    adapter.setPageIndex(position)
                }
            })
            binding.viewPager.setCurrentItem(sourceIndex,false)

            tabLayoutMediator = TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
                tab.text = getTabTitle(position)
            }.also {
                it.attach()
            }
        }
        postponeEnterTransition()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onStop() {
        super.onStop()

        adapter.onStop()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        binding?.viewPager?.adapter = null
        binding = null
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
                            sharedModel.videoQueryText.postValue(query)
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

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            PIXABAY_INDEX -> getString(R.string.pixabay_title)
            PEXELS_INDEX -> getString(R.string.pexels_title)
            else -> null
        }
    }

    companion object {
        const val PIXABAY_INDEX = 0
        const val PEXELS_INDEX = 1
    }
}