package com.okry.newstuff.tintwidget;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import com.okry.newstuff.R;

/**
 * Created by hexiaogang on 8/31/15.
 */
public class PgTintHelper {
    private final View mView;
    private TintInfo mBackgroundTint;
    private TintInfo mImageTint;
    private static final PorterDuff.Mode DEFAULT_MODE = PorterDuff.Mode.SRC_IN;
    //本来想用TintManager的cache来管理colorFilters，但是其方法都是private的。只有自己做了,有个不好的地方就是有两套cache
    private static final SparseArray<PorterDuffColorFilter> S_TINT_CACHE = new SparseArray<>();

    public PgTintHelper(View view) {
        this.mView = view;
    }

    public void loadAttr(AttributeSet attrs) {
        TypedArray a = mView.getContext().obtainStyledAttributes(attrs,
                R.styleable.Tint, 0, 0);
        try {

            if (a.hasValue(R.styleable.Tint_PgBackgroundTint)) {
                setSupportBackgroundTintList(a.getColorStateList(R.styleable.Tint_PgBackgroundTint));
            }
            if (a.hasValue(R.styleable.Tint_PgBackgroundTintMode)) {
                setSupportBackgroundTintMode(parseTintMode(
                        a.getInt(R.styleable.Tint_PgBackgroundTintMode, -1),
                        null));
            }
            if (a.hasValue(R.styleable.Tint_PgImageTint)) {
                setImageTintList(a.getColorStateList(R.styleable.Tint_PgImageTint));
            }
            if (a.hasValue(R.styleable.Tint_PgImageTintMode)) {
                setImageTintMode(parseTintMode(
                        a.getInt(R.styleable.Tint_PgImageTintMode, -1),
                        null));
            }
        } finally {
            a.recycle();
        }
    }

    public void setSupportBackgroundTintMode(PorterDuff.Mode mode) {
        if (mBackgroundTint == null) {
            mBackgroundTint = new TintInfo();
        }
        mBackgroundTint.mHasTintMode = true;
        mBackgroundTint.mTintMode = mode;
        applySupportBackgroundTint();
    }

    public void setSupportBackgroundTintList(ColorStateList tintList) {
        if (mBackgroundTint == null) {
            mBackgroundTint = new TintInfo();
        }
        mBackgroundTint.mTintList = tintList;
        mBackgroundTint.mHasTintList = true;

        applySupportBackgroundTint();
    }

    public void setImageTintMode(PorterDuff.Mode mode) {
        if (mImageTint == null) {
            mImageTint = new TintInfo();
        }
        mImageTint.mHasTintMode = true;
        mImageTint.mTintMode = mode;
        applyImageTint();
    }

    public void setImageTintList(ColorStateList tintList) {
        if (mImageTint == null) {
            mImageTint = new TintInfo();
        }
        mImageTint.mTintList = tintList;
        mImageTint.mHasTintList = true;

        applyImageTint();
    }

    public void applyImageTint() {
        if (mImageTint != null && mView instanceof ImageView) {
            Drawable drawable = ((ImageView) mView).getDrawable();
            if (drawable != null) {
                applyTint(drawable, mImageTint);
            }
        }
    }

    private void applyTint(Drawable drawable, TintInfo tintInfo) {
        if (drawable == null || tintInfo == null) {
            return;
        }
        if (tintInfo.mHasTintList || tintInfo.mHasTintMode) {
            PorterDuffColorFilter filter = getTintFilter(
                    tintInfo.mHasTintList ? tintInfo.mTintList : null,
                    tintInfo.mHasTintMode ? tintInfo.mTintMode : DEFAULT_MODE,
                    mView.getDrawableState());
            //TODO 这里每个状态都会设置一次，导致invalide，效率不高
            drawable.setColorFilter(filter);
        } else {
            drawable.clearColorFilter();
        }

        if (Build.VERSION.SDK_INT <= 10) {
            // On Gingerbread, GradientDrawable does not invalidate itself when it's ColorFilter
            // has changed, so we need to force an invalidation
            mView.invalidate();
        }
    }

    private static int generateCacheKey(int color, PorterDuff.Mode mode) {
        int hashCode = 1;
        hashCode = 31 * hashCode + color;
        hashCode = 31 * hashCode + mode.hashCode();
        return hashCode;
    }

    private PorterDuffColorFilter getTintFilter(ColorStateList tint,
                                                PorterDuff.Mode tintMode, final int[] state) {
        if (tint == null || tintMode == null) {
            return null;
        }
        final int color = tint.getColorForState(state, Color.TRANSPARENT);
        if (color == Color.TRANSPARENT) {
            return null;
        }
        int key = generateCacheKey(color, tintMode);
        PorterDuffColorFilter filter = S_TINT_CACHE.get(key);
        if (filter == null) {
            filter = new PorterDuffColorFilter(color, tintMode);
            S_TINT_CACHE.put(key, filter);
        }
        return filter;
    }

    public void applySupportBackgroundTint() {
        if (mView.getBackground() != null && mBackgroundTint != null) {
            applyTint(mView.getBackground(), mBackgroundTint);
        }
    }

    public ColorStateList getSupportBackgroundTintList() {
        return mBackgroundTint != null ? mBackgroundTint.mTintList : null;
    }

    public PorterDuff.Mode getSupportBackgroundTintMode() {
        return mBackgroundTint != null ? mBackgroundTint.mTintMode : null;
    }

    public ColorStateList getImageTintList() {
        return mImageTint != null ? mImageTint.mTintList : null;
    }

    public PorterDuff.Mode getImageTintMode() {
        return mImageTint != null ? mImageTint.mTintMode : null;
    }

    private PorterDuff.Mode parseTintMode(int value, PorterDuff.Mode defaultMode) {
        switch (value) {
            case 3:
                return PorterDuff.Mode.SRC_OVER;
            case 5:
                return PorterDuff.Mode.SRC_IN;
            case 9:
                return PorterDuff.Mode.SRC_ATOP;
            case 14:
                return PorterDuff.Mode.MULTIPLY;
            case 15:
                return PorterDuff.Mode.SCREEN;
            case 16:
                return Build.VERSION.SDK_INT >= 11 ? PorterDuff.Mode.valueOf("ADD")
                        : defaultMode;
            default:
                return defaultMode;
        }
    }

    private class TintInfo {
        public ColorStateList mTintList;
        public PorterDuff.Mode mTintMode;
        public boolean mHasTintMode;
        public boolean mHasTintList;
    }
}
