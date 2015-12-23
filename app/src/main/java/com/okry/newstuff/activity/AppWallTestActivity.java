package com.okry.newstuff.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.okry.newstuff.R;
import com.okry.newstuff.view.AppWallRotateView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AppWallTestActivity extends AppCompatActivity {
    @InjectView(R.id.appwall_view)
    AppWallRotateView mAppWall;

    @InjectView(R.id.start_btn)
    Button mStartBtn;

    @InjectView(R.id.stop_btn)
    Button mStopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_wall_test);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        setBtns();
    }

    private void setBtns() {
        mStartBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mAppWall.startAnim();
            }
        });
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppWall.stopAnim();
            }
        });
    }

}
