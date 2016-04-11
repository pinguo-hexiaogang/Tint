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
public class GraduationSeekBar extends View implements GestureDetector.OnGestureListener {
    private static final int LINE_NUMS = 19;  //必须为单数,数字0的时候可以剧中
    private static final int COLOR = 0xff999999;
    private static final int SEL_COLOR = 0xffffd04e;

    private Scroller mScroller;
    private GestureDetectorCompat mGestureDetectorCompat;   // 使用V4兼容包, 兼容低版本系统

    private int mTotalStep = 100;
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
    private int mVerticalTopPadding;
    private int mVerticalBottomPadding;
    private double mScrollDis = 0f;
    //向左可以滚动的最大位移
    private double mLeftTotalScroll = 0f;
    //向右可以滚动的最大位移
    private double mRightTotalScroll = 0f;
    private float[] mLineWidthAlphaCache = new float[2];
    private int mCurrentStep = 0;
    private int mStartStep = 0;
    private int mRightTotalStep = 0;
    private int mLeftTotalStep = 0;
    private boolean mFling = false;
    private boolean mInScroll = false;
    private double mScrollToDis = Double.MIN_VALUE;

    public interface OnSeekBarChangeListener {
        void onProgressChanged(int progress, int total);

        void onStartTrackingTouch(int progress, int total);

        void onStopTrackingTouch(int progress, int total);
    }

    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public GraduationSeekBar(Context context) {
        super(context);
        init();
    }

    public GraduationSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GraduationSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GraduationSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mScroller = new Scroller(getContext());
        mGestureDetectorCompat = new GestureDetectorCompat(getContext(), this);

