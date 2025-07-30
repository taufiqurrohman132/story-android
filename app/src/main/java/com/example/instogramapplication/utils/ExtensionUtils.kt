package com.example.instogramapplication.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.LinearGradient
import android.graphics.Rect
import android.graphics.Shader
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ExtensionUtils {
    private const val MAXIMAL_SIZE = 1000000

    fun TextView.setGradientText(vararg colors: Int){
        val width = paint.measureText(text.toString())
        paint.shader = LinearGradient(
            0f, 0f, width, 0f,
            colors, null, Shader.TileMode.CLAMP
        )
    }

    fun File.reduceFileImage(): File{
        val file = this
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    fun View.observeKeyboardVisibility(owner: LifecycleOwner,onKeyboardVisibilityChanged: (Boolean) -> Unit) {
        val rootView = this.rootView
        var isKeyboardVisible = false

        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)

            val screenHeight = rootView.height
            val keypadHeight = screenHeight - rect.bottom

            val visible = keypadHeight > screenHeight * 0.15 // 15% threshold
            if (visible != isKeyboardVisible) {
                isKeyboardVisible = visible
                onKeyboardVisibilityChanged(visible)
            }
        }

        viewTreeObserver.addOnGlobalLayoutListener(listener)

        owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                viewTreeObserver.removeOnGlobalLayoutListener(listener)
            }
        })
    }

}