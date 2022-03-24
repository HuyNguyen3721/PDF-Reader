package com.ezstudio.pdfreaderver4.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.ezstudio.pdfreaderver4.databinding.LayoutDialogPasswordFileBinding

class DialogPasswordFile(
    context: Context,
    var binding: LayoutDialogPasswordFileBinding,
    style: Int
) : Dialog(context, style) {
    var listenerNo: (() -> Unit)? = null
    var listenerYes: (() -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setCancelable(true)
        initData()
        initView()
        initListener()
    }

    private fun initData() {

    }

    private fun initView() {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(binding.root)
    }

    private fun initListener() {
        binding.tvCancel.setOnClickListener {
            listenerNo?.invoke()
        }
        binding.tvOk.setOnClickListener {
            listenerYes?.invoke()
        }
    }
}