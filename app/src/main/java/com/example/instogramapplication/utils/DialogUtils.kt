package com.example.instogramapplication.utils

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import com.example.instogramapplication.R
import com.example.instogramapplication.utils.constants.DialogType
import com.saadahmedev.popupdialog.PopupDialog
import com.saadahmedev.popupdialog.listener.StandardDialogActionListener

object DialogUtils {
    fun showToast(message: String?, context: Context) {
        message?.let {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun confirmDialog(context: Context, heading: String, desc: String, negBtnText: String,posBtnText: String, listener: () -> Unit) {
        PopupDialog.getInstance(context)
            .standardDialogBuilder()
            .createIOSDialog()
            .setHeading(heading)
            .setDescription(desc)
            .setNegativeButtonText(negBtnText)
            .setPositiveButtonText(posBtnText)
            .setPositiveButtonTextColor(R.color.red)
            .build(object : StandardDialogActionListener {
                override fun onPositiveButtonClicked(dialog: Dialog) {
                    listener()
                    dialog.dismiss()
                }

                override fun onNegativeButtonClicked(dialog: Dialog) {
                    dialog.dismiss()
                }
            })
            .show()
    }

    fun stateDialog(
        context: Context,
        status: DialogType,
        title: String,
        description: String,
        textButton: String,
        onDismiss: (Dialog) -> Unit = {}
    ) {
        val builder = PopupDialog.getInstance(context)
            .statusDialogBuilder()

        val dialog = when (status) {
            DialogType.SUCCESS -> builder.createSuccessDialog()
            DialogType.WARNING -> builder.createWarningDialog()
            DialogType.ERROR -> builder.createErrorDialog()
        }

        dialog.apply {
            setHeading(title)
            setDescription(description)
            setActionButtonText(textButton)

            setCancelable(false)
            build { onDismiss(builder.dialog) }.show()
        }
    }
}