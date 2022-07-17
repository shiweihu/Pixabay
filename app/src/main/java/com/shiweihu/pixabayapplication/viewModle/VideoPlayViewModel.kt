package com.shiweihu.pixabayapplication.viewModle

import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_MOVIES
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.transformer.TransformationException
import com.google.android.exoplayer2.transformer.TransformationResult
import com.google.android.exoplayer2.transformer.Transformer
import com.shiweihu.pixabayapplication.MyApplication
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.net.ApplicationModule
import com.shiweihu.pixabayapplication.videoPlayView.VideoPlayActivity
import com.shiweihu.pixabayapplication.videoPlayView.VideoPlayActivityArgs

import com.shiweihu.pixabayapplication.videoPlayView.VideoPlayFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
class VideoPlayViewModel @Inject constructor(
    val videoPlayerPosition: ApplicationModule.Companion.VideoPlayerPosition
):ViewModel() {



    fun navigateToUserProfilePage(context: Context, username:String, userid:String){
        navigateToWeb(context,"https://pixabay.com/users/${username}-${userid}/")
    }
    fun navigateToUserProfilePageOnPexels(context: Context,url:String){
        navigateToWeb(context,url)
    }

    fun navigateToWeb(context: Context, url:String){
        val uri = Uri.parse(url)
        context.startActivity(Intent(Intent.ACTION_VIEW,uri))
    }

    fun navigateToFullScreen(player: View, uri: Uri,position:Long){
        player.findNavController().navigate(R.id.full_screen,VideoPlayActivityArgs(uri,position).toBundle(),
            null,
            FragmentNavigatorExtras(
                player to VideoPlayActivity.TRANSITION_NAME
             )
        )

    }


    fun downloadVideo(context:Context,mediaItem:MediaItem,callBack:()->Unit){
       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
           downloadV26(context,mediaItem,callBack)
       }else{
           download(context,mediaItem,callBack)
       }

    }
    private fun download(context: Context,mediaItem:MediaItem,callBack:()->Unit){

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
                    val intent = Intent(Intent.ACTION_SEND).also {
                        it.type = "video/*"
                        it.putExtra(Intent.EXTRA_STREAM, ClipData.Item(uri).uri)
                    }

                    val chose_intent = Intent.createChooser(intent,context.resources.getString(R.string.app_choser_title))
                    context.startActivity(chose_intent)
                    callBack()
                }

                override fun onTransformationError(
                    inputMediaItem: MediaItem,
                    exception: TransformationException
                ) {
                    super.onTransformationError(inputMediaItem, exception)

                    exception.printStackTrace()
                    callBack()
                }
            }).build()
        transformer.startTransformation(mediaItem,filePath)
    }

    @RequiresApi(26)
    private fun downloadV26(context: Context,mediaItem:MediaItem,callBack:()->Unit){
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
                    val intent = Intent(Intent.ACTION_SEND).also {
                        it.type = "video/*"
                        it.putExtra(Intent.EXTRA_STREAM, outputUri)
                    }
                    val chose_intent = Intent.createChooser(intent,context.resources.getString(R.string.app_choser_title))
                    context.startActivity(chose_intent)
                    callBack()
                }

                override fun onTransformationError(
                    inputMediaItem: MediaItem,
                    exception: TransformationException
                ) {
                    super.onTransformationError(inputMediaItem, exception)

                    exception.printStackTrace()
                    callBack()
                }
            })
            .build()



        val fileDescriptor = context.contentResolver.openFileDescriptor(outputUri!!,"w")
        transformer.startTransformation(mediaItem, fileDescriptor!! )
    }


    override fun onCleared() {
        super.onCleared()
        MyApplication.mHandler.removeCallbacksAndMessages(null)
    }

}