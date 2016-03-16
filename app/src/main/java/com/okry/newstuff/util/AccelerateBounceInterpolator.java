package com.okry.newstuff.util;

import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by hexiaogang on 3/14/16.
 */
public class AccelerateBounceInterpolator implements Interpolator {
    private DecelerateInterpolator mAccelerate;
    private BounceInterpolator mBounce;

    public AccelerateBounceInterpolator() {
        mAccelerate = new DecelerateInterpolator(0.9f);
        mBounce = new BounceInterpolator();
    }


    public float getInterpolation(float t) {
        return mBounce.getInterpolation(mAccelerate.getInterpolation(t));
    }
}
