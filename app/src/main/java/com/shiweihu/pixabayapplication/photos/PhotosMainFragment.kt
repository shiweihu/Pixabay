package com.shiweihu.pixabayapplication.photos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.appbar.AppBarLayout
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


    val model:PhotoFragmentMainViewModel by viewModels()

    private val categoryAdapter by lazy{
        CategoryAdapter(this.requireContext())
    }

    private val photosAdapter by lazy {
        PhotosAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model.searchPhotos("",photosAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainPhotosBinding.inflate(inflater,container,false).also {
            it.categoryGrid.adapter = categoryAdapter
            it.recycleView.adapter = photosAdapter


            it.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                Log.println(Log.DEBUG,"appbar",verticalOffset.toString())



            })
        }
        return binding.root
    }

    companion object {

    }
}