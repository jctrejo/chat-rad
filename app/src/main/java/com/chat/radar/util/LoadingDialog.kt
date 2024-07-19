package com.chat.radar.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import com.android.chat_redar.R
import com.android.chat_redar.databinding.DialogHslLoadingBinding

class LoadingDialog(context: Context) : Dialog(context) {

    private lateinit var binding: DialogHslLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context.setTheme(R.style.hsl_dialog_theme)
        window?.let {
            it.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            it.setBackgroundDrawableResource(android.R.color.transparent)
        }
        binding = DialogHslLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(false)
    }

    override fun show() {
        if (!isShowing) {
            super.show()
        }
    }

    override fun dismiss() {
        if (isShowing) {
            super.dismiss()
        }
    }
}
