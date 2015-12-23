package com.okry.newstuff.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.okry.newstuff.R;
import com.okry.newstuff.view.DownloadDrawable;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DownloadDrawableActivity extends AppCompatActivity {
    @InjectView(R.id.view1)
    View mView1;
    @InjectView(R.id.view2)
    View mView2;
    @InjectView(R.id.view3)
    View mView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_drawable);
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


        //还没开始下载
        DownloadDrawable drawable1 = new DownloadDrawable(this);
        mView1.setBackgroundDrawable(drawable1);

        //正在下载
        DownloadDrawable drawable2 = new DownloadDrawable(this);
        drawable2.setProgress(70);
        mView2.setBackgroundDrawable(drawable2);

        //下载完成
        DownloadDrawable drawable3 = new DownloadDrawable(this);
        drawable3.setState(DownloadDrawable.STATUS_DOWNLOAD_DONE);
        mView3.setBackgroundDrawable(drawable3);
    }

}
