package com.okry.newstuff.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.okry.newstuff.R;

/**
 * Created by hexiaogang on 10/14/15.
 */
public class RotateScaleLineView extends View {
    private Matrix mMatrix = new Matrix();
    private float[] mDefaultLine;
    private float[] mTransformLine;
    private Paint mLinePaint;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mLineGapMin;
    private int mLineGapMax;
    //屏幕对角线长度
    private float mDiagonalLength;
    //两条线之间点额间隔
    private int mLineGap;

    private int mDefaultLineGap;

    public RotateScaleLineView(Context context) {
        super(context);
        init();
    }

    public RotateScaleLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RotateScaleLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RotateScaleLineView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        initDefaultLines();
        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(0xff000000);
    }

    private void initDefaultLines() {
        Activity activity = (Activity) getContext();
        mScreenWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
        mDiagonalLength = (float) Math.sqrt(mScreenWidth * mScreenWidth + mScreenHeight * mScreenHeight);

        mDefaultLineGap = getResources().getDimensionPixelSize(R.dimen.line_gap);
        mLineGapMax = mScreenHeight;
        mLineGapMin = mLineGap / 4;
        mTransformLine = new float[8];
    }

    private void updateDefaultLines(int lineGap) {
        float widthOffset = (mDiagonalLength - getMeasuredWidth()) / 2.0f;
        float line1X0 = -widthOffset;
        float line1Y0 = getMeasuredHeight() / 2.0f - lineGap / 2;
        float line1X1 = line1X0 + mDiagonalLength;
        float line1Y1 = line1Y0;

        float line2X0 = line1X0;
        float line2Y0 = line1Y0 + lineGap;
        float line2X1 = line1X1;
        float line2Y1 = line2Y0;
        mDefaultLine = new float[]{line1X0, line1Y0, line1X1, line1Y1,line2X0, line2Y0, line2X1, line2Y1};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updateDefaultLines(mDefaultLineGap);
    }

    /**
     * @param degrees the total degrees
     */
    public void setRotate(int degrees) {
        mMatrix.reset();
        mMatrix.setRotate(degrees, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        invalidate();
    }
    public void setScale(float scaleFactor){
        mLineGap = (int)(mDefaultLineGap * scaleFactor);
        mLineGap = Math.max(mLineGapMin,Math.min(mLineGap,mLineGapMax));
        updateDefaultLines(mLineGap);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mMatrix.mapPoints(mTransformLine, mDefaultLine);
        canvas.drawLines(mTransformLine, mLinePaint);
    }
}
