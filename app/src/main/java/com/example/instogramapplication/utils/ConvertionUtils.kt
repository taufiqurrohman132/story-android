package com.example.instogramapplication.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

}