package com.okry.newstuff.activity.event

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.TextView
import com.okry.newstuff.util.eventName

class InnerView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : TextView(context, attrs, defStyleAttr) {

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val result = true
        Log.d("InnerView", "onTouchEvent,${event.eventName()},result:$result")
        return result
    }
}