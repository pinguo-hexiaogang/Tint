package com.okry.newstuff.tintwidget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v4.view.TintableBackgroundView;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by hexiaogang on 8/28/15.
 */
public class TintButton extends Button implements TintableBackgroundView {
    private PgTintHelper mTintHelper;

    public TintButton(Context context) {
        super(context);
        init(null);
    }

    public TintButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TintButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mTintHelper = new PgTintHelper(this);
        if (attrs != null) {
            mTintHelper.loadAttr(attrs);
        }
    }

    @Override
    public void setSupportBackgroundTintList(@Nullable ColorStateList tint) {
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
        }
    }
}
