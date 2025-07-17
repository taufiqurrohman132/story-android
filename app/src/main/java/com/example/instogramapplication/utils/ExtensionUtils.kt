package com.example.instogramapplication.utils

import android.graphics.LinearGradient
import android.graphics.Shader
import android.widget.TextView

object ExtensionUtils {
    fun TextView.setGradientText(vararg colors: Int){
        val width = paint.measureText(text.toString())
        paint.shader = LinearGradient(
            0f, 0f, width, 0f,
            colors, null, Shader.TileMode.CLAMP
        )
    }
}