package com.example.instogramapplication.data.remote.model

import com.google.gson.annotations.SerializedName

data class ErrorRespons (
    @field:SerializedName("error")
    val error: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null
)
