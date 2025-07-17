package com.example.instogramapplication.utils

import android.util.Patterns

object ValidationUtils {

    fun isEmailValid(email: CharSequence?): Boolean {
        return if (!email.isNullOrBlank()) Patterns.EMAIL_ADDRESS.matcher(email).matches()
        else true
    }
}