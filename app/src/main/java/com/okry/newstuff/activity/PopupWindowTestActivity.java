package com.okry.newstuff.activity;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.okry.newstuff.R;
import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PopupWindowTestActivity extends AppCompatActivity {
    @InjectView(R.id.button)
    Button button;
    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_window_test);
        ButterKnife.inject(this);
        button.postDelayed(() -> {
            popupWindow = new PopupWindow(this);
            TextView textView = new TextView(this);
            textView.setText("dianwwowowowowow");
            textView.setOnClickListener((View v) ->{
                Logger.d("onclick");
            });
            popupWindow.setWidth(300);
            popupWindow.setHeight(300);
            popupWindow.setContentView(textView);
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAsDropDown(button);
            popupWindow.setFocusable(false);
        }, 1000);

        button.setOnClickListener((View view) -> {
            Snackbar.make(getWindow().getDecorView(), "click", 2000).show();
        });
    }
}
