package com.shiweihu.pixabayapplication.video

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.forEachIndexed
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

    private var binding:FragmentMainVideoBinding? = null

    private val model:VideoFragmentMainViewModel by viewModels()

    private var queryStr:String = ""

    private var category:String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            }
            binding.recycleView.adapter = VideosAdapter(model,this).also {
                model.searchVideo(adapter = it)
            }
            initMenu(binding.toolBar.menu)
            binding.lifecycleOwner = viewLifecycleOwner
        }
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun query(q:String? = null , category: String = ""){
        val videosAdapter = ( binding?.recycleView?.adapter as VideosAdapter)
        model.searchVideo(q?.trim() ?: "", videosAdapter, category)
        videosAdapter.refresh()
    }

    private fun initMenu(menu: Menu){
        menu.forEachIndexed { index, item ->
            when (item.itemId) {
                R.id.action_search -> {
                    val searchView = item.actionView as SearchView
//                    searchView.setOnQueryTextFocusChangeListener { view, b ->
//                        isInput = b
//                    }
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

    companion object {

    }
}