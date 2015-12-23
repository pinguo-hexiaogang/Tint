package com.okry.newstuff.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.okry.newstuff.R;
import com.okry.newstuff.detector.RotateGestureDetector;
import com.okry.newstuff.view.RotateScaleLineView;

public class RotateLineActivity extends AppCompatActivity {
    private RotateGestureDetector mRotateDetector = null;
    private ScaleGestureDetector mScaleDetector = null;
    private RotateScaleLineView mView = null;

    private int mRotationDegrees = 0;
    private float mScaleFactor = 1.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_line);
        mView = (RotateScaleLineView)findViewById(R.id.rotateScaleLine_view);
        mRotateDetector = new RotateGestureDetector(this,new RotateListener());
        mScaleDetector = new ScaleGestureDetector(this,new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mRotateDetector.onTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.i("Denny","scaleFactor:"+detector.getScaleFactor());
            mScaleFactor *= detector.getScaleFactor();
            Log.i("Denny","scaleFactor total:"+mScaleFactor);
            mScaleFactor = Math.max(0.1f, Math.min(10.0f, mScaleFactor));
            mView.setScale(mScaleFactor);
            return true;
        }
    }
    private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener{

        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            Log.d("Denny","onRotate:"+detector.getRotationDegreesDelta());
            mRotationDegrees -= detector.getRotationDegreesDelta();
            mView.setRotate(mRotationDegrees);
            return true;
        }
    }
}
