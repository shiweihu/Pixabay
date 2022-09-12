package com.shiweihu.pixabayapplication.viewModle

import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_MOVIES
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.transformer.TransformationException
import com.google.android.exoplayer2.transformer.TransformationResult
import com.google.android.exoplayer2.transformer.Transformer
import com.shiweihu.pixabayapplication.MyApplication
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.net.ApplicationModule
import com.shiweihu.pixabayapplication.util.CustomTabActivityHelper
import com.shiweihu.pixabayapplication.videoPlayView.VideoPlayActivity
import com.shiweihu.pixabayapplication.viewArgu.VideoPlayArgu

import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
class VideoPlayViewModel @Inject constructor(
):ViewModel() {

    private val customTabActivityHelper = CustomTabActivityHelper()


    fun navigateToUserProfilePage(context: Context, username:String, userid:String){
        navigateToWeb(context,"https://pixabay.com/users/${username}-${userid}/")
    }
    fun navigateToUserProfilePageOnPexels(context: Context,url:String){
        navigateToWeb(context,url)
    }

    fun navigateToWeb(context: Context, url:String){
        val intentBuilder = CustomTabsIntent.Builder()
        val defaultColors = CustomTabColorSchemeParams.Builder()
            .build()
        intentBuilder.setDefaultColorSchemeParams(defaultColors)
        intentBuilder.setShareState(CustomTabsIntent.SHARE_STATE_ON)
        intentBuilder.setShowTitle(true)
        intentBuilder.setUrlBarHidingEnabled(true)
        CustomTabActivityHelper.openCustomTab(
            context, intentBuilder.build(), Uri.parse(url)){context, uri ->
            if(Intent(Intent.ACTION_VIEW,uri).resolveActivity(context.packageManager)!=null){
                context.startActivity(Intent(Intent.ACTION_VIEW,uri))
            }
        }


    }

    fun onBindingCostomTabSever(context: Context){
        customTabActivityHelper.bindCustomTabsService(context)
    }

    fun onUnBindingCostomTabSever(context: Context){
        customTabActivityHelper.unbindCustomTabsService(context)
    }

    fun navigateToFullScreen(player: View){
        player.findNavController().navigate(R.id.full_screen,null,
            null,
            FragmentNavigatorExtras(
                player to VideoPlayActivity.TRANSITION_NAME
             )
        )

    }

    private fun shareToOtherAPP(context: Context,uri: Uri){
        val intent = Intent(Intent.ACTION_SEND).also {
            it.type = "video/mp4"
            it.putExtra(Intent.EXTRA_STREAM, uri)
        }
        val chose_intent = Intent.createChooser(intent,context.resources.getString(R.string.app_choser_title))
        context.startActivity(chose_intent)
    }

    fun shareVideo(context:Context, mediaItem:MediaItem, callBack:()->Unit){
       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
           downloadV26(context,mediaItem){ uri ->
               if(uri != null){
                   shareToOtherAPP(context,uri)
               }
               callBack()
           }
       }else{
           download(context,mediaItem){ uri ->
               if(uri != null){
                   shareToOtherAPP(context,uri)
               }
               callBack()
           }
       }
    }

    fun downloadVideo(context:Context, mediaItem:MediaItem, callBack:(Uri?)->Unit){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            downloadV26(context,mediaItem){ uri ->
                callBack(uri)
            }
        }else{
            download(context,mediaItem){ uri ->
                callBack(uri)
            }
        }
    }

    private fun download(context: Context,mediaItem:MediaItem,callBack:(Uri?)->Unit){

        val path = Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES).absolutePath
        val filePath = path +"/${UUID.randomUUID()}.mp4"
        val transformer: Transformer = Transformer.Builder(context)
            .addListener(object: Transformer.Listener{
                override fun onTransformationCompleted(inputMediaItem: MediaItem,transformationResult: TransformationResult) {
                    super.onTransformationCompleted(inputMediaItem,transformationResult)

//                    val fileDescriptor = context.contentResolver.openFileDescriptor(outputUri!!,"w")
//                    val file = File(Environment.getDownloadCacheDirectory().path+"/temp.mp4")
//                    val uri = Uri.fromFile(file)
                    val file = File(filePath)
                    val uri = Uri.fromFile(file)
                    callBack(uri)
                }

                override fun onTransformationError(
                    inputMediaItem: MediaItem,
                    exception: TransformationException
                ) {
                    super.onTransformationError(inputMediaItem, exception)
                    exception.printStackTrace()
                    callBack(null)
                }
            }).build()
        transformer.startTransformation(mediaItem,filePath)
    }

    @RequiresApi(26)
    private fun downloadV26(context: Context,mediaItem:MediaItem,callBack:(Uri?)->Unit){
        val content = ContentValues()
        content.put(MediaStore.Video.Media.DISPLAY_NAME,UUID.randomUUID().toString())
        content.put(MediaStore.Video.Media.MIME_TYPE,"video/mp4")
        val outputUri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,content)
        // Configure and create a Transformer instance.
        val transformer: Transformer = Transformer.Builder(context)
            .addListener(object: Transformer.Listener{
                override fun onTransformationCompleted(inputMediaItem: MediaItem,transformationResult: TransformationResult) {
                    super.onTransformationCompleted(inputMediaItem,transformationResult)

//                    val fileDescriptor = context.contentResolver.openFileDescriptor(outputUri!!,"w")
//                    val file = File(Environment.getDownloadCacheDirectory().path+"/temp.mp4")
//                    val uri = Uri.fromFile(file)
                    callBack(outputUri)
                }

                override fun onTransformationError(
                    inputMediaItem: MediaItem,
                    exception: TransformationException
                ) {
                    super.onTransformationError(inputMediaItem, exception)

                    exception.printStackTrace()
                    callBack(null)
                }
            })
            .build()



        val fileDescriptor = context.contentResolver.openFileDescriptor(outputUri!!,"w")
        transformer.startTransformation(mediaItem, fileDescriptor!! )
    }


    var videoData: VideoPlayArgu? = null
    private val data:VideoPlayArgu
    get() {
        return videoData!!
    }

    fun goToPage(context: Context,position:Int){
        data.pageUrls?.get(position)?.let {
            navigateToWeb(context,it)
        }
    }
    fun goToUserPage(context: Context,position:Int){
        when(data.from){
            0 ->{
                val username = data.userNameArray?.get(position)
                val userid = data.useridArray?.get(position)
                if(username != null && userid!=null){
                    navigateToUserProfilePage(context,username,userid)
                }
            }
            1 ->{
                val url = data.useridArray?.get(position)
                if(url != null){
                    navigateToUserProfilePageOnPexels(context,url)
                }
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        MyApplication.mHandler.removeCallbacksAndMessages(null)
    }

}