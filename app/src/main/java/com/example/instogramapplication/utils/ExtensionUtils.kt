package com.example.instogramapplication.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.LinearGradient
import android.graphics.Rect
import android.graphics.Shader
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


object ExtensionUtils {
    private const val MAXIMAL_SIZE = 1000000

    fun TextView.setGradientText(vararg colors: Int) {
        val width = paint.measureText(text.toString())
        paint.shader = LinearGradient(
            0f, 0f, width, 0f,
            colors, null, Shader.TileMode.CLAMP
        )
    }

    fun File.reduceFileImage(): File {
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

    fun View.keyboardVisibilityFlow(): Flow<Boolean> = callbackFlow {
        val rootView = this@keyboardVisibilityFlow.rootView
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootView.height
            val keypadHeight = screenHeight - r.bottom
            trySend(keypadHeight > screenHeight * 0.15)
        }

        rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)
        awaitClose {
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }


    @SuppressLint("CheckResult")
    fun ImageView.loadUrl(
        url: String?,
        overrideSize: Int? = null,
        cacheStrategy: DiskCacheStrategy = DiskCacheStrategy.ALL
    ) {
        Glide.with(this.context)
            .load(url)
            .apply {
                overrideSize?.let { override(it) }
                diskCacheStrategy(cacheStrategy)
            }
            .into(this)
    }



}