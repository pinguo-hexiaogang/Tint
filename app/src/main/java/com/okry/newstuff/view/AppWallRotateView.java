package com.okry.newstuff.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.okry.newstuff.R;
import com.okry.newstuff.util.Util;

/**
 * Created by hexiaogang on 12/23/15.
 */
public class AppWallRotateView extends View {
    private Paint mBorderPaint1 = new Paint();

    private Paint mBorderPaint2 = new Paint();

    private float mBorder1StrokeWidth;
    private float mBorder2StrokeWidth;
    private float mSpaceWidth;
    private float mBorder1Radius;
    private float mBorder2Radius;
    private RectF mDrawableRect = new RectF();

    //需要绘制的Bitmap
    private Bitmap mCurrentBitmap = null;

    private Matrix mMatrix = null;

    private static final int[] DEFAULT_DRAWABLE_IDS = {
            R.drawable.appwall_new_iocn1,
            R.drawable.appwall_new_iocn2,
            R.drawable.appwall_new_iocn3,
            R.drawable.appwall_new_iocn4,
            R.drawable.appwall_new_iocn5,
            R.drawable.appwall_new_iocn6,
            R.drawable.appwall_new_iocn7,
            R.drawable.appwall_new_iocn8};

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
        mBorder1StrokeWidth = Util.dpToPixel(getContext(), 1.5f);
        mBorder2StrokeWidth = Util.dpToPixel(getContext(), 1.5f);
        mSpaceWidth = Util.dpToPixel(getContext(), 1f);

        mBorderPaint1.setColor(0x80ffffff);
        mBorderPaint1.setAntiAlias(true);
        mBorderPaint1.setStyle(Paint.Style.STROKE);
        mBorderPaint1.setStrokeWidth(mBorder1StrokeWidth);

        mBorderPaint2.setColor(0xffffffff);
        mBorderPaint2.setAntiAlias(true);
        mBorderPaint2.setStyle(Paint.Style.STROKE);
        mBorderPaint2.setStrokeWidth(mBorder2StrokeWidth);

        //默认绘制的drawable
        //mCurrentBitmap = Util.getBitmapByDrawable(getResources().getDrawable(DEFAULT_DRAWABLE_IDS[0]));
        mCurrentBitmap = Util.getBitmapByDrawable(getResources().getDrawable(R.drawable.big_beauty));

        mMatrix = new Matrix();
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
        float drawableRadius = (w - mBorder1StrokeWidth * 2 - mBorder2StrokeWidth * 2 - mSpaceWidth * 2) * 1.0f / 2;
        float drawableRectWidth = 1.4f * drawableRadius;
        mDrawableRect.left = (w - drawableRectWidth) / 2;
        mDrawableRect.top = (w - drawableRectWidth) / 2;
        mDrawableRect.right = mDrawableRect.left + drawableRectWidth;
        mDrawableRect.bottom = mDrawableRect.top + drawableRectWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBorders(canvas);
        drawDrawable(canvas);
    }

    private void drawDrawable(Canvas canvas) {
        RectF srcRect = new RectF(0, 0, mCurrentBitmap.getWidth(), mCurrentBitmap.getHeight());
        RectF dstRect = new RectF(0, 0, mDrawableRect.width(), mDrawableRect.height());

        canvas.save();
        canvas.translate((getWidth() - mDrawableRect.width()) / 2, (getHeight() - mDrawableRect.height()) / 2);
        mMatrix.setRectToRect(srcRect, dstRect, Matrix.ScaleToFit.FILL);
        mMatrix.postRotate(60, mDrawableRect.width() / 2, mDrawableRect.height() / 2);

        canvas.drawBitmap(mCurrentBitmap, mMatrix, null);
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
        //TODO
    }

    /**
     * 停止翻转动画
     */
    public void stopAnim() {
        //TODO
    }
}
