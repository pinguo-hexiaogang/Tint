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
import android.widget.Scroller;

import com.okry.newstuff.util.Util;
import com.orhanobut.logger.Logger;

/**
 * Created by liubo on 3/31/16.
 */
public class GraduationSeekBar2 extends View implements GestureDetector.OnGestureListener {
    private static final int LINE_NUMS = 31;  //必须为单数,数字0的时候可以剧中
    private static final int COLOR = 0xffFFD700;
    private static final int SEL_COLOR = 0xffff0000;

    private Scroller mScroller;
    private GestureDetectorCompat mGestureDetectorCompat;   // 使用V4兼容包, 兼容低版本系统

    private int mTotalStep = 100;
    private int mAngel = 180;
    private Paint mPaint;
    private Path mCenterIndicatorPath = new Path();
    private double mSeekBarLen;   //弧形划块总长度
    private float mLineStrokeWidth;  //弧形上画笔大小
    private double mSpaceLen; //弧形上空白大小

    //中间的三角形大小,每条边的长度
    private int mCursorSize;
    private int mCursorColor = 0xffFFD700;
    private int mLineHeight;
    private int mCursorPadding;
    private int mVerticalPadding;
    private float mScrollDis = 0f;
    //向左可以滚动的最大位移
    private float mLeftTotalScroll = 0f;
    //向右可以滚动的最大位移
    private float mRightTotalScroll = 0f;
    private float[] mLineWidthAlphaCache = new float[2];
    private int mCurrentStep = 0;
    private int mStartStep = 0;
    private int mRightTotalStep = 0;
    private int mLeftTotalStep = 0;
    private boolean mFling = false;

    public interface OnSeekBarChangeListener {
        void onProgressChanged(int progress, int total);

        void onStartTrackingTouch(int progress, int total);

        void onStopTrackingTouch(int progress, int total);
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
        mScroller = new Scroller(getContext());
        mGestureDetectorCompat = new GestureDetectorCompat(getContext(), this);

        mLineStrokeWidth = Util.dpToPx(getContext(), 3);
        mCursorSize = Util.dpToPx(getContext(), 10);
        mLineHeight = Util.dpToPx(getContext(), 15);
        mCursorPadding = Util.dpToPx(getContext(), 1);
        mVerticalPadding = Util.dpToPx(getContext(), 6);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(COLOR);
    }

    public void setTotalStep(int totalStep) {
        this.mTotalStep = totalStep;
        updateRightLeftStep(mCurrentStep);
        calLeftRightTotalScroll(mStartStep, mTotalStep);
    }

    /**
     * @param startStep 以1开始计数
     */
    public void setStartStep(int startStep) {
        this.mCurrentStep = startStep;
        this.mStartStep = startStep;
        updateRightLeftStep(mCurrentStep);
        calLeftRightTotalScroll(mStartStep, mTotalStep);
    }

    /**
     * @param currentStep 以1开始计数
     */
    private void updateRightLeftStep(int currentStep) {
        mLeftTotalStep = currentStep - 1;
        mRightTotalStep = mTotalStep - currentStep;
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
        calLeftRightTotalScroll(mStartStep, mTotalStep);
    }


    private int getPerAngle() {
        int perAngle = 180 / (LINE_NUMS - 1);
        return perAngle;
    }

    private float getPerRadian() {
        float perRadian = (float) (Math.PI / (LINE_NUMS - 1));
        return perRadian;
    }

    private double getScrollRadianInPer(float scrollDis) {
        int angle = getScrollAngleInPer(scrollDis);
        double scrollRadian = Math.PI / 180 * angle;
        //Logger.d("scrollRadian:" + scrollRadian);
        return scrollRadian;
    }

    private double getScrollRadian(float scrollDis) {
        int angle = getScrollAngle(scrollDis);
        double scrollRadian = Math.PI / 180 * angle;
        Logger.d("scrollRadian:" + scrollRadian);
        return scrollRadian;
    }

    private int getScrollAngleInPer(float scrollDis) {
        int scrollRadian = (int) ((scrollDis * 1.0f / getWidth()) * 180);
        int angle = scrollRadian % getPerAngle();
        return angle;
    }

    private int getScrollAngle(float scrollDis) {
        int scrollRadian = (int) ((scrollDis * 1.0f / getWidth()) * 180);
        return scrollRadian;
    }

    private void updateCursorPath(int width, int height) {
        mCenterIndicatorPath.reset();
        mCenterIndicatorPath.moveTo(width / 2 - mCursorSize / 2, mCursorPadding);
        mCenterIndicatorPath.lineTo(width / 2 + mCursorSize / 2, mCursorPadding);
        mCenterIndicatorPath.lineTo(width / 2, mCursorPadding + (int) (0.7 * mCursorSize));
        mCenterIndicatorPath.close();

    }

