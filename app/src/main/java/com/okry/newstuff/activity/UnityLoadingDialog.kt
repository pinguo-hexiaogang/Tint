package com.okry.newstuff.activity

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowManager
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.okry.newstuff.R
import org.jetbrains.anko.find

/**
 * Created by hexiaogang on 2017/11/30.
 */
class UnityLoadingDialog(context: Context) : Dialog(context, R.style.fullScreenDialog) {
    private lateinit var lottieView: LottieAnimationView
    private lateinit var progressTv: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.unity_load_layout)
        lottieView = find<LottieAnimationView>(R.id.lottieView)
        lottieView.imageAssetsFolder = "/unity_load_anim"
        progressTv = find<TextView>(R.id.progressView)
    }

    fun setProgress(progress: Int) {
        progressTv.text = "" + progress + "%"
    }


}