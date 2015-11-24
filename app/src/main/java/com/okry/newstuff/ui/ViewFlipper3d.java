package com.okry.newstuff.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ViewFlipper;

import com.okry.newstuff.util.Rotate3dAnimation;

/**
 * Created by hexiaogang on 11/18/15.
 */
public class ViewFlipper3d extends ViewFlipper {
    private Animation mInAnimation;
    private Animation mOutAnimation;
    private final static int ANIM_TIME = 600;

    public ViewFlipper3d(Context context) {
        super(context);
    }

    public ViewFlipper3d(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int childWidth = 0;
        int childHeight = 0;
        if (getChildCount() != 0) {
            childWidth = getChildAt(0).getMeasuredWidth();
            childHeight = getChildAt(0).getMeasuredHeight();
        } else {
            childWidth = w;
            childHeight = h;
        }
        mOutAnimation = new Rotate3dAnimation(0, 90, childWidth / 2, childHeight / 2, 0f, false);
        mOutAnimation.setDuration(ANIM_TIME);
        mOutAnimation.setInterpolator(new AccelerateInterpolator());

        mInAnimation = new Rotate3dAnimation(-90, 0, childWidth / 2, childHeight / 2, 0f, false);
        mInAnimation.setDuration(ANIM_TIME);
        mInAnimation.setStartOffset(ANIM_TIME);
        mInAnimation.setInterpolator(new AccelerateInterpolator());
        setInAnimation(mInAnimation);
        setOutAnimation(mOutAnimation);
    }
}
