package com.okry.newstuff.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
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

    private int mMin = -100;
    private int mMax = 100;
    private int mStep = 1;
    private float[] cosArray = null;
    private float mDensityDpi = 1.0f;
    private int mAngel = 180;
    private Paint mPaint;
    private Path mCenterIndicatorPath = new Path();
    private int mValue = -50;
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

    /**
     * [90,270] 建议值180、360
     */
    public void setRoundAngel(int angel) {
        mAngel = angel;
    }

    public void setRange(int min, int max) {
        mMin = min;
        mMax = max;
    }

    public void setStep(int step) {
        mStep = step;
    }

    public void setValue(int value) {
        mValue = value;
        invalidate();
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
        updateAngelCosArray();
    }

    private void updateAngelCosArray() {
        double scrollRadian = getScrollRadian();
        int arrayLen = LINE_NUMS / 2 + 1;
        cosArray = new float[arrayLen];
        double perAngel = Math.PI / 2 / (arrayLen - 1);

        for (int i = 0; i < arrayLen; i++) {
            double angelOfPi = perAngel * i + scrollRadian;
            double cos = Math.cos(angelOfPi);
            cosArray[i] = (float) cos;
        }
    }

    private double getScrollRadian() {
        double scrollRadian = (mScrollDis * 1.0f / getWidth()) * Math.PI;
        Logger.d("scrollRadian:" + scrollRadian);
        return scrollRadian;
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
        canvas.drawPath(mCenterIndicatorPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = false;
        ret = mGestureDetectorCompat.onTouchEvent(event);
        return ret;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //三角形
        drawIndicator(canvas);

        float lineWidth = mLineStrokeWidth;
        float halfOfCicleLen = (float) (Math.PI * getWidth() / 2);

        int len = LINE_NUMS / 2;
        float halfOfWidth = getWidth() / 2.0f;
        mPaint.setStrokeWidth(lineWidth);

        int yTop = mCursorPadding + mVerticalPadding + (int) (mCursorSize * 0.7);
        int yBottom = yTop + mLineHeight;

        canvas.drawLine(halfOfWidth, yTop, halfOfWidth, yBottom, mPaint);

        int selIndex = mValue * mAngel / (mMax - mMin) * LINE_NUMS / 180;
        boolean isRight = selIndex > 0;
        if (!isRight) {
            selIndex = 0 - selIndex;
        }
        Log.e("Frisky", "indexFromMin:" + selIndex);
        for (int i = 1; i <= len; i++) {
            float strokeWidth = cosArray[i] * lineWidth;
            if (strokeWidth < 0.8f) {
                continue;
            }

            if (strokeWidth <= 1.0f) {
                strokeWidth = 1.0f;
            }

            mPaint.setStrokeWidth(strokeWidth);
            double angel = (lineWidth + mSpaceLen) * i / halfOfCicleLen * 180;
            double angelOfPi = angel * Math.PI / 180;
            float distance = (float) (Math.sin(angelOfPi + getScrollRadian()) * getWidth() / 2);

            float centerXLeft = halfOfWidth - lineWidth / 2 - distance;
            float centerXRight = halfOfWidth + lineWidth / 2 + distance;

            mPaint.setColor(COLOR);
            mPaint.setAlpha((int) (255 * cosArray[i] + 0.5f));
            canvas.drawLine(centerXRight, yTop, centerXRight, yBottom, mPaint);
            canvas.drawLine(centerXLeft, yTop, centerXLeft, yBottom, mPaint);

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
        updateAngelCosArray();
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
