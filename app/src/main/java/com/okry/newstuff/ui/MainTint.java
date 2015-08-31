package com.okry.newstuff.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.okry.newstuff.BaseActivity;
import com.okry.newstuff.R;
import com.okry.newstuff.tintwidget.TintButton;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by mr on 15/8/24.
 */
public class MainTint extends BaseActivity implements View.OnClickListener{

    @InjectView(R.id.tint_image1)
    ImageView mView1;
    @InjectView(R.id.tint_image2)
    Button mView2;
    @InjectView(R.id.tint_image3)
    ImageView mView3;
    @InjectView(R.id.tint_image4)
    ImageView mView4;
    @InjectView(R.id.tint_image5)
    ImageView mView5;
    @InjectView(R.id.tint_image6)
    ImageView mView6;
    @InjectView(R.id.tint_image7)
    ImageView mView7;
    @InjectView(R.id.tint_imagebutton1)
    ImageButton mButton1;

    @InjectView(R.id.tint_btn)
    TintButton mTintBtn;

    @InjectView(R.id.compat_btn)
    AppCompatButton mCompatButton;

    @InjectView(R.id.compat_tv)
    AppCompatAutoCompleteTextView mCompatTv;

    @InjectView(R.id.tint_imv)
    View mTintImv;

    @InjectView(R.id.tint_imv_src)
    View mTintImvSrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tint);
        ButterKnife.inject(this);
        setClickListener();
        // 使用DrawableCompact的方式
        Drawable d = getResources().getDrawable(R.drawable.abc_ic_clear_mtrl_alpha);
        d = DrawableCompat.wrap(d);
        //DrawableCompat.setTint(d, 0x90ff0000);
        DrawableCompat.setTintList(d,getResources().getColorStateList(R.color.custom_tint));
        mView2.setBackgroundDrawable(d);// 不需要设置
        // 使用DrawableCompact + mute的方式
        Drawable d2 = getResources().getDrawable(R.drawable.abc_ic_search_api_mtrl_alpha);
        d2 = DrawableCompat.wrap(d2);
        d2 = d2.mutate();
        DrawableCompat.setTint(d2, 0x900000ff);
        mView5.setImageDrawable(d2);// 重设
        // 使用TintedBitmapDrawable
        TintedBitmapDrawable d3 = new TintedBitmapDrawable(getResources(), R.drawable.abc_ic_menu_share_mtrl_alpha, 0x900ffff0);
        mView6.setImageDrawable(d3);
        //

        //AppCompatButton
        mCompatButton.setSupportBackgroundTintList(getResources().getColorStateList(R.color.custom_tint));

        //mCompatTv.setSupportBackgroundTintList(getResources().getColorStateList(R.color.custom_tint));
    }
    private void setClickListener(){
        mView1.setOnClickListener(this);
        mView2.setOnClickListener(this);
        mView3.setOnClickListener(this);
        mView4.setOnClickListener(this);
        mView5.setOnClickListener(this);
        mView6.setOnClickListener(this);
        mView7.setOnClickListener(this);
        mButton1.setOnClickListener(this);
        mCompatButton.setOnClickListener(this);
        mCompatTv.setOnClickListener(this);
        mTintBtn.setOnClickListener(this);
        mTintImv.setOnClickListener(this);
        mTintImvSrc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
