package com.okry.newstuff.activity;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import java.util.Comparator;
import java.util.List;

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


        List<String> lists = new ArrayList<>();
        lists.add("1");
        lists.add("1");
        lists.add("2");
        lists.add("1");
        lists.add("3");
        lists.add("1");
        lists.add("5");
        lists.add("5");
        lists.add("7");
        lists.add("5");
        lists.add("7");
        lists.add("8");
        List<String> newList = removeEqual(lists, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.equals(o2))
                    return 0;
                else
                    return 1;
            }
        });
        testMatrixMirror();
    }

    private void testMatrixMirror(){

        float[] src = {0.2f,1f};
        float[] dst = new float[2];
        Matrix matrix = new Matrix();
        //matrix.postTranslate(-0.5f,0);
        matrix.postScale(-1,1,0.5f,0f);
        //matrix.postTranslate(0.5f,0);
        matrix.mapPoints(dst,src);

        for(float n:dst){
            System.out.println(n);
        }

    }

    public void testNullify() {
        Collection<Integer> c = new ArrayList<Integer>();
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

    public static <T> List<T> removeEqual(List<T> srcList, Comparator<T> comparator) {
        if (srcList == null) {
            return null;
        }
        List<T> newList = new ArrayList<T>();
        for (T t : srcList) {
            boolean alreadyHas = false;
            for (T newT : newList) {
                if (comparator.compare(newT, t) == 0) {
                    alreadyHas = true;
                    break;
                }
            }
            if (!alreadyHas) {
                newList.add(t);
            }
        }
        return newList;
    }
}
