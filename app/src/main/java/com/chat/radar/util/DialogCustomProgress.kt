package com.chat.radar.util

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.android.chat_redar.R

class DialogCustomProgress(private val activity: Activity) : Dialog(activity) {
    private var ll_progress_dialog: LinearLayout? = null
    private var progressBar2: ProgressBar? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        setContentView(R.layout.custom_progress_dialog_style)
        initUI()
    }

    private fun initUI() {
        ll_progress_dialog = findViewById(R.id.ll_progress_dialog) as LinearLayout?
        progressBar2 = findViewById(R.id.progressBar2) as ProgressBar?
        progressBar2?.indeterminateDrawable?.setColorFilter(
            activity.resources.getColor(R.color.purple_700),
            PorterDuff.Mode.MULTIPLY
        )
    }

    fun showProgressDialog() {
        if (this != null) {
            if (this.isShowing()) {
                return
            }
        }
    }

    fun dismissProgressDialog() {
        dismiss()
    }
}