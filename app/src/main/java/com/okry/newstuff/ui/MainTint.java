package com.okry.newstuff.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.okry.newstuff.BaseActivity;
import com.okry.newstuff.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by mr on 15/8/24.
 */
public class MainTint extends BaseActivity {

    @InjectView(R.id.tint_image1)
    ImageView mView1;
    @InjectView(R.id.tint_image2)
    ImageView mView2;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tint);
        ButterKnife.inject(this);
        // 使用DrawableCompact的方式
        Drawable d = getResources().getDrawable(R.drawable.abc_ic_clear_mtrl_alpha);
        d = DrawableCompat.wrap(d);
        DrawableCompat.setTint(d, 0x90ff0000);
//        mView2.setImageDrawable(d);// 不需要设置
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
    }

}
