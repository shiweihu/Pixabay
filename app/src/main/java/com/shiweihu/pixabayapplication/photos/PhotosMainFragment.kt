package com.shiweihu.pixabayapplication.photos

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.viewModels
import com.google.android.material.appbar.AppBarLayout
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.bigPictureView.BigPictureFragment
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
        PhotosAdapter(model){
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
                    binding.recycleView.layoutManager?.scrollToPosition(model.sharedElementIndex)
                    binding.executePendingBindings()
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
        binding = FragmentMainPhotosBinding.inflate( LayoutInflater.from(this.context) ,null,false).also {
            it.categoryGrid.adapter = categoryAdapter
            it.recycleView.adapter = photosAdapter
            it.recycleView.isSaveEnabled = false;
            it.categoryGrid.isSaveEnabled = false

            initShareElement(it)

            it.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                Log.println(Log.DEBUG,"appbar",verticalOffset.toString())

            })
        }

        return binding.root
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