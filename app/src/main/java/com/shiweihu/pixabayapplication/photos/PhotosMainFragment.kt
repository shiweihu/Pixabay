package com.shiweihu.pixabayapplication.photos

import android.content.Context
import android.os.Bundle
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
import com.shiweihu.pixabayapplication.BaseFragment

import com.shiweihu.pixabayapplication.R

import com.shiweihu.pixabayapplication.databinding.FragmentMainPhotosBinding
import com.shiweihu.pixabayapplication.viewModle.PhotoFragmentMainViewModel
import dagger.hilt.android.AndroidEntryPoint
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

    private val categoryAdapter by lazy{
        CategoryAdapter(this.requireContext()){
            if(!isInput){
                query(queryStr)
            }
        }
    }

    private val photosAdapter by lazy {
        PhotosAdapter(model,this){
            model.sharedElementIndex = it
        }
    }


    private fun initShareElement(binding:FragmentMainPhotosBinding){
        this.setExitSharedElementCallback(object: SharedElementCallback(){
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                super.onMapSharedElements(names, sharedElements)
                val viewHolder = binding.recycleView.findViewHolderForAdapterPosition(model.sharedElementIndex)
                if(sharedElements != null && names != null && viewHolder !=null){
                    sharedElements[names[0]] = (viewHolder as PhotosAdapter.ImageViewHolder).binding.imageView
                }
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.searchPhotos("",photosAdapter)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentMainPhotosBinding.inflate( LayoutInflater.from(this.context) ,container,false).also { it ->
            it.appBar.setExpanded(false)
            it.categoryGrid.adapter = categoryAdapter
            it.recycleView.adapter = photosAdapter
            it.recycleView.setItemViewCacheSize(20)
            initShareElement(it)
            initMenu(it.toolBar.menu)
            val view =
                it.recycleView.layoutManager?.findViewByPosition(model.sharedElementIndex)
            if (view == null) {
                postponeEnterTransition(
                    resources.getInteger(R.integer.post_pone_time).toLong(),
                    TimeUnit.MILLISECONDS
                )
                it.recycleView.layoutManager?.scrollToPosition(model.sharedElementIndex)
            }

        }
        return binding.root.also {
            it.isSaveEnabled = true
        }
    }

    private fun query(q:String){
        var category: String = categoryAdapter.checkedItem
        model.searchPhotos(q, photosAdapter, category)
        photosAdapter.refresh()
        model.sharedElementIndex = 0
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
                            query(query)
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

    override fun onDestroyView() {
        super.onDestroyView()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exitTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.grid_exit_transition)

        reenterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.grid_exit_transition)
    }


    companion object {

    }
}