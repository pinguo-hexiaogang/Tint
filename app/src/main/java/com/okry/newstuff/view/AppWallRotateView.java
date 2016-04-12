package com.okry.newstuff.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.okry.newstuff.R;
import com.okry.newstuff.util.Util;

import java.util.Random;

/**
 * Created by hexiaogang on 12/23/15.
 */
public class AppWallRotateView extends View {
    private final static long ANIM_PER_DURATION = 600L;
    //每个动画的间隔时间
    private final static long PER_ANIM_DELAY_TIME = 1000L;
    private Paint mBorderPaint1 = new Paint();

    private Paint mBorderPaint2 = new Paint();

    private float mBorder1StrokeWidth;
    private float mBorder2StrokeWidth;
    private float mRoundCornerRadius;
    private float mSpaceWidth;
    private float mBorder1Radius;
    private float mBorder2Radius;
    private RectF mDrawableRect = new RectF();

    //需要绘制的Bitmap
    private RoundedBitmapDrawable mCurrentDrawable = null;

    private Matrix mMatrix = null;
    private ValueAnimator mRotateAnimator = null;
    private Camera mCamera = new Camera();
    private Random mRandom;
    private long mAnimatorRepeatedTimes = 0;
    private long mCurrentAnimTime = 0;
    private int mCurrentDrawableIndex = 0;

    private static final int[] DEFAULT_DRAWABLE_IDS = {
            R.drawable.appwall_new_iocn1,
            R.drawable.appwall_new_iocn2,
            R.drawable.appwall_new_iocn3,
            R.drawable.appwall_new_iocn4,
            R.drawable.appwall_new_iocn5,
            R.drawable.appwall_new_iocn6,
            R.drawable.appwall_new_iocn7};

    public AppWallRotateView(Context context) {
        super(context);
        init();
    }

    public AppWallRotateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppWallRotateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AppWallRotateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        createAnimator();
        mRandom = new Random();
        mBorder1StrokeWidth = Util.dpToPixel(getContext(), 1.5f);
        mBorder2StrokeWidth = Util.dpToPixel(getContext(), 1.5f);
        mSpaceWidth = Util.dpToPixel(getContext(), 1f);
        mRoundCornerRadius = Util.dpToPixel(getContext(), 10f);

        mBorderPaint1.setColor(0x80ffffff);
        mBorderPaint1.setAntiAlias(true);
        mBorderPaint1.setStyle(Paint.Style.STROKE);
        mBorderPaint1.setStrokeWidth(mBorder1StrokeWidth);

        mBorderPaint2.setColor(0xffffffff);
        mBorderPaint2.setAntiAlias(true);
        mBorderPaint2.setStyle(Paint.Style.STROKE);
        mBorderPaint2.setStrokeWidth(mBorder2StrokeWidth);

        //默认绘制的drawable
        mCurrentDrawable = RoundedBitmapDrawableFactory.create(getResources(), Util.getBitmapByDrawable(getResources().getDrawable(DEFAULT_DRAWABLE_IDS[0])));
        //mCurrentDrawable = Util.getBitmapByDrawable(getResources().getDrawable(R.drawable.big_beauty));

        mMatrix = new Matrix();
    }

    private void createAnimator() {
        mRotateAnimator = ValueAnimator.ofInt(0, 90);
        mRotateAnimator.setDuration(ANIM_PER_DURATION);
        mRotateAnimator.setInterpolator(new LinearInterpolator());
        mRotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mRotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int rotate = (int) animation.getAnimatedValue();
                updateMatrixByRotate(rotate);
                invalidate();
            }
        });
        mRotateAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                mAnimatorRepeatedTimes++;
                if (mAnimatorRepeatedTimes % 2 != 0) {
                    //从垂直于屏幕的角度旋转出来
                    mRotateAnimator.setIntValues(-90, 0);
                    //每次图片垂直于屏幕的时候，随机下一张图片，这样才看不会在切换图片时闪烁
                    int nextIndex = mRandom.nextInt(DEFAULT_DRAWABLE_IDS.length);
                    mCurrentDrawable = RoundedBitmapDrawableFactory.create(getResources(), Util.getBitmapByDrawable(getResources().getDrawable(DEFAULT_DRAWABLE_IDS[nextIndex])));
                    mCurrentDrawable.setCornerRadius(mDrawableRect.width() / 2);
                } else {
                    //从平行于屏幕旋转到垂直于屏幕
                    mRotateAnimator.setIntValues(0, 90);
                    mCurrentAnimTime = mRotateAnimator.getCurrentPlayTime();
                    mRotateAnimator.cancel();
                    postDelayed(mRestartAnimationRunnable, PER_ANIM_DELAY_TIME);
                }
            }

        });

    }

    private Runnable mRestartAnimationRunnable = new Runnable() {
        @Override
        public void run() {
            mRotateAnimator.setCurrentPlayTime(mCurrentAnimTime);
            mRotateAnimator.start();
        }
    };

    private void updateMatrixByRotate(int rotate) {

        final float centerX = getWidth() / 2;
        final float centerY = getHeight() / 2;

        mMatrix.reset();
        mCamera.save();
       /* if (mReverse) {
            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
        } else {
            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
        }*/
        mCamera.rotateY(rotate);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        mMatrix.preTranslate(-centerX, -centerY);
        mMatrix.postTranslate(centerX, centerY);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBorder1Radius = (w - mBorder1StrokeWidth * 2) / 2;
        mBorder2Radius = (w - mBorder2StrokeWidth * 2 - mBorder1StrokeWidth * 2 - mSpaceWidth * 2) / 2;
        float drawableRadius = (w - mBorder1StrokeWidth * 2 - mBorder2StrokeWidth * 3 - mSpaceWidth * 2) * 1.0f / 2;
        float drawableRectWidth = 2f * drawableRadius;
        mDrawableRect.left = (w - drawableRectWidth) / 2;
        mDrawableRect.top = (w - drawableRectWidth) / 2;
        mDrawableRect.right = mDrawableRect.left + drawableRectWidth;
        mDrawableRect.bottom = mDrawableRect.top + drawableRectWidth;
        mCurrentDrawable.setCornerRadius(mDrawableRect.width() / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBorders(canvas);
        drawDrawable(canvas);
    }

    private void drawDrawable(Canvas canvas) {

        canvas.save();
        mCurrentDrawable.setBounds((int) mDrawableRect.left, (int) mDrawableRect.top, (int) mDrawableRect.right, (int) mDrawableRect.bottom);
        canvas.concat(mMatrix);
        mCurrentDrawable.draw(canvas);
        canvas.restore();
    }

    private void drawBorders(Canvas canvas) {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorder1Radius, mBorderPaint1);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorder2Radius, mBorderPaint2);
    }


    /**
     * 开始翻转动画
     */
    public void startAnim() {
        mRotateAnimator.cancel();
        removeCallbacks(mRestartAnimationRunnable);
        mRotateAnimator.start();
    }

    /**
     * 停止翻转动画
     */
    public void stopAnim() {
        mRotateAnimator.cancel();
        removeCallbacks(mRestartAnimationRunnable);
    }
}
