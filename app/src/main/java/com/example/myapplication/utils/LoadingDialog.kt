package com.example.myapplication.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.R

class LoadingDialog(context: Context) {
    private val dialog: Dialog = Dialog(context)
    private var loadingText: TextView? = null
    private var loadingImage: ImageView? = null
    private var rotateAnimation: Animation? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null)
        loadingText = view.findViewById(R.id.tv_loading_text)
        loadingImage = view.findViewById(R.id.iv_loading_image)

        // Setup animation
        rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_animation)

        // Setup dialog
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun show(message: String = "Loading, please wait...") {
        loadingText?.text = message
        loadingImage?.startAnimation(rotateAnimation)
        dialog.show()
    }

    fun hide() {
        if (dialog.isShowing) {
            loadingImage?.clearAnimation()
            dialog.dismiss()
        }
    }

    fun setMessage(message: String) {
        loadingText?.text = message
    }

    fun isShowing(): Boolean = dialog.isShowing
}