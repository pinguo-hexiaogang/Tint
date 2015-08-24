package com.okry.newstuff;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * Created by MR on 14-3-20.
 */
public class BaseActivity extends FragmentActivity {

    protected final String LIFE_CYCLE_TAG = "activity_lifecycle";
    protected final String CLASS_TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LIFE_CYCLE_TAG, CLASS_TAG + ":onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LIFE_CYCLE_TAG, CLASS_TAG + ":onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LIFE_CYCLE_TAG, CLASS_TAG + ":onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LIFE_CYCLE_TAG, CLASS_TAG + ":onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LIFE_CYCLE_TAG, CLASS_TAG + ":onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LIFE_CYCLE_TAG, CLASS_TAG + ":onDestroy");
    }
}
