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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.okry.newstuff.R;
import com.orhanobut.logger.Logger;

/**
 * Created by hexiaogang on 3/7/16.
 */
public class BubbleView extends View {
    private static final long ANIMATE_DURATION = 1000;
    private Bitmap mBubbleImgBitmap = null;
    private Bitmap mBubbleLargeBitmap = null;
    private Bitmap mBubbleSmallBitmap = null;
    private int mBubbleLargeX = 0;
    private int mBubbleLargeY = 0;
    private float mBubbleScale = 0.0f;
    private Matrix mMatrix = new Matrix();
    private Matrix mImageMatrix = new Matrix();
    private Paint mBubblePaint;
    private Paint mBubbleImgPaint;
    private Paint mCirclePaint;

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

        mBubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBubbleImgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBubbleImgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(0xaaffffff);
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
        return mBubbleLargeBitmap.getWidth() + mBubbleSmallBitmap.getWidth() / 2;
    }

    private int getBubbleHeight() {
        return mBubbleLargeBitmap.getHeight() + mBubbleSmallBitmap.getHeight() / 2;
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
        Logger.d("setBubbleScale:" + scale);
        this.mBubbleScale = scale;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBubbleLarge(canvas);
        drawBubbleImg(canvas);
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
        //mImageMatrix.postConcat(mMatrix);
        mImageMatrix.postTranslate((mBubbleLargeBitmap.getWidth() - mBubbleImgBitmap.getWidth()) / 2,
                (mBubbleLargeBitmap.getHeight() - mBubbleImgBitmap.getHeight()) / 2);
        canvas.save();
        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null,
                Canvas.MATRIX_SAVE_FLAG |
                        Canvas.CLIP_SAVE_FLAG |
                        Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                        Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                        Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        canvas.concat(mMatrix);
        canvas.drawCircle(mBubbleLargeBitmap.getWidth() / 2, mBubbleLargeBitmap.getHeight() / 2, mBubbleLargeBitmap.getWidth() / 2, mCirclePaint);
        canvas.drawBitmap(mBubbleImgBitmap, mImageMatrix, mBubbleImgPaint);
        canvas.restoreToCount(sc);
    }

    public void popBubble() {
        float startScale = 0.2f;
        ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(this, "bubbleScale", startScale, 1.0f);
        scaleAnimator.setDuration(ANIMATE_DURATION);
        scaleAnimator.setInterpolator(new AccelerateInterpolator());

        int startX = getBubbleWidth() - mBubbleSmallBitmap.getWidth() / 2;
        ObjectAnimator transXAnimator = ObjectAnimator.ofInt(this, "bubbleLargeX", startX, 0);
        transXAnimator.setDuration(ANIMATE_DURATION);
        transXAnimator.setInterpolator(new AccelerateInterpolator());

        int startY = getBubbleHeight() - mBubbleSmallBitmap.getHeight() / 2;
        ObjectAnimator transYAnimator = ObjectAnimator.ofInt(this, "bubbleLargeY", startY, 0);
        transYAnimator.setDuration(ANIMATE_DURATION);
        transYAnimator.setInterpolator(new AccelerateInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleAnimator, transXAnimator, transYAnimator);
        animatorSet.start();
    }

}
