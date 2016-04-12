package com.okry.newstuff.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.okry.newstuff.R;
import com.okry.newstuff.util.Util;

/**
 * Created by hexiaogang on 12/8/15.
 */
public class DownloadDrawable extends Drawable {
    public static final int STATUS_DOWNLOAD_WAIT = 0;
    public static final int STATUS_DOWNLOADING = 1;
    public static final int STATUS_DOWNLOAD_DONE = 2;
    private Paint mPaint = new Paint();
    private int mState = STATUS_DOWNLOAD_WAIT;
    private int mProgress = 0;
    private final Resources mRes;
    private final Drawable mWaitDrawable;
    private final Drawable mDownloadingDrawable;
    private final Drawable mDoneDrawable;
    private final int mBgColor = 0x66000000;
    private final int mProgressColor = 0xff44db5e;
    private int mProgressStrokeWidth;
    private int mSpaceWidth;

    public DownloadDrawable(Context context) {
        this.mRes = context.getResources();
        mPaint.setAntiAlias(true);

        this.mWaitDrawable = mRes.getDrawable(R.drawable.download_wait);
        this.mDownloadingDrawable = mRes.getDrawable(R.drawable.download_presses);
        this.mDoneDrawable = mRes.getDrawable(R.drawable.download_done);

        mProgressStrokeWidth = Util.dpToPixel(context, 1.5f);
        mSpaceWidth = Util.dpToPixel(context, 1.5f);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mState == STATUS_DOWNLOAD_WAIT) {
            drawCircleBg(canvas);
            drawDrawable(mWaitDrawable, canvas);
        } else if (mState == STATUS_DOWNLOADING) {
            drawCircleBg(canvas);
            drawDrawable(mDownloadingDrawable, canvas);
            drawProgress(canvas);
        } else if (mState == STATUS_DOWNLOAD_DONE) {
            drawCircleBg(canvas);
            drawDrawable(mDoneDrawable, canvas);
            drawProgress(canvas);
        }
    }

    private void drawProgress(Canvas canvas) {
        Rect bounds = getBounds();
        RectF rectF = new RectF(bounds);
        RectF progressRect = new RectF(rectF.left + mProgressStrokeWidth, rectF.top + mProgressStrokeWidth, rectF.right - mProgressStrokeWidth, rectF.bottom - mProgressStrokeWidth);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mProgressColor);
        mPaint.setStrokeWidth(mProgressStrokeWidth);

        canvas.drawArc(progressRect, 270f, getSweepAngle(), false, mPaint);
    }

    private float getSweepAngle() {
        return (mProgress * 1.0f / 100) * 360;
    }

    private void drawCircleBg(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBgColor);

        Rect bounds = getBounds();
        int bgRadius = (bounds.height() - 2 * mProgressStrokeWidth - 2 * mSpaceWidth) / 2;
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), bgRadius, mPaint);
    }

    private void drawDrawable(Drawable drawable, Canvas canvas) {
        int centerX = getBounds().centerX();
        int centerY = getBounds().centerY();
        drawable.setBounds(
                centerX - drawable.getIntrinsicWidth() / 2,
                centerY - drawable.getIntrinsicHeight() / 2,
                centerX + drawable.getIntrinsicWidth() / 2,
                centerY + drawable.getIntrinsicHeight() / 2
        );
        drawable.draw(canvas);
    }

    public void setState(int status) {
        this.mState = status;
        if (mState == STATUS_DOWNLOAD_DONE) {
            mProgress = 100;
        }
        invalidateSelf();
    }

    public void setProgress(int progress) {
        if (progress != 100) {
            this.mState = STATUS_DOWNLOADING;
        }
        this.mProgress = progress;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
