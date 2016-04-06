package com.okry.newstuff.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import com.okry.newstuff.util.Util;
import com.orhanobut.logger.Logger;

/**
 * Created by liubo on 3/31/16.
 */
public class GraduationSeekBar2 extends View implements GestureDetector.OnGestureListener {
    private static final int LINE_NUMS = 31;  //必须为单数,数字0的时候可以剧中
    private static final int COLOR = 0xffFFD700;
    private static final int SEL_COLOR = 0xffff0000;

    private OverScroller mScroller;
    private GestureDetectorCompat mGestureDetectorCompat;   // 使用V4兼容包, 兼容低版本系统

    private int mTotalStep = 100;
    private float mDensityDpi = 1.0f;
    private int mAngel = 180;
    private Paint mPaint;
    private Path mCenterIndicatorPath = new Path();
    private boolean mIsSmartScroll;
    private double mSeekBarLen;   //弧形划块总长度
    private float mLineStrokeWidth;  //弧形上画笔大小
    private double mSpaceLen; //弧形上空白大小
    private double mSpaceOutSide;

    //中间的三角形大小,每条边的长度
    private int mCursorSize;
    private int mCursorColor = 0xffFFD700;
    private int mLineHeight;
    private int mCursorPadding;
    private int mVerticalPadding;
    private float mScrollDis = 0f;
    private float[] mLineWidthAlphaCache = new float[2];
    private int mStartStep = 0;
    private int mRightTotalStep = 0;
    private int mLeftTotalStep = 0;

    public interface OnSeekBarChangeListener {
        void onProgressChanged(int progress, int min, int max);

        void onStartTrackingTouch(int progress, int min, int max);

        void onStopTrackingTouch(int progress, int min, int max);
    }

    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public GraduationSeekBar2(Context context) {
        super(context);
        init();
    }

    public GraduationSeekBar2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GraduationSeekBar2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GraduationSeekBar2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mScroller = new OverScroller(getContext());
        mGestureDetectorCompat = new GestureDetectorCompat(getContext(), this);

        mLineStrokeWidth = Util.dpToPx(getContext(), 2);
        mCursorSize = Util.dpToPx(getContext(), 10);
        mLineHeight = Util.dpToPx(getContext(), 15);
        mCursorPadding = Util.dpToPx(getContext(), 1);
        mVerticalPadding = Util.dpToPx(getContext(), 6);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(COLOR);

