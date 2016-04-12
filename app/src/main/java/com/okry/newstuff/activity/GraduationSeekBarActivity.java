package com.okry.newstuff.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.okry.newstuff.R;
import com.okry.newstuff.view.GraduationSeekBar;
import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GraduationSeekBarActivity extends AppCompatActivity {
    @InjectView(R.id.seekbar_2)
    GraduationSeekBar mSeekBar2;

    @InjectView(R.id.progress_tv)
    TextView mTextTv;

    @InjectView(R.id.button)
    Button mButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graduation_seek_bar);
        ButterKnife.inject(this);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mSeekBar2.setTotalStep(100);
        mSeekBar2.setStartStep(1);
        mSeekBar2.setOnSeekBarChangeListener(new GraduationSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(int progress, int total) {
                mTextTv.setText("start progress:" + progress + ",total:" + total);
            }

            @Override
            public void onStartTrackingTouch(int progress, int total) {
                mTextTv.setText("current progress:" + progress + ",total:" + total);
            }

            @Override
            public void onStopTrackingTouch(int progress, int total) {
                mTextTv.setText("stop progress:" + progress + ",total:" + total);
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartValue = (mStartValue + 10) % 100;
                //mSeekBar2.setStartStep(mStartValue);
                mSeekBar2.setCurrentStep(mStartValue);
            }
        });
        Logger.d("9 % 3.14:" + (9 % Math.PI));
    }

    private int mStartValue = 11;

}
