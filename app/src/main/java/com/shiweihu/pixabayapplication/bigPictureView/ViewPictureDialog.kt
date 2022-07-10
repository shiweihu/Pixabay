package com.shiweihu.pixabayapplication.bigPictureView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.ImageViewBinding
import com.shiweihu.pixabayapplication.databinding.ViewPictureDialogLayoutBinding
import java.util.zip.Inflater
import kotlin.math.round
import kotlin.math.sqrt

class ViewPictureDialog (context: Context,val url:String,res:Int): AlertDialog(context,res) {

    private lateinit var binding:ViewPictureDialogLayoutBinding

    private var origMatrix:Matrix? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ViewPictureDialogLayoutBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        window?.attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes?.height = WindowManager.LayoutParams.MATCH_PARENT
        binding.url = url
        val origFirstPoint = Point()
        val origSecondPoint = Point()
        binding.imgView.setOnTouchListener { v, event ->
            var x = event.x
            var y = event.y
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    origFirstPoint.set(x.toInt(),y.toInt())
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    origMatrix = binding.imgView.imageMatrix
                    origFirstPoint.set(x.toInt(), y.toInt())
                    x = event.getX(1)
                    y = event.getY(1)
                    origSecondPoint.set(x.toInt(), y.toInt())
                }
                MotionEvent.ACTION_MOVE -> {
                    if (binding.imgView.scaleType != ImageView.ScaleType.MATRIX) {
                        binding.imgView.scaleType = ImageView.ScaleType.MATRIX
                    }
                    if (event.pointerCount >= 2) {
                        val x1 = event.getX(1)
                        val y1 = event.getY(1)
                        val dis = getDistance(x.toInt(), y.toInt(), x1.toInt(), y1.toInt())
                        val lastdis = getDistance(
                            origFirstPoint.x,
                            origFirstPoint.y,
                            origSecondPoint.x,
                            origSecondPoint.y
                        )
                        val scale = dis.toFloat() / lastdis.toFloat()
                        val matrix = Matrix()
                        matrix.set(binding.imgView.imageMatrix)
                        matrix.postScale(scale, scale, (x + x1) / 2, (y + y1) / 2)
                        binding.imgView.imageMatrix = matrix
                        origFirstPoint.set(x.toInt(), y.toInt())
                        origSecondPoint.set(x1.toInt(), y1.toInt())
                    }else{
                        val diff_x = x - origFirstPoint.x
                        val diff_y = y - origFirstPoint.y
                        val matrix = Matrix()
                        matrix.set(binding.imgView.imageMatrix)
                        matrix.postTranslate(diff_x,diff_y)
                        binding.imgView.imageMatrix = matrix
                        origFirstPoint.set(x.toInt(), y.toInt())

                    }
                }
                MotionEvent.ACTION_POINTER_UP ->{

                }
                MotionEvent.ACTION_CANCEL -> {

                }
                MotionEvent.ACTION_UP -> {
                }
            }


            return@setOnTouchListener true
        }
        binding.root.setOnClickListener {
            this.cancel()
        }
    }

    private fun getDistance(x1:Int,y1:Int,x2:Int,y2:Int):Int{
        return sqrt( (x1-x2)*(x1-x2).toDouble() + (y1-y2)*(y1-y2) ).toInt()
    }



    private fun restoreCenter(binding: ImageViewBinding){
        val m = binding.imageView.imageMatrix
        val rectf = RectF(0f,0f,binding.imageView.drawable.intrinsicWidth.toFloat(),binding.imageView.drawable.intrinsicHeight.toFloat())
        m.mapRect(rectf)
        val postX = binding.imageView.width/2 - (rectf.right + rectf.left)/2
        val postY = binding.imageView.height/2 - (rectf.bottom + rectf.top)/2
        val matrix = Matrix()
        matrix.set(m)
        matrix.postTranslate(postX,postY)
        binding.imageView.imageMatrix = matrix
    }




    companion object{
          class Builder(){
            private var dialog:ViewPictureDialog? = null
            fun show(context: Context,url:String){
                dialog =  ViewPictureDialog(context,url, R.style.view_picture_dialog_theme)
                dialog?.show()
                dialog?.setCancelable(true)
            }
            fun close(){
                dialog?.cancel()
            }
        }
    }

}