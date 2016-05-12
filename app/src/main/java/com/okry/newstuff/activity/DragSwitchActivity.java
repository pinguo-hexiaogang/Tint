package com.okry.newstuff.activity;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.okry.newstuff.R;
import com.okry.newstuff.view.DragSwitchViewHor;
import com.okry.newstuff.view.DragSwitchViewWithoutEvent;
import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DragSwitchActivity extends AppCompatActivity {
    @InjectView(R.id.drag_view)
    DragSwitchViewWithoutEvent mDragView;
    @InjectView(R.id.drag_view_hor)
    DragSwitchViewHor mDragViewHor;
    @InjectView(R.id.touch_view)
    View mTouchView;
    private GestureDetectorCompat mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_switch);
        ButterKnife.inject(this);
        mGestureDetector = new GestureDetectorCompat(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return mDragView.onDown(e);
            }

            @Override
            public void onShowPress(MotionEvent e) {
                mDragView.onShowPress(e);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return mDragView.onSingleTapUp(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return mDragView.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                mDragView.onLongPress(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return mDragView.onFling(e1, e2, velocityX, velocityY);
            }
        });
        mDragView.setCurrentIndex(5);
        mDragView.setItemChangeListener(new DragSwitchViewWithoutEvent.IItemChangeListener() {
            @Override
            public void onItemChange(int index) {
                Logger.d("the select index is:" + index);
            }
        });


        mDragViewHor.setCurrentIndex(1);
        mDragViewHor.setItemChangeListener(new DragSwitchViewHor.IItemChangeListener() {
            @Override
            public void onItemChange(int index) {
                Logger.d("the select index is:" + index);
            }
        });


        mTouchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean ret = false;
                ret = mGestureDetector.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mDragView.onUp(event);
                }
                return ret;
            }
        });
    }
}
