package com.shiweihu.pixabayapplication.video

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.FragmentMainVideoBinding
import com.shiweihu.pixabayapplication.photos.CategoryAdapter
import com.shiweihu.pixabayapplication.viewModle.VideoFragmentMainViewModel
import dagger.hilt.android.AndroidEntryPoint

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

    var binding:FragmentMainVideoBinding? = null

    val model:VideoFragmentMainViewModel by viewModels()


    private val categoryAdapter by lazy{
        CategoryAdapter(this.requireContext()){

        }
    }

    private val videosAdapter by lazy {
        VideosAdapter(model,this){

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.feedData(adapter = videosAdapter)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainVideoBinding.inflate(inflater,container,false).also{
            it.categoryGrid.adapter = categoryAdapter
            it.recycleView.adapter = videosAdapter

        }
        return binding?.root
    }

    companion object {

    }
}