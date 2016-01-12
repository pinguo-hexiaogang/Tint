package com.okry.newstuff.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.okry.newstuff.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WebViewCacheTestActivity extends AppCompatActivity {
    @InjectView(R.id.webview)
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_cache_test);
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
        initWebView();
    }

    private void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); //设置 缓存模式 
        //开启 DOM storage API 功能 
        mWebView.getSettings().setDomStorageEnabled(true);

        //设置  Application Caches 缓存目录 
        mWebView.getSettings().setAppCachePath(getExternalCacheDir().getAbsolutePath());
        //开启 Application Caches 功能 
        mWebView.getSettings().setAppCacheEnabled(true);
    }

}
