package com.okry.newstuff.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;

import com.okry.newstuff.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RippleActivity extends ActionBarActivity {
    @InjectView(R.id.image_view)
    ImageView mImageView;
    @InjectView(R.id.ripple_image_view)
    View mRippleView;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ripple);
        ButterKnife.inject(this);
        //RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(getResources().getColor(R.color.light_common_high_light)),mImageView.getDrawable(),null);
        //mImageView.setImageDrawable(rippleDrawable);
    }
}