        mLineStrokeWidth = Util.dpToPx(getContext(), 3);
        mCursorSize = Util.dpToPx(getContext(), 10);
        mLineHeight = Util.dpToPx(getContext(), 13);
        mCursorPadding = Util.dpToPx(getContext(), 1);
        mVerticalTopPadding = Util.dpToPx(getContext(), 13);
        mVerticalBottomPadding = Util.dpToPx(getContext(), 5);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(COLOR);
    }

    public void setTotalStep(int totalStep) {
        this.mTotalStep = totalStep;
        this.mStartStep = 1;
        this.mCurrentStep = 1;
        updateRightLeftStep(mCurrentStep);
        calLeftRightTotalScroll(mStartStep, mTotalStep);
        reset();
        invalidate();
    }

    /**
     * @param startStep 以1开始计数
     */
    public void setStartStep(int startStep) {
        if (startStep < 1) {
            throw new IllegalArgumentException("the start step mus >= 1");
        }
        this.mCurrentStep = startStep;
        this.mStartStep = startStep;
        updateRightLeftStep(mCurrentStep);
        calLeftRightTotalScroll(mStartStep, mTotalStep);
        reset();
        invalidate();
    }

    private void reset() {
        mFling = false;
        mInScroll = false;
        mScrollDis = 0f;
        mScrollToDis = Float.MIN_VALUE;
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
        int height = mCursorSize + mCursorPadding + mVerticalTopPadding + mVerticalBottomPadding + mLineHeight;
        setMeasuredDimension(getMeasuredWidth(), height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateCursorPath(w, h);
        mSeekBarLen = Math.PI * w * 180 / 360;
        mSpaceLen = (mSeekBarLen - LINE_NUMS * mLineStrokeWidth) / (LINE_NUMS - 1);
        calLeftRightTotalScroll(mStartStep, mTotalStep);
    }


    private double getPerRadian() {
        double perRadian = (float) (Math.PI / (LINE_NUMS - 1));
        return perRadian;
    }

    private double getScrollRadianInPer(double scrollDis) {
        double scrollRadian = (scrollDis * 1.0f / getWidth()) * Math.PI;
        double angle = scrollRadian % getPerRadian();
        return angle;
    }

    private double getScrollRadian(double scrollDis) {
        double angle = getScrollAngle(scrollDis);
        double scrollRadian = Math.PI / 180 * angle;
        Logger.d("scrollRadian:" + scrollRadian);
        return scrollRadian;
    }


    private double getScrollAngle(double scrollDis) {
        double scrollRadian = (scrollDis * 1.0f / getWidth()) * 180;
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
        double totalRightRadian = totalRightStep * getPerRadian();
        mRightTotalScroll = (totalRightRadian / Math.PI * getWidth());

        int totalLeftStep = startStep - 1;
        double totalLeftRadian = totalLeftStep * getPerRadian();
        mLeftTotalScroll = (totalLeftRadian / Math.PI * getWidth());
    }

    private void drawIndicator(Canvas canvas) {
        mPaint.setColor(SEL_COLOR);
        mPaint.setAlpha(255);
        canvas.drawPath(mCenterIndicatorPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = false;
        ret = mGestureDetectorCompat.onTouchEvent(event);
        if (!mFling && event.getAction() == MotionEvent.ACTION_UP) {
            autoScroll();
            return true;
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
            } else {
                if (mInScroll) {
                    updateTotalScroll(mScrollDis - mScrollToDis);
                    callScrollEnd();
                    mInScroll = false;
                    invalidate();
                }
            }
            Logger.d("scroller end");
        }
    }

    private void callScrollEnd() {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(mCurrentStep, mTotalStep);
        }
    }

    /**
     * 滚动到合适的刻度,使某个刻度正好对准三角形箭头
     */
    private void autoScroll() {
        double totalAngle = getScrollRadian(mScrollDis) * 1.0f / getPerRadian();
        int total = (int) Math.round(totalAngle);
        if (totalAngle < 0) {
            if (Math.abs(total) > mTotalStep - mStartStep) {
                total = -(mTotalStep - mStartStep);
            }
        } else {
            if (total > (mStartStep - 1)) {
                total = mStartStep - 1;
            }
        }
        //WHY?这里用getPerRadian算出来的值不准确
        double scrollToDis = (double) (total * getPerRadian() * 1.0f / Math.PI * getWidth());
        //float scrollToDis = (float) (total * getPerRadian() / Math.PI * getWidth());
        int dx = (int) (scrollToDis - mScrollDis);
        Logger.d("autoScroll,total:" + total + ",dx:" + dx + ",scrollToDis:" + scrollToDis);
        if (dx != 0) {
            mScroller.startScroll((int) mScrollDis, 0, dx, 0, 250);
            //scroller只有int,算出来的位置不是很精确,不能恰好对准箭头;所有把这个值保存下来
            //滚动完成之后再使用这个值更新位置.
            mInScroll = true;
            mScrollToDis = scrollToDis;
            //updateTotalScroll(mScrollDis - scrollToDis);
        } else {
            updateTotalScroll(mScrollDis - scrollToDis);
            callScrollEnd();
        }
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
        mLineWidthAlphaCache[1] = (int) (220 * cos);
        if (mLineWidthAlphaCache[1] < 10) {
            mLineWidthAlphaCache[1] = 10;
        }
        return mLineWidthAlphaCache;
    }

    private void drawLines(Canvas canvas) {
        float lineWidth = mLineStrokeWidth;
        float halfOfCicleLen = (float) (Math.PI * getWidth() / 2);

        int len = LINE_NUMS / 2;
        float halfOfWidth = getWidth() / 2.0f;

        int yTop = mCursorPadding + mVerticalTopPadding + (int) (mCursorSize * 0.7);
        int yBottom = yTop + mLineHeight;

        double scrollRadian = getScrollRadianInPer(mScrollDis);

        int offsetStep = mCurrentStep - mStartStep;

        //画中间的线
        double cos = Math.cos(scrollRadian);
        float strokeWidth = (float) (cos * lineWidth);
        mPaint.setColor(SEL_COLOR);
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
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(false);
        }
        mInScroll = false;
        mFling = false;
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
        Logger.d("scroll distanceX:" + distanceX * 0.7f);
        updateTotalScroll(distanceX * 0.7f);
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

    private void updateTotalScroll(double xDistance) {
        double scrollDis = mScrollDis - xDistance;
        double radian = getScrollRadian(scrollDis);
        double scrollStep = radian * 1.0f / getPerRadian();
        int currentStep = mStartStep - (int) ((scrollStep));
        //Logger.d("currentStep:" + currentStep + ",scrollStep:" + scrollStep + ",scrollDis:" + scrollDis + ",mScrollDis:" + mScrollDis + ",rightTotal:" + mRightTotalScroll);
        //解决startStep在首或者末的时候,轻轻拖动不滚动的问题
        if (mStartStep == mTotalStep && xDistance < 0 && scrollStep > 0f && scrollStep < 1f) {
            mScrollDis = scrollDis;
        } else if (mStartStep == 1 && xDistance > 0 && scrollStep > -1f && scrollStep < 0f) {
            mScrollDis = scrollDis;
        } else if (currentStep >= mTotalStep) {
            if (mStartStep == mTotalStep) {
                //解决起始值是total,滑动到倒数第二个就马上跳到最后一个的问题
                if (scrollStep < 0.2f) {
                    mScrollDis = -mRightTotalScroll;
                    mCurrentStep = mTotalStep;
                    updateRightLeftStep(mCurrentStep);
                } else {
                    mScrollDis = scrollDis;
                    mCurrentStep = currentStep;
                    updateRightLeftStep(mCurrentStep);
                }
            } else {
                mScrollDis = -mRightTotalScroll;
                mCurrentStep = mTotalStep;
                updateRightLeftStep(mCurrentStep);
            }

        } else if (currentStep <= 1 && scrollDis >= mLeftTotalScroll) {
            mScrollDis = mLeftTotalScroll;
            mCurrentStep = 1;
            updateRightLeftStep(mCurrentStep);
        } else {
            Logger.d("updateTotalScroll2" + ",preDis:" + mScrollDis + ",now Dis:" + scrollDis);
            mScrollDis = scrollDis;
            mCurrentStep = currentStep;
            updateRightLeftStep(mCurrentStep);
        }
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(mCurrentStep, mTotalStep);
        }
    }
}
