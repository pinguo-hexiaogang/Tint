package com.okry.newstuff.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import com.okry.newstuff.R
import kotlinx.android.synthetic.main.activity_animation_test.*

class AnimationTestActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation_test)
      /*  val animator = ObjectAnimator.ofInt(view1, "backgroundColor", 0x00000000, -0x1000000)
        animator.duration = 2000
        animator.setEvaluator(ArgbEvaluator())
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.start()*/

        /*Integer from = 0x00000000;
        Integer to = 0xff000000;
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),from,to);*/
        /* ValueAnimator valueAnimator = ValueAnimator.ofArgb(0x00000000,0xff000000);
        valueAnimator.setDuration(1900);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimView.setBackgroundColor((int)animation.getAnimatedValue());
            }
        });
        valueAnimator.start();*/
        var count = 0
        changePictureAnimation.setOnClickListener({
            imageSwitcher.setImageResource(if (count % 2 == 0) R.drawable.eft_lightcolor_beauty else R.drawable.eft_skin_red)
            count++
        })

        imageSwitcher.inAnimation = AlphaAnimation(0f,1f)
        imageSwitcher.inAnimation.duration = 5000
        imageSwitcher.inAnimation.interpolator = LinearInterpolator()
        imageSwitcher.outAnimation = AlphaAnimation(1f,0f)
        imageSwitcher.outAnimation.duration = 5000
        imageSwitcher.outAnimation.interpolator = LinearInterpolator()
    }
}
