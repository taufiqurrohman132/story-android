package com.example.instogramapplication.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object ConvertionUtils {
    fun downloadImageToCache(context: Context, url: String, fileName: String): File? {
        return try {
            val file = File(context.cacheDir, fileName)
            if (!file.exists()) {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                file.outputStream().use { connection.inputStream.copyTo(it) }
            }
            file
        } catch (e: Exception) {
            null
        }
    }

    fun getBitmapFromCache(context: Context, fileName: String): Bitmap? {
        val file = File(context.cacheDir, fileName)
        return if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
    }

    fun vectorToBitmap(
        resources: Resources,
        @DrawableRes id: Int,
        @ColorInt color: Int? = null
    ): Bitmap {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
            ?: throw IllegalArgumentException("Resource not found")

        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        color?.let { DrawableCompat.setTint(vectorDrawable, it) }
        vectorDrawable.draw(canvas)
        return bitmap
    }

}