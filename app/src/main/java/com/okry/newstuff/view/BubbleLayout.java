package com.okry.newstuff.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;

/**
 * Created by hexiaogang on 3/7/16.
 */
public class BubbleLayout extends RelativeLayout {
    //@InjectView(R.id.bubble_image_imv)
    ImageView mBubbleLargeImv;
    //@InjectView(R.id.bubble_small_imv)
    ImageView mBubbleSmallImv;
    //@InjectView(R.id.bubble_large_imv)
    ImageView mImageImv;

    public BubbleLayout(Context context) {
        super(context);
    }

    public BubbleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BubbleLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void setBubbleImage(String imageFilePath) {
        //TODO
    }

    public void setBubbleImage(Bitmap bitmap) {
        //TODO
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
