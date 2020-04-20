package com.example.assignment.Common

import android.content.Context
import android.content.LocusId
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.GestureDetector
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.Resource
import com.example.assignment.Callback.IMyButtonCallback

abstract class MySwipeHelper(context:Context,
                             private val recyclerView: RecyclerView,
                             internal val buttonWidth:Int):ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT)
{
    private var buttonList : MutableList<MyButton>?=null
    private val gestureDetector:GestureDetector
    private var swipePosition

    inner class MyButton(private val context:Context,
    private val text:String,
    private val textSize:Int,
    private val imageResId: Int,
    private val color:Int,
    private val  listener:IMyButtonCallback) {
        private var pos: Int = 0
        private var clickRegion: RectF? = null
        private val resources: Resources

        init {
                resources = context.resources
        }
        fun onClick(x:Float,y:Float):Boolean{
            if(clickRegion!=null && clickRegion!!.contains(x,y))
            {
                listener.onClick(pos)
                return true
            }
            return false

        }

        fun onDraw(c: Canvas, rectF: RectF, pos:Int){
            val p = Paint()
            p.color = color
            c.drawRect(rectF,p)

            p.color =Color.WHITE
            p.textSize = textSize.toFloat()

            val r = Rect()
            val cHeight = rectF.height()
            val cWidth = rectF.width()
             p.textAlign = Paint.Align.LEFT
            p.getTextBounds(text,0,text.length,r)
            var x=0f
            var y=0f
            if(imageResId == 0) //Test
            {
                x = cWidth/2f - r.width()/2f - r.left.toFloat()
                y = cWidth/2f+r.height()/2f - r.bottom
                c.drawText(text,rectF.left+x,rectF.top+y,p)

            }else{
                val d = ContextCompat.getDrawable(context,imageResId)
                val bitmap = drawableToBitmap(d)
                c.drawBitmap(bitmap,(rectF.left + rectF.right)/2,(rectF.top+rectF.bottom)/2,p)
            }
            clickRegion = rectF
            this.pos = pos

        }
    }

    private fun drawableToBitmap(d: Drawable?): Bitmap {
     if(d!! is BitmapDrawable)
         return d.toBitmap()
        val bitmap = Bitmap.createBitmap(d!!.intrinsicWidth,d.intrinsicHeight,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        d.setBounds(0,0,canvas.width,canvas.height)
        d.draw(canvas)
        return bitmap

    }
}