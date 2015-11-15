package com.okry.newstuff.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.okry.newstuff.R;
import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DragSwitchActivity extends AppCompatActivity {
    @InjectView(R.id.drag_view)
    DragSwitchView mDragView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_switch);
        ButterKnife.inject(this);
        mDragView.setCurrentIndex(5);
        mDragView.setItemChangeListener(new DragSwitchView.IItemChangeListener() {
            @Override
            public void onItemChange(int index) {
                Logger.d("the select index is:" + index);
            }
        });
    }
}
