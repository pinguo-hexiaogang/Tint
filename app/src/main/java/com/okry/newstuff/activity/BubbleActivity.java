package com.okry.newstuff.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.okry.newstuff.R;
import com.okry.newstuff.view.BubbleView;
import com.orhanobut.logger.Logger;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BubbleActivity extends AppCompatActivity {

    @InjectView(R.id.bubble_view)
    BubbleView mBubbleView;
    @InjectView(R.id.span_tv)
    TextView mSpanTv;

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
        SpannableStringBuilder styleTextBuilder = new SpannableStringBuilder(mSpanTv.getText());
        styleTextBuilder.setSpan(new ForegroundColorSpan(0XFFFDD600), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //mSpanTv.setText(styleTextBuilder);
        testNullify();
    }
    public void testNullify() {
        Collection<Integer> c  = new ArrayList<Integer>();
        nullify(c);
        Assert.assertNotNull(c);
        final Collection<Integer> c1 = c;
        Assert.assertTrue(c1.equals(c));
        change(c);
        Assert.assertTrue(c1.equals(c));
    }

    private void change(Collection<Integer> c) {
        c = new ArrayList<Integer>();
    }

    public void nullify(Collection<?> t) {
        t = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int i = 0;
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                i++;
                Logger.d("down down,repeat count:" + event.getRepeatCount());
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
