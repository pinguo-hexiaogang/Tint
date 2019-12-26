package com.okry.newstuff.util

import android.view.MotionEvent


fun MotionEvent?.eventName(): String {
    if (this == null) return ""
    return when (this.action) {
        MotionEvent.ACTION_DOWN -> "DOWN"
        MotionEvent.ACTION_UP -> "UP"
        MotionEvent.ACTION_CANCEL -> "CANCEL"
        MotionEvent.ACTION_MOVE -> "MOVE"
        MotionEvent.ACTION_SCROLL -> "SCROLL"
        else -> "OTHER"
    }
}