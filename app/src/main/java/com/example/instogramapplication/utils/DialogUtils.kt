package com.example.instogramapplication.utils

import android.content.Context
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtils {
    fun showConfirmationDialog(
        context: Context,
        titleRes: Int,
        messageRes: Int,
        positiveRes: Int,
        negativeRes: Int,
        onConfirmed: () -> Unit
    ){
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(titleRes))
            .setMessage(context.getString(messageRes))
            .setPositiveButton(context.getString(positiveRes)) { dialog, _ ->
                onConfirmed()
                dialog.dismiss()
            }
            .setNeutralButton(context.getString(negativeRes)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}