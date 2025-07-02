package com.example.instogramapplication.utils

import com.example.instogramapplication.data.remote.model.ErrorRespons
import com.google.gson.Gson
import okhttp3.ResponseBody

object ApiUtils {
    fun parseError(errorBody: ResponseBody?): ErrorRespons? {
        return try {
            val gson = Gson()
            val jsonInString = errorBody?.charStream()
            gson.fromJson(jsonInString, ErrorRespons::class.java)
        }catch (e: Exception){
            null
        }
    }
}