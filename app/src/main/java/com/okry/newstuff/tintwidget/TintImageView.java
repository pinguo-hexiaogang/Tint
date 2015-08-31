package com.okry.newstuff.tintwidget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.TintableBackgroundView;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by hexiaogang on 8/28/15.
 */
public class TintImageView extends ImageView implements TintableBackgroundView, IPgImageTint {
    private PgTintHelper mTintHelper;
    public TintImageView(Context context) {
        super(context);
        init(null);
    }

    public TintImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TintImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TintImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mTintHelper = new PgTintHelper(this);
        if (attrs != null) {
            mTintHelper.loadAttr(attrs);
        }
    }

    @Override
    public void setSupportBackgroundTintList(ColorStateList tint) {
        if (mTintHelper != null) {
            mTintHelper.setSupportBackgroundTintList(tint);
        }
    }

    @Nullable
    @Override
    public ColorStateList getSupportBackgroundTintList() {
        return mTintHelper != null ? mTintHelper.getSupportBackgroundTintList() : null;
    }

    @Override
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        if (mTintHelper != null) {
            mTintHelper.setSupportBackgroundTintMode(tintMode);
        }
    }

    @Nullable
    @Override
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        return mTintHelper != null ? mTintHelper.getSupportBackgroundTintMode() : null;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mTintHelper != null) {
            mTintHelper.applySupportBackgroundTint();
            mTintHelper.applyImageTint();
        }
    }

    @Override
    public void setPgImageTintList(ColorStateList tintList) {
        if (mTintHelper != null) {
            mTintHelper.setImageTintList(tintList);
        }
    }

    @Override
    public void setPgImageTintMode(PorterDuff.Mode mode) {
        if (mTintHelper != null) {
            mTintHelper.setImageTintMode(mode);
        }
    }

    @Override
    public ColorStateList getPgImageTintList() {
        if (mTintHelper != null) {
            return mTintHelper.getImageTintList();
        }
        return null;
    }

    @Override
    public PorterDuff.Mode getPgImageTintMode() {
        if (mTintHelper != null) {
            return mTintHelper.getImageTintMode();
        }
        return null;
    }
}
