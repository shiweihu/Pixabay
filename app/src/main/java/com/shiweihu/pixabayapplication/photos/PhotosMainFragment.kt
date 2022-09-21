package com.shiweihu.pixabayapplication.photos

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.transition.TransitionInflater
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.widget.SearchView
import androidx.core.app.SharedElementCallback
import androidx.core.view.forEach

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

import com.google.android.material.tabs.TabLayoutMediator
import com.shiweihu.pixabayapplication.BaseFragment

import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.BottomSheetPicturePickerLayoutBinding

import com.shiweihu.pixabayapplication.databinding.FragmentMainPhotosBinding
import com.shiweihu.pixabayapplication.utils.DisplayUtils
import com.shiweihu.pixabayapplication.viewModle.FragmentComunicationViewModel
import com.shiweihu.pixabayapplication.viewModle.PhotosMainFragmentModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PhotosMainFragment : BaseFragment() {


    private val activeModel:FragmentComunicationViewModel by activityViewModels()
    private val model: PhotosMainFragmentModel by viewModels()


    private var binding:FragmentMainPhotosBinding? = null

    private val viewBinding:FragmentMainPhotosBinding
    get() {
        return binding!!
    }
    private var sourceIndex = 0

    private var adView:AdView? = null


    private val fragmentAdapter:SouceAdapter by lazy {
        SouceAdapter(this,model,activeModel)
    }




    private var tabLayoutMediator:TabLayoutMediator? = null

    private val requestCameraPermissionLauncher =registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLaunch.launch()
        } else {
            // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
            Snackbar.make(viewBinding.root,R.string.permission_deny_notice, Snackbar.LENGTH_LONG).setAction(R.string.go){
                getAppDetailSettingIntent()
            }.show()
        }
    }

    /**
     * navigate to application setting view.
     */
    private fun getAppDetailSettingIntent() {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.parse("package:${requireActivity().packageName}")
        intent.resolveActivity(this.requireActivity().packageManager)?.let {
            startActivity(intent)
        }
    }



    private val cameraLaunch = registerForActivityResult(object :
        ActivityResultContract<Unit, Bitmap?>(){
        var outputUri:Uri? = null
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                val content = ContentValues()
                content.put(MediaStore.Images.Media.DISPLAY_NAME, UUID.randomUUID().toString())
                content.put(MediaStore.Images.Media.MIME_TYPE,"image/png")
                outputUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,content)
                it.putExtra(MediaStore.EXTRA_OUTPUT,outputUri)
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Bitmap? {
            return if(resultCode == Activity.RESULT_OK){
                val inputStream = this@PhotosMainFragment.requireContext().contentResolver.openInputStream(
                    outputUri!!
                )
                BitmapFactory.decodeStream(inputStream)
            }else{
                null
            }
        }

    }) {
        postSearchByBitmap(it)
    }
    private fun postSearchByBitmap(bitmap: Bitmap?){
        if(bitmap != null){
            model.classifyPicture(bitmap){ keyTerm ->
                if(keyTerm !=null && keyTerm.isNotEmpty()){
                    activeModel.pictureQueryText.postValue(keyTerm)
                }else{
                    Toast.makeText(this.requireContext(),R.string.empty_key_term_notice,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val picturePickerLaunch = registerForActivityResult(object :
        ActivityResultContract<Unit, Bitmap?>(){
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(Intent.ACTION_PICK).also {
                it.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*")
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Bitmap? {
            if(resultCode == Activity.RESULT_OK){
                return intent?.let {
                    val uri = it.data
                    val inputStream = this@PhotosMainFragment.requireContext().contentResolver.openInputStream(uri!!)
                    return@let BitmapFactory.decodeStream(inputStream)
                }
            }
            return null
        }
    }) {
        postSearchByBitmap(it)
    }

    private fun initShareElement(){
        this.setExitSharedElementCallback(object: SharedElementCallback(){
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                super.onMapSharedElements(names, sharedElements)

                val recyclerView =  viewBinding.viewPager.findViewWithTag<RecyclerView>(sourceIndex)
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
    ): View {
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
        return viewBinding.root
    }




    private fun showPicturePickerDialog(){
        val bottomSheetDialog =  BottomSheetDialog(this.requireContext())
        val dialogBinding = BottomSheetPicturePickerLayoutBinding.inflate(this.layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)
        bottomSheetDialog.show()

        dialogBinding.cancelBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        dialogBinding.pickFromPhotoAlbum.setOnClickListener {
            picturePickerLaunch.launch()
            bottomSheetDialog.dismiss()
        }
        dialogBinding.pickFromCamera.setOnClickListener {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            bottomSheetDialog.dismiss()
        }



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
                            viewBinding.root.requestFocus()
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            return true
                        }
                    })
                }
                R.id.action_picture_select ->{
                    val camera_btn = (it.actionView as ImageView)
                    camera_btn.setImageResource(R.drawable.camera_icon)
                    camera_btn.setOnClickListener {
                        showPicturePickerDialog()
                    }
                }
            }
        }


    }

    override fun onStop() {
        super.onStop()
//        val recyclerView =  viewBinding.viewPager?.findViewWithTag<RecyclerView>(sourceIndex)
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
        viewBinding.viewPager.adapter = null
        adView?.destroy()
        adView = null
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