package com.okry.newstuff.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieImageAsset
import com.okry.newstuff.R
import com.okry.newstuff.view.HomeAdvDialog

import kotlinx.android.synthetic.main.activity_lottie.*
import kotlinx.android.synthetic.main.content_lottie.*

class LottieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lottie)
        setSupportActionBar(toolbar)
        showDialog.setOnClickListener({
            val dialog = UnityLoadingDialog(this)
            dialog.show()
        })

        showAdvDialog.setOnClickListener({
            val dialog = HomeAdvDialog(this, R.style.fullScreenDialog)
            dialog.show()
        })
    }

}
