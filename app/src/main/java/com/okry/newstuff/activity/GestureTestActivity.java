package com.okry.newstuff.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.okry.newstuff.R;
import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GestureTestActivity extends AppCompatActivity {

    @InjectView(R.id.down_imv)
    ImageView mDownImv;

    @InjectView(R.id.up_imv)
    ImageView mUpImv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_test);
        ButterKnife.inject(this);
        mDownImv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Logger.d("mDownImv down");
                return true;
            }
        });
        mUpImv.setOnTouchListener(new UpTouchListener());
    }

    private class UpTouchListener implements View.OnTouchListener {

        private GestureDetector detector = new GestureDetector(new UpGestureDetectorListener());

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean ret = detector.onTouchEvent(event);
            Logger.d("Up,ret is:" + (ret ? "true" : "false"));
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Logger.d("ACTION_DOWN");
                    return true;
                case MotionEvent.ACTION_UP:
                    Logger.d("ACTION_UP");
                    return false;
                case MotionEvent.ACTION_MOVE:
                    Logger.d("ACTION_MOVE");
                    return false;
            }
            return ret;
        }
    }

    private static class UpGestureDetectorListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}
