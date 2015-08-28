package com.okry.newstuff.tintwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.graphics.drawable.DrawableUtils;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.okry.newstuff.R;

/**
 * Created by hexiaogang on 8/28/15.
 */
public class TintButton extends AppCompatButton {
    public TintButton(Context context) {
        this(context,null);
    }

    public TintButton(Context context, AttributeSet attrs) {
        this(context,attrs,R.attr.buttonStyle);
    }

    public TintButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttr(attrs, defStyleAttr);
    }

    private void loadAttr(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.Tint, defStyleAttr, 0);
        try {

            if (a.hasValue(R.styleable.Tint_PgBackgroundTint)) {
                setSupportBackgroundTintList(a.getColorStateList(R.styleable.Tint_PgBackgroundTint));
            }
            if (a.hasValue(R.styleable.Tint_PgBackgroundTintMode)) {
                setSupportBackgroundTintMode(DrawableUtils.parseTintMode(
                        a.getInt(R.styleable.Tint_PgBackgroundTintMode, -1),
                        null));
            }
        } finally {
            a.recycle();
        }
    }
}
