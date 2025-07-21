package com.example.instogramapplication.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.LinearGradient
import android.graphics.Shader
import android.widget.TextView
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
}