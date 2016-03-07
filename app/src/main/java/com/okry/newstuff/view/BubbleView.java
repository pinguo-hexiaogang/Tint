package com.okry.newstuff.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.okry.newstuff.R;
import com.okry.newstuff.util.Util;
import com.orhanobut.logger.Logger;

/**
 * Created by hexiaogang on 3/7/16.
 */
public class BubbleView extends View {
    private static final long ANIMATE_DURATION = 2000;
    private static final long ANIMATE_SCALE_DURATION = 2000;
    private Bitmap mBubbleImgBitmap = null;
    private Bitmap mBubbleLargeBitmap = null;
    private Bitmap mBubbleSmallBitmap = null;
    private int mBubbleLargeX = 0;
    private int mBubbleLargeY = 0;
    private float mBubbleScale = 0.0f;
    private Matrix mMatrix = new Matrix();
    private Matrix mImageMatrix = new Matrix();
    private Paint mBubblePaint;
    //气泡里面的额图片没有气泡高，被裁剪的时候最线面有一条横线（主要时因为decode的时候被系统放大了）
    private int mImagePadding;

    public BubbleView(Context context) {
        super(context);
        init();
    }

    public BubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        this.mBubbleLargeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bubble_large);
        this.mBubbleSmallBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bubble_small);
        Logger.d("large width:" + mBubbleLargeBitmap.getWidth() + ",height:" + mBubbleLargeBitmap.getHeight()
                + ",small width:" + mBubbleSmallBitmap.getWidth() + ",height:" + mBubbleSmallBitmap.getHeight());

        mBubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mImagePadding = Util.dpToPixel(getContext(), 1);
    }

    public void setBubbleImageBitmap(Bitmap bubbleImageBitmap) {
        mBubbleImgBitmap = bubbleImageBitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getBubbleWidth(), getBubbleHeight());
    }

    private int getBubbleWidth() {
        return mBubbleLargeBitmap.getWidth() + mBubbleSmallBitmap.getWidth() / 2 + getOvershootDis();
    }

    private int getBubbleHeight() {
        return mBubbleLargeBitmap.getHeight() + mBubbleSmallBitmap.getHeight() - mBubbleSmallBitmap.getHeight() / 5 + getOvershootDis();
    }

    public void setBubbleLargeX(int x) {
        this.mBubbleLargeX = x;
        invalidate();
    }

    public void setBubbleLargeY(int y) {
        this.mBubbleLargeY = y;
        invalidate();
    }

    public void setBubbleScale(float scale) {
        this.mBubbleScale = scale;
        invalidate();
    }

    private int getOvershootDis() {
        return mBubbleLargeBitmap.getWidth() / 4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBubbleImg(canvas);
        drawBubbleLarge(canvas);
        drawBubbleSmall(canvas);
    }

    private void drawBubbleLarge(Canvas canvas) {
        mMatrix.reset();
        mMatrix.postScale(mBubbleScale, mBubbleScale);
        mMatrix.postTranslate(mBubbleLargeX, mBubbleLargeY);
        canvas.drawBitmap(mBubbleLargeBitmap, mMatrix, mBubblePaint);
    }

    private void drawBubbleSmall(Canvas canvas) {
        int left = getWidth() - mBubbleSmallBitmap.getWidth();
        int top = getHeight() - mBubbleSmallBitmap.getHeight();
        canvas.drawBitmap(mBubbleSmallBitmap, left, top, mBubblePaint);
    }

    private void drawBubbleImg(Canvas canvas) {
        if (mBubbleImgBitmap == null) {
            return;
        }
        mImageMatrix.reset();
        mImageMatrix.postTranslate((mBubbleLargeBitmap.getWidth() - mBubbleImgBitmap.getWidth()) / 2,
                (mBubbleLargeBitmap.getHeight() - mBubbleImgBitmap.getHeight()) / 2 + mImagePadding);
        canvas.save();
        canvas.concat(mMatrix);
        Path path = new Path();
        path.addCircle(mBubbleLargeBitmap.getWidth() / 2, mBubbleLargeBitmap.getHeight() / 2, mBubbleLargeBitmap.getWidth() / 2, Path.Direction.CW);
        canvas.clipPath(path);
        canvas.drawBitmap(mBubbleImgBitmap, mImageMatrix, mBubblePaint);
        canvas.restore();
    }

    public void popBubble() {
        float startScale = 0.33f;

        ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(this, "bubbleScale", startScale, 1.0f);
        scaleAnimator.setDuration(ANIMATE_SCALE_DURATION);
        scaleAnimator.setInterpolator(new DecelerateInterpolator());

        int startX = getBubbleWidth() - mBubbleSmallBitmap.getWidth();
        ObjectAnimator transXAnimator = ObjectAnimator.ofInt(this, "bubbleLargeX", startX, getOvershootDis());
        transXAnimator.setDuration(ANIMATE_DURATION);
        transXAnimator.setInterpolator(new OvershootInterpolator(0.8f));

        int startY = getBubbleHeight() - mBubbleSmallBitmap.getHeight();
        ObjectAnimator transYAnimator = ObjectAnimator.ofInt(this, "bubbleLargeY", startY, getOvershootDis());
        transYAnimator.setDuration(ANIMATE_DURATION);
        transYAnimator.setInterpolator(new OvershootInterpolator(0.8f));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleAnimator, transXAnimator, transYAnimator);
        animatorSet.start();
    }

}
