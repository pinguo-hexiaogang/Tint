package com.okry.newstuff.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.okry.newstuff.R;
import com.okry.newstuff.view.GraduationSeekBar2;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GraduationSeekBarActivity extends AppCompatActivity {
    @InjectView(R.id.seekbar_2)
    GraduationSeekBar2 mSeekBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graduation_seek_bar);
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
        mSeekBar2.setRange(-100, 100);
        mSeekBar2.setStep(100);
        mSeekBar2.setValue(20);
    }

}
