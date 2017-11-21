package com.okry.newstuff.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.ViewSwitcher

/**
 * Created by hexiaogang on 2017/11/21.
 */
class FadeImageSwitcher : ImageSwitcher, ViewSwitcher.ViewFactory {

    constructor(ctx: Context) : super(ctx) {
        setFactory(this)
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        setFactory(this)
    }

    override fun makeView(): View {
        val imageView = ImageView(context)
        val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT)
        imageView.layoutParams = layoutParams
        return imageView
    }

}