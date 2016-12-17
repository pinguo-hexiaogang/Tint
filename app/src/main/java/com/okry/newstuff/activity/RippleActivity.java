package com.okry.newstuff.activity;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.okry.newstuff.R;
import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RippleActivity extends ActionBarActivity {
    @InjectView(R.id.image_view)
    ImageView mImageView;
    @InjectView(R.id.ripple_image_view)
    View mRippleView;
    @InjectView(R.id.trans_layout)
    View mTransLayout;

    private int a = 0xffffffff;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ripple);
        ButterKnife.inject(this);
        //RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(getResources().getColor(R.color.light_common_high_light)),mImageView.getDrawable(),null);
        //mImageView.setImageDrawable(rippleDrawable);

        //mTransLayout.setTranslationX(500);
        Logger.d("a:" + a);
        int test = finallyTest();
        System.out.println("test is:" + test);
    }

    private int finallyTest() {
        int c = 1;
        try {
            c++;
            System.out.println("try执行中...");
            int a = 1/0;
            //return c + 100; //--------1
        } catch (Exception e) {
            System.out.println("exception执行中...");
            c = c + 200;
            return c;   //--------4
        } finally {
            c = c + 1000;
            System.out.println("finally执行中...");
            //return c;  //--------2
        }
        c = c+800;
        return c;   //---------3
    }
}
