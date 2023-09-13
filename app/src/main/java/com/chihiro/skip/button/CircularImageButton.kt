package com.chihiro.skip.button

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

class CircularImageButton(context: Context, attrs: AttributeSet) : AppCompatImageButton(context, attrs) {
    private val paint = Paint()

    init {
        setBackgroundColor(Color.TRANSPARENT)
        scaleType = ScaleType.CENTER_CROP
    }

    init {
        scaleType = ScaleType.CENTER_CROP
    }

    override fun onDraw(canvas: Canvas) {
        val radius = width.coerceAtMost(height) / 2.toFloat()
        canvas.drawCircle(width / 2.toFloat(), height / 2.toFloat(), radius, paint)
        super.onDraw(canvas)
    }



    override fun setImageBitmap(bm: Bitmap?) {
        val circularBitmap = createCircularBitmap(bm)
        super.setImageBitmap(circularBitmap)
    }

    private fun createCircularBitmap(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) return null
        val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        val paint = Paint()
        val rect = android.graphics.Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(bitmap.width / 2.toFloat(), bitmap.height / 2.toFloat(), bitmap.width / 2.toFloat(), paint)
        paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return outputBitmap
    }
}
