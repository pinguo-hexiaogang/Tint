package com.okry.newstuff.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.okry.newstuff.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AnimationTestActivity extends AppCompatActivity {

    @InjectView(R.id.view1)
    View mAnimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_test);
        ButterKnife.inject(this);
        ObjectAnimator animator = ObjectAnimator.ofInt(mAnimView, "backgroundColor", 0x00000000, 0xff000000);
        animator.setDuration(2000);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();

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
    }
}
