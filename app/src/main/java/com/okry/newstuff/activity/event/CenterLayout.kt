package com.okry.newstuff.activity.event

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout
import com.okry.newstuff.util.eventName

class CenterLayout @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val result = when (ev?.action) {
            MotionEvent.ACTION_DOWN -> true
            else -> false
        }
        //val result = true
        Log.d("CenterLayout", "onInterceptTouchEvent,${ev.eventName()},return:$result")
        return result
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val result = false
        Log.d("CenterLayout", "onTouchEvent,${event.eventName()},return:$result")
        return result
    }
}