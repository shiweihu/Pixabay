package com.shiweihu.pixabayapplication.photos

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment

import androidx.appcompat.widget.SearchView

import androidx.core.app.SharedElementCallback
import androidx.core.view.forEachIndexed

import androidx.fragment.app.viewModels

import com.shiweihu.pixabayapplication.R

import com.shiweihu.pixabayapplication.databinding.FragmentMainPhotosBinding
import com.shiweihu.pixabayapplication.viewModle.PhotoFragmentMainViewModel
import dagger.hilt.android.AndroidEntryPoint

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
class PhotosMainFragment : Fragment() {


    private val model:PhotoFragmentMainViewModel by viewModels()

    private lateinit var binding:FragmentMainPhotosBinding

    private val categoryAdapter by lazy{
        CategoryAdapter(this.requireContext())
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
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainPhotosBinding.inflate( LayoutInflater.from(this.context) ,null,false).also { it ->
            it.categoryGrid.adapter = categoryAdapter
            it.recycleView.adapter = photosAdapter
            initShareElement(it)
            it.recycleView.layoutManager?.scrollToPosition(model.sharedElementIndex)
            initMenu(it.toolBar.menu)
        }
        return binding.root
    }

    private fun initMenu(menu: Menu){
        menu.forEachIndexed { index, item ->
            when (item.itemId) {
                R.id.action_search -> {
                    val searchView = item.actionView as SearchView
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            var category: String = ""
                            categoryAdapter.checkedList.forEachIndexed { index, selectedCategory ->
                                category += if (index == categoryAdapter.checkedList.size - 1) {
                                    selectedCategory
                                } else {
                                    ("$selectedCategory,")
                                }
                            }
                            model.searchPhotos(query, photosAdapter, category)
                            photosAdapter.refresh()
                            model.sharedElementIndex = 0
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exitTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.grid_exit_transition)

        reenterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.grid_exit_transition)

        postponeEnterTransition()

    }

    companion object {

    }
}