package com.example.instogramapplication

import android.content.Context
import androidx.annotation.StringRes

interface ResourceProvider {
    fun getString(@StringRes resId: Int): String
}

class DefaultResourceProvider(private val context: Context) : ResourceProvider{
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}