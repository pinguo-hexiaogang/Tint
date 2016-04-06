package com.okry.newstuff.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.okry.newstuff.util.Util;

/**
 * Created by hexiaogang on 4/5/16.
 */
public class GraduationSeekBar extends View {
    private float mMin = 0f;
    private float mMax = 100f;
    private int mTotalSteps = 100;
    private float mCurrentValue = 5f;
    //两条线之间间隔多少像素
    private int mIntervalDis;
    //线的高度
    private int mLineHeight;
    //线的宽度
    private int mLineStrokeWidth;
    //线的颜色
    private int mLineColor = 0xffffff00;
    //箭头的padding
    private int mCursorPadding;

    private int mVerticalPadding;
    //中间的三角形大小,每条边的长度
    private int mCursorSize;
    private int mCursorColor = 0xffFFD700;
    private int mCurrentStep = 0;

    private Paint mCursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    private Path mCenterIndicatorPath = new Path();

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
        mIntervalDis = Util.dpToPx(getContext(), 2);
        mCursorSize = Util.dpToPx(getContext(), 10);
        mLineHeight = Util.dpToPx(getContext(), 5);
        mCursorPadding = Util.dpToPx(getContext(), 1);
        mVerticalPadding = Util.dpToPx(getContext(), 3);
        mLineStrokeWidth = Util.dpToPx(getContext(), 2);

        mCursorPaint.setColor(mCursorColor);
        mCursorPaint.setStyle(Paint.Style.FILL);

        mLinePaint.setColor(mLineColor);

    }

    public void setRange(float min, float max) {
        this.mMin = min;
        this.mMax = max;
    }

    public void setTotalSteps(int totalSteps) {
        this.mTotalSteps = totalSteps;
        mCurrentStep = calStepByValue(mCurrentValue);
    }

    public void setValue(float value) {
        this.mCurrentValue = value;
        this.mCurrentStep = calStepByValue(value);
    }

    public void setStep(int step) {
        this.mCurrentStep = step;
    }

    private int calStepByValue(float value) {
        float total = mMax - mMin;
        return (int) ((value / total) * mTotalSteps);
    }

    private float calValueByStep(int step) {
        float total = mMax - mMin;
        return (step * 1.0f / mTotalSteps) * total;
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
    }

    private void updateCursorPath(int width, int height) {
        mCenterIndicatorPath.reset();
        mCenterIndicatorPath.moveTo(width / 2 - mCursorSize / 2, mCursorPadding);
        mCenterIndicatorPath.lineTo(width / 2 + mCursorSize / 2, mCursorPadding);
        mCenterIndicatorPath.lineTo(width / 2, mCursorPadding + (int) (0.7 * mCursorSize));
        mCenterIndicatorPath.close();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mCenterIndicatorPath, mCursorPaint);

    }
}
