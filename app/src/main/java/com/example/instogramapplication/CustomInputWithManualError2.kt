package com.example.instogramapplication

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CustomInputWithManualError2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputLayout(context, attrs) {

    private lateinit var errorMessage: String

    var isSucces: (CharSequence?) -> Boolean = { false }

    init {
        post {
            setValidationListener()
        }
        setErrorTextColor(ColorStateList.valueOf(Color.RED))
    }

    private fun setValidationListener() {
        editText?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                this@CustomInputWithManualError2.error = if (isSucces(s)) {
                    isErrorEnabled = false
                    null
                } else {
                    isErrorEnabled = true
                    errorMessage
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun setTextError(message: String){
        errorMessage = message
    }

}