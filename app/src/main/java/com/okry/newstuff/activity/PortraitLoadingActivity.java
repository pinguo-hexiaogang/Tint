package com.okry.newstuff.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.okry.newstuff.R;
import com.okry.newstuff.util.FasterAnimationsContainer;
import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PortraitLoadingActivity extends AppCompatActivity {

    @InjectView(R.id.flipper)
    ViewFlipper mFlipper;
    @InjectView(R.id.imv)
    ImageView mImageView;


    private static final int[] FRAMES = {
            R.drawable.bing_00,
            R.drawable.bing_01,
            R.drawable.bing_01,
            R.drawable.bing_02,
            R.drawable.bing_03,
            R.drawable.bing_04,
            R.drawable.bing_05,
            R.drawable.bing_06,
            R.drawable.bing_07,
            R.drawable.bing_08,
            R.drawable.bing_09,
            R.drawable.bing_10,
            R.drawable.bing_11,
            R.drawable.bing_12,
            R.drawable.bing_13,
            R.drawable.bing_14,
            R.drawable.bing_15,
            R.drawable.bing_16,
            R.drawable.bing_17,
            R.drawable.bing_18,
            R.drawable.bing_19,
            R.drawable.bing_20,
            R.drawable.bing_21,
            R.drawable.bing_22,
            R.drawable.bing_23,
            R.drawable.bing_24,
            R.drawable.bing_25,
            R.drawable.bing_26,
            R.drawable.bing_27,
            R.drawable.bing_28,
            R.drawable.bing_29,
            R.drawable.bing_30,
            R.drawable.bing_31,
            R.drawable.bing_32,
            R.drawable.bing_33,
            R.drawable.bing_34,
            R.drawable.bing_35,
            R.drawable.bing_36,
            R.drawable.bing_37,
            R.drawable.bing_38,
            R.drawable.bing_39,
            R.drawable.bing_40,
            R.drawable.bing_41,
            R.drawable.bing_42,
            R.drawable.bing_43,
            R.drawable.bing_44,
            R.drawable.bing_45,
            R.drawable.bing_46,
            R.drawable.bing_47,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portrait_loading);
        ButterKnife.inject(this);
        mFlipper.startFlipping();
        FasterAnimationsContainer fasterAnimationsContainer = new FasterAnimationsContainer(mImageView, true);
        fasterAnimationsContainer.addAllFrames(FRAMES, 20);
        fasterAnimationsContainer.setOnAnimationFrameChangedListener(new FasterAnimationsContainer.OnAnimationFrameChangedListener() {
            @Override
            public void onAnimationFrameChanged(int index) {
                Logger.d("the frame is:" + index);
            }
        });
        fasterAnimationsContainer.setOnAnimationStoppedListener(new FasterAnimationsContainer.OnAnimationStoppedListener() {
            @Override
            public void onAnimationStopped() {
                Logger.d("animation end");
                mImageView.setImageBitmap(null);
            }
        });
        fasterAnimationsContainer.start();
    }
}
