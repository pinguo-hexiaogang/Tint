package com.okry.newstuff.tintwidget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;

/**
 * Created by hexiaogang on 8/31/15.
 */
public interface IPgImageTint {
    void setPgImageTintList(ColorStateList tintList);

    void setPgImageTintMode(PorterDuff.Mode mode);

    ColorStateList getPgImageTintList();

    PorterDuff.Mode getPgImageTintMode();
}
