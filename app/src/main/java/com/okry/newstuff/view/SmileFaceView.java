package com.okry.newstuff.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.okry.newstuff.R;

/**
 * Created by hexiaogang on 1/11/16.
 */
public class SmileFaceView extends View {
    //头平起的状态
    public final static int STATE_DOWN = 0x1;
    //抬头过程的动画
    public final static int STATE_MOVING_UP = 0x2;
    //头抬起的状态
    public final static int STATE_UP = 0x3;
    //低头过程的动画
    public final static int STATE_MOVING_DOWN = 0x4;

    private int mState = STATE_DOWN;

    public SmileFaceView(Context context) {
        super(context);
        init();
    }

    public SmileFaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmileFaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SmileFaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        changeAnimByState();
    }

    private void changeAnimByState() {
        AnimationDrawable animationDrawable = null;
        switch (mState) {
            case STATE_DOWN:
                setBackgroundResource(R.drawable.smile_face_down_anim);
                animationDrawable = (AnimationDrawable) getBackground();
                animationDrawable.start();
                break;
            case STATE_MOVING_UP:
                setBackgroundResource(R.drawable.smile_face_moving_up_anim);
                animationDrawable = (AnimationDrawable) getBackground();
                animationDrawable.setCallback(new AnimationDrawableCallback(animationDrawable, this) {
                    @Override
                    public void onAnimationComplete() {
                        mState = STATE_UP;
                        changeAnimByState();
                    }
                });
                animationDrawable.start();
                break;
            case STATE_UP:
                setBackgroundResource(R.drawable.smile_face_up_anim);
                animationDrawable = (AnimationDrawable) getBackground();
                animationDrawable.start();
                break;
            case STATE_MOVING_DOWN:
                setBackgroundResource(R.drawable.smile_face_moving_down_anim);
                animationDrawable = (AnimationDrawable) getBackground();
                animationDrawable.setCallback(new AnimationDrawableCallback(animationDrawable, this) {
                    @Override
                    public void onAnimationComplete() {
                        mState = STATE_DOWN;
                        changeAnimByState();
                    }
                });
                animationDrawable.start();
                break;
        }
    }

    /**
     * 切换动画
     *
     * @param state
     */
    public void setState(int state) {
        if (mState == state) {
            return;
        } else {
            if (state == STATE_MOVING_DOWN && mState == STATE_DOWN) {
                return;
            }
            if (state == STATE_MOVING_UP && mState == STATE_UP) {
                return;
            }
            this.mState = state;
            changeAnimByState();
        }
    }

    public int getState() {
        return mState;
    }
}
