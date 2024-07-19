package com.chat.radar.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import com.airbnb.lottie.LottieDrawable
import com.android.chat_redar.R
import com.android.chat_redar.databinding.DialogHsuErrorBinding

class AlertErrorDialog(context: Context) : Dialog(context) {

    private lateinit var binding: DialogHsuErrorBinding

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
        binding = DialogHsuErrorBinding.inflate(layoutInflater)
        binding.btnAcept.setOnClickListener {
            dismiss()
        }
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

    fun messageCustomAction(title: String, content: String, iconJSON: Int, onAccept: ()-> Unit) {
        if (!isShowing) {
            super.show()
        }
        binding.textViewTilte.text = title
        binding.textViewContent.text = content
        binding.loader.setAnimation(R.raw.logout)
        binding.loader.repeatCount = LottieDrawable.INFINITE
        binding.loader.playAnimation()

        binding.btnAcept.setOnClickListener {
            dismiss()
            onAccept()
        }

        binding.backButton.setOnClickListener {
            dismiss()
        }
    }

    fun typeMessage(type: TypeErrorDialog, message: String? = "", title: String? = "") {
        if (!isShowing) {
            super.show()
        }
        binding.btnAcept.setOnClickListener {
            dismiss()
        }
        when (type) {
            TypeErrorDialog.WRONG -> {
                binding.textViewTilte.setText(R.string.something_wrong)
                binding.textViewContent.setText(R.string.something_wrong_content)
            }
            TypeErrorDialog.FAIL -> {
                binding.textViewTilte.setText(R.string.something_wrong)
                binding.textViewContent.text = context.getString(R.string.something_wrong_content)
            }
            TypeErrorDialog.CONNECTION -> {
                binding.textViewTilte.setText(R.string.something_wrong)
                binding.textViewContent.text = context.getString(R.string.something_wrong_content)
            }
            TypeErrorDialog.CUSTOM -> {
                if (title == "") {
                    binding.textViewTilte.setText(R.string.something_wrong)
                } else {
                    binding.textViewTilte.text = title
                }
                binding.textViewContent.text = message
            }
            else -> {}
        }
    }

    enum class TypeErrorDialog(val typeError: Int) {
        WRONG(0), FAIL(1), CONNECTION(2), CUSTOM(3)
    }
}