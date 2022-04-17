package com.shiweihu.pixabayapplication.bigPictureView

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.RectF
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.shiweihu.pixabayapplication.databinding.ImageViewBinding
import com.shiweihu.pixabayapplication.viewArgu.BigPictureArgu
import kotlin.math.sqrt

class BigPictureAdapter(val argu: BigPictureArgu,val fragment: Fragment,val touchCallBack:(touchState:Int)->Unit): RecyclerView.Adapter<BigPictureAdapter.ImageViewHolder>() {
    class ImageViewHolder(val binding:ImageViewBinding):RecyclerView.ViewHolder(binding.root)

    private var origMatrix:Matrix? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ImageViewBinding.inflate( LayoutInflater.from(parent.context),parent,false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.binding.imageUrl = argu.images?.get(position) ?:""
        holder.binding.priority = position == argu.currentIndex
        holder.binding.root.tag = position
        holder.binding.imageView.transitionName = "${BigPictureFragment.SHARE_ELEMENT_NAME}-${position}"
        holder.binding.doEnd = {
            if(position == argu.currentIndex) {
                fragment.startPostponedEnterTransition()
            }
        }


        val origFirstPoint = Point()
        val origSecondPoint = Point()

        var isMoving = false

        holder.binding.imageView.setOnTouchListener { v, event ->
            var x = event.x
            var y = event.y

            when(event.actionMasked){
                MotionEvent.ACTION_DOWN->{

                }
                MotionEvent.ACTION_POINTER_DOWN->{
                    origMatrix = holder.binding.imageView.imageMatrix
                    origFirstPoint.set(x.toInt(),y.toInt())
                    x = event.getX(1)
                    y = event.getY(1)
                    origSecondPoint.set(x.toInt(),y.toInt())
                }
                MotionEvent.ACTION_MOVE->{
                    if(event.pointerCount >= 2){
                        if(holder.binding.imageView.scaleType != ImageView.ScaleType.MATRIX){
                            holder.binding.imageView.scaleType = ImageView.ScaleType.MATRIX
                        }
                        isMoving = true
                        touchCallBack(MotionEvent.ACTION_MOVE)
                        val x1 = event.getX(1)
                        val y1 = event.getY(1)
                        val dis = getDistance(x.toInt(),y.toInt(),x1.toInt(),y1.toInt())
                        val lastdis = getDistance(origFirstPoint.x,origFirstPoint.y,origSecondPoint.x,origSecondPoint.y)
                        val scale = dis.toFloat() / lastdis.toFloat()
                        val matrix = Matrix()
                        matrix.set(holder.binding.imageView.imageMatrix)
                        matrix.postScale(scale,scale,(x+x1)/2,(y+y1)/2)
                        holder.binding.imageView.imageMatrix = matrix
                        origFirstPoint.set(x.toInt(),y.toInt())
                        origSecondPoint.set(x1.toInt(),y1.toInt())
                    }
                }
                MotionEvent.ACTION_CANCEL->{
                    if(isMoving) {
                        restoreCenter(holder.binding)
                        isMoving = false
                    }
                }
                MotionEvent.ACTION_UP->{
                    if(isMoving){
                        touchCallBack(MotionEvent.ACTION_UP)
                        restoreCenter(holder.binding)
                        isMoving = false
                    }

                }
            }


            return@setOnTouchListener true
        }
        holder.binding.executePendingBindings()
    }

    private fun getDistance(x1:Int,y1:Int,x2:Int,y2:Int):Int{
        return sqrt( (x1-x2)*(x1-x2).toDouble() + (y1-y2)*(y1-y2) ).toInt()
    }


    private fun restoreCenter(binding:ImageViewBinding){
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


    override fun getItemCount(): Int {
        return argu.images!!.size
    }
}