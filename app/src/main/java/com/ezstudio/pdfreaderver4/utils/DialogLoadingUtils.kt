package com.ezstudio.pdfreaderver4.utils

import android.content.Context
import android.view.Window
import com.ezstudio.pdfreaderver4.dialog.DialogLoading

object DialogLoadingUtils {
    private var dialogHiding: DialogLoading? = null

    fun showDialogHiding(context: Context, isShowing: Boolean) {
        if (isShowing) {
            dialogHiding = DialogLoading(context)
            dialogHiding?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogHiding?.show()
        } else {
            dialogHiding?.dismiss()
        }
    }

}