    private void calLeftRightTotalScroll(int startStep, int totalStep) {
        int totalRightStep = totalStep - startStep;
        float totalRightRadian = totalRightStep * getPerRadian();
        mRightTotalScroll = (float) (totalRightRadian / Math.PI * getWidth());

        int totalLeftStep = startStep - 1;
        float totalLeftRadian = totalLeftStep * getPerRadian();
        mLeftTotalScroll = (float) (totalLeftRadian / Math.PI * getWidth());
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
        if (!mFling && event.getAction() == MotionEvent.ACTION_UP) {
            autoScroll();
        }
        return ret;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            Logger.d("scroller,x:" + mScroller.getCurrX());
            updateTotalScroll(mScrollDis - mScroller.getCurrX());
            invalidate();
        } else {
            if (mFling) {
                mFling = false;
                autoScroll();
            }
            Logger.d("scroller end");
        }
    }

    /**
     * 滚动到合适的刻度,使某个刻度正好对准三角形箭头
     */
    private void autoScroll() {
        float totalAngle = getScrollAngle(mScrollDis) * 1.0f / getPerAngle();
        int total = (int) Math.floor(totalAngle);
        if (totalAngle < 0) {
            if (Math.abs(total) > mTotalStep - mStartStep) {
                total = -(mTotalStep - mStartStep);
            }
        } else {
            if (total > (mStartStep - 1)) {
                total = mTotalStep - 1;
            }
        }
        float scrollToDis = (float) (total * getPerRadian() / Math.PI * getWidth());
        Logger.d("autoScroll,total:" + total);
        updateTotalScroll(mScrollDis - scrollToDis);
        invalidate();
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

        double scrollRadian = getScrollRadianInPer(mScrollDis);

        double perRadian = getPerRadian();
        int offsetStep = mCurrentStep - mStartStep;

        //画中间的线
        double cos = Math.cos(scrollRadian);
        float strokeWidth = (float) (cos * lineWidth);
        mPaint.setColor(COLOR);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAlpha((int) (255 * cos + 0.5f));
        float distance = (float) (Math.sin(scrollRadian) * getWidth() / 2);
        canvas.drawLine(halfOfWidth + distance, yTop, halfOfWidth + distance, yBottom, mPaint);
        //画右边的线
        for (int i = 1; i < len; i++) {
            if (i > mRightTotalStep) {
                break;
            }
            double angel = (lineWidth + mSpaceLen) * i / halfOfCicleLen * 180;
            double angelOfPi = angel * Math.PI / 180;
            float distanceRight = (float) (Math.sin(angelOfPi + scrollRadian) * getWidth() / 2);

            float centerXRight = halfOfWidth + lineWidth / 2 + distanceRight;
            if (offsetStep < 0 && Math.abs(offsetStep) >= i) {
                mPaint.setColor(SEL_COLOR);
            } else {
                mPaint.setColor(COLOR);
            }

            float[] widthAlphaRight = getStrokeWidthAndAlpha(angelOfPi, scrollRadian, true);
            mPaint.setAlpha((int) widthAlphaRight[1]);
            mPaint.setStrokeWidth(widthAlphaRight[0]);
            canvas.drawLine(centerXRight, yTop, centerXRight, yBottom, mPaint);
        }
        //画左边的线
        for (int i = 1; i < len; i++) {
            if (i > mLeftTotalStep) {
                break;
            }
            double angel = (lineWidth + mSpaceLen) * i / halfOfCicleLen * 180;
            double angelOfPi = angel * Math.PI / 180;
            float distanceLeft = (float) (Math.sin(angelOfPi - scrollRadian) * getWidth() / 2);

            float centerXLeft = halfOfWidth - lineWidth / 2 - distanceLeft;

            if (offsetStep > 0 && offsetStep >= i) {
                mPaint.setColor(SEL_COLOR);
            } else {
                mPaint.setColor(COLOR);
            }

            float[] widthAlphaLeft = getStrokeWidthAndAlpha(angelOfPi, scrollRadian, false);
            mPaint.setAlpha((int) widthAlphaLeft[1]);
            mPaint.setStrokeWidth(widthAlphaLeft[0]);
            canvas.drawLine(centerXLeft, yTop, centerXLeft, yBottom, mPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //三角形
        drawIndicator(canvas);
        drawLines(canvas);
    }


    @Override
    public boolean onDown(MotionEvent e) {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(mCurrentStep, mTotalStep);
        }
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
        Logger.d("scroll distanceX:" + distanceX);
        updateTotalScroll(distanceX);
        invalidate();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        //ignore
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (mScrollDis < 0) {
            if (Math.abs(mScrollDis) >= mRightTotalScroll) {
                return false;
            }
        } else if (mScrollDis > 0) {
            if (mScrollDis >= mLeftTotalScroll) {
                return false;
            }
        }
        mScroller.fling((int) (mScrollDis + 0.5f), 0, (int) velocityX, 0, (int) -mRightTotalScroll, (int) mLeftTotalScroll, 0, 0);
        mFling = true;
        invalidate();
        return true;
    }

    private void updateTotalScroll(float xDistance) {
        float scrollDis = mScrollDis - xDistance;
        int angle = getScrollAngle(scrollDis);
        float scrollStep = angle * 1.0f / getPerAngle();
        int currentStep = mStartStep - (int) (scrollStep);
        if (currentStep >= mTotalStep) {
            mScrollDis = -mRightTotalScroll;
            mCurrentStep = mTotalStep;
            updateRightLeftStep(mCurrentStep);
        } else if (currentStep <= 1) {
            mScrollDis = mLeftTotalScroll;
            mCurrentStep = 1;
            updateRightLeftStep(mCurrentStep);
        } else {
            Logger.d("updateTotalScroll" + ",preDis:" + mScrollDis + ",now Dis:" + scrollDis);
            mScrollDis = scrollDis;
            mCurrentStep = currentStep;
            updateRightLeftStep(mCurrentStep);
        }
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(mCurrentStep, mTotalStep);
        }
    }
}
