package com.okry.newstuff.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.okry.newstuff.R;
import com.okry.newstuff.view.BubbleView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BubbleActivity extends AppCompatActivity {

    @InjectView(R.id.bubble_view)
    BubbleView mBubbleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.inject(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mBubbleView.setBubbleImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bubble_image));
        mBubbleView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBubbleView.popBubble();
            }
        }, 1000);
    }

}