        mIsSmartScroll = false;
    }

    public void setTotalStep(int totalStep) {
        this.mTotalStep = totalStep;
        updateRightLeftStep(mStartStep);
    }

    public void setStartStep(int startStep) {
        this.mStartStep = startStep;
        updateRightLeftStep(mStartStep);
    }

    /**
     * @param currentStep 以1开始计数
     */
    private void updateRightLeftStep(int currentStep) {
        mLeftTotalStep = currentStep - 1;
        mRightTotalStep = mTotalStep - mStartStep;
    }


    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = mCursorSize + mCursorPadding + 2 * mVerticalPadding + mLineHeight;
        setMeasuredDimension(getMeasuredWidth(), height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateCursorPath(w, h);
        mSeekBarLen = Math.PI * w * mAngel / 360;
        mSpaceLen = (mSeekBarLen - LINE_NUMS * mLineStrokeWidth) / (LINE_NUMS - 1);
    }


    private int getPerAngle() {
        int perAngle = 180 / (LINE_NUMS - 1);
        return perAngle;
    }

    private float getPerRadian() {
        float perRadian = (float) (Math.PI / (LINE_NUMS - 1));
        return perRadian;
    }

    private double getScrollRadian() {
        int angle = getScrollAngle();
        double scrollRadian = Math.PI / 180 * angle;
        Logger.d("scrollRadian:" + scrollRadian);
        return scrollRadian;
    }

    private int getScrollAngle() {
        int scrollRadian = (int) ((mScrollDis * 1.0f / getWidth()) * 180);
        int angle = scrollRadian % getPerAngle();
        return angle;
    }

    private void updateCursorPath(int width, int height) {
        mCenterIndicatorPath.reset();
        mCenterIndicatorPath.moveTo(width / 2 - mCursorSize / 2, mCursorPadding);
        mCenterIndicatorPath.lineTo(width / 2 + mCursorSize / 2, mCursorPadding);
        mCenterIndicatorPath.lineTo(width / 2, mCursorPadding + (int) (0.7 * mCursorSize));
        mCenterIndicatorPath.close();

    }

    private void drawIndicator(Canvas canvas) {
        mPaint.setColor(COLOR);
        mPaint.setAlpha(255);
        canvas.drawPath(mCenterIndicatorPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = false;
        ret = mGestureDetectorCompat.onTouchEvent(event);
        return ret;
    }

    private float[] getStrokeWidthAndAlpha(double radian, double scrollRadian, boolean isRight) {
        double cos = 0f;
        if (isRight) {
            cos = Math.cos(radian + scrollRadian);
        } else {
            cos = Math.cos(radian - scrollRadian);
        }
        float strokeWidth = (float) (cos * mLineStrokeWidth);
        mLineWidthAlphaCache[0] = strokeWidth;
        mLineWidthAlphaCache[1] = (int) (255 * cos + 0.5f);
        return mLineWidthAlphaCache;
    }

    private void drawLines(Canvas canvas) {
        float lineWidth = mLineStrokeWidth;
        float halfOfCicleLen = (float) (Math.PI * getWidth() / 2);

        int len = LINE_NUMS / 2;
        float halfOfWidth = getWidth() / 2.0f;

        int yTop = mCursorPadding + mVerticalPadding + (int) (mCursorSize * 0.7);
        int yBottom = yTop + mLineHeight;

        double scrollRadian = getScrollRadian();
        double perRadian = getPerRadian();

        for (int i = 0; i <= len; i++) {
            if (i == 0) {
                double cos = Math.cos(scrollRadian);
                float strokeWidth = (float) (cos * lineWidth);
                mPaint.setColor(COLOR);
                mPaint.setStrokeWidth(strokeWidth);
                mPaint.setAlpha((int) (255 * cos + 0.5f));
                float distance = (float) (Math.sin(scrollRadian) * getWidth() / 2);
                canvas.drawLine(halfOfWidth + distance, yTop, halfOfWidth + distance, yBottom, mPaint);
            } else {
                double angel = (lineWidth + mSpaceLen) * i / halfOfCicleLen * 180;
                double angelOfPi = angel * Math.PI / 180;
                float distanceRight = (float) (Math.sin(angelOfPi + getScrollRadian()) * getWidth() / 2);
                float distanceLeft = (float) (Math.sin(angelOfPi - getScrollRadian()) * getWidth() / 2);

                float centerXLeft = halfOfWidth - lineWidth / 2 - distanceLeft;
                float centerXRight = halfOfWidth + lineWidth / 2 + distanceRight;

                mPaint.setColor(COLOR);

                float[] widthAlphaRight = getStrokeWidthAndAlpha(angelOfPi, scrollRadian, true);
                mPaint.setAlpha((int) widthAlphaRight[1]);
                mPaint.setStrokeWidth(widthAlphaRight[0]);
                canvas.drawLine(centerXRight, yTop, centerXRight, yBottom, mPaint);


                float[] widthAlphaLeft = getStrokeWidthAndAlpha(angelOfPi, scrollRadian, false);
                mPaint.setAlpha((int) widthAlphaLeft[1]);
                mPaint.setStrokeWidth(widthAlphaLeft[0]);
                canvas.drawLine(centerXLeft, yTop, centerXLeft, yBottom, mPaint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //三角形
        drawIndicator(canvas);
        drawLines(canvas);



         /*   if (mValue == 0) {
                mPaint.setColor(COLOR);
                mPaint.setAlpha((int) (255 * cosArray[i] + 0.5f));
                canvas.drawLine(centerXRight, yTop, centerXRight, yBottom, mPaint);
                canvas.drawLine(centerXLeft, yTop, centerXLeft, yBottom, mPaint);
            } else {
                if (i > selIndex) {
                    mPaint.setColor(COLOR);
                    mPaint.setAlpha((int) (255 * cosArray[i] + 0.5f));
                    canvas.drawLine(centerXRight, yTop, centerXRight, yBottom, mPaint);
                    canvas.drawLine(centerXLeft, yTop, centerXLeft, yBottom, mPaint);
                } else {
                    mPaint.setColor(COLOR);
                    mPaint.setAlpha((int) (255 * cosArray[i] + 0.5f));
                    if (isRight) {
                        canvas.drawLine(centerXRight, yTop, centerXRight, yBottom, mPaint);
                        mPaint.setColor(SEL_COLOR);
                        mPaint.setAlpha((int) (255 * cosArray[i] + 0.5f));
                        canvas.drawLine(centerXLeft, yTop, centerXLeft, yBottom, mPaint);
                    } else {
                        canvas.drawLine(centerXLeft, yTop, centerXLeft, yBottom, mPaint);
                        mPaint.setColor(SEL_COLOR);
                        mPaint.setAlpha((int) (255 * cosArray[i] + 0.5f));
                        canvas.drawLine(centerXRight, yTop, centerXRight, yBottom, mPaint);
                    }
                }
            }*/
    }


    @Override
    public boolean onDown(MotionEvent e) {
       /* if (mOnSeekBarChangeListener == null) {
            return false;
        }
        mOnSeekBarChangeListener.onStartTrackingTouch(mValue, mMin, mMax);*/
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        //ignore
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //scrollBy((int) distanceX, 0);
        mScrollDis -= distanceX;
        invalidate();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        //ignore
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
