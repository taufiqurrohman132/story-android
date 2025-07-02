package com.example.instogramapplication

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.R as RMaterial
import com.example.instogramapplication.databinding.ViewEditTextWithErrorBinding
import com.google.android.material.shape.CornerFamily
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CustomInputWithManualError @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

//    private val binding: ViewEditTextWithErrorBinding =
//        ViewEditTextWithErrorBinding.inflate(LayoutInflater.from(context), this)

//    private val inputLayout: TextInputLayout
    private val editText: TextInputEditText
    private val errorTextView: TextView

    private var onTextChangedCallback: ((String) -> Unit)? = null

    init {
        orientation = VERTICAL

        val themeContext = ContextThemeWrapper(context, RMaterial.style.ThemeOverlay_Material3_TextInputEditText_OutlinedBox)
        // Inisialisasi TextInputLayout dan EditText
//        inputLayout = TextInputLayout(themeContext).apply {
//            layoutParams = LayoutParams(
//                LayoutParams.MATCH_PARENT,
//                LayoutParams.WRAP_CONTENT
//            )
//            hintTextColor = ColorStateList.valueOf(context.getColor(R.color.color_base))
//            boxStrokeColor = context.getColor(R.color.color_base)
//            boxStrokeErrorColor = ColorStateList.valueOf(Color.RED)
////            boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
//        }

        editText = TextInputEditText(themeContext).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            setTextColor(Color.BLACK)
            setEms(10)
        }
//        inputLayout.addView(editText)

        // Inisialisasi TextView untuk error manual
        errorTextView = TextView(themeContext).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            setTextColor(Color.RED)
            textSize = 12f
            visibility = View.GONE
            setPadding(0, 4, 0, 0)
        }

        // Tambahkan ke layout
        addView(editText)
        addView(errorTextView)

        // Tambahkan listener teks berubah
        editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChangedCallback?.invoke(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    fun setHint(hint: String){
        editText.hint = hint

    }

    fun inputType(type: Int){
        editText.inputType = type
    }
//
//    fun setCornerRadius(radius: Float){
//        val shapeAppearanceModel = editText.shapeAppearanceModel
//            .toBuilder()
//            .setTopLeftCorner(CornerFamily.ROUNDED, radius)
//            .setTopRightCorner(CornerFamily.ROUNDED, radius)
//            .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
//            .setBottomRightCorner(CornerFamily.ROUNDED, radius)
//            .build()
//        inputLayout.shapeAppearanceModel = shapeAppearanceModel
//    }

    fun getText(): String = editText.text?.toString() ?: ""

    fun setErrorMessage(message: String?){
        errorTextView.text = message
        errorTextView.visibility = if (message.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    fun setOnTextChangedListener(callBack: (String) -> Unit){
        onTextChangedCallback = callBack
    }

}