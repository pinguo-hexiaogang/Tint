package com.okry.newstuff.tintwidget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.TintableBackgroundView;
import android.support.v7.internal.widget.TintManager;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.okry.newstuff.R;

/**
 * Created by hexiaogang on 8/28/15.
 */
public class TintImageView extends ImageView implements TintableBackgroundView{
    private final TintManager mTintManager;
    public TintImageView(Context context) {
        this(context,null);
    }

    public TintImageView(Context context, AttributeSet attrs) {
        this(context,attrs, -1);
    }

    public TintImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context,attrs,defStyleAttr,-1);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TintImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mTintManager = TintManager.get(context);
    }

    @Override
    public void setSupportBackgroundTintList(ColorStateList tint) {

    }

    @Nullable
    @Override
    public ColorStateList getSupportBackgroundTintList() {
        return null;
    }

    @Override
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {

    }

    @Nullable
    @Override
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        return null;
    }
}
