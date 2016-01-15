package com.okry.newstuff.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.okry.newstuff.R;
import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WebViewCacheTestActivity extends AppCompatActivity {
    @InjectView(R.id.webview_hide)
    WebView mWebViewHidden;

    @InjectView(R.id.webview)
    WebView mWebView;
    @InjectView(R.id.content_layout)
    ViewGroup mContentLayout;

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
        setClient();
        initWebView2();
        setClient2();
        //mWebViewHidden.loadUrl("http://www.baidu.com");
        //mWebViewHidden.loadUrl("https://mall.camera360.com/Ebusiness/views/index.html");
        //mWebViewHidden.loadUrl("http://map.baidu.com/mobile/webapp/index/index#index/index/foo=bar/vt=map");
        mWebViewHidden.loadUrl("http://union.co/");
        //mWebViewHidden.loadUrl("http://joomla.templaza.net/everline/");
        //mWebViewHidden.loadUrl("http://www.mug.pl/");
        mWebViewHidden.setVisibility(View.INVISIBLE);
        mWebView.setVisibility(View.INVISIBLE);
    }

    private void initWebView() {
        mWebViewHidden.getSettings().setJavaScriptEnabled(true);

        mWebViewHidden.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //开启 DOM storage API 功能 
        mWebViewHidden.getSettings().setDomStorageEnabled(true);

        String cachePath = getCacheDir().getAbsolutePath();
        //设置  Application Caches 缓存目录 
        mWebViewHidden.getSettings().setAppCachePath(cachePath);
        mWebViewHidden.getSettings().setDatabaseEnabled(true);
        mWebViewHidden.getSettings().setDatabasePath(cachePath);
        //开启 Application Caches 功能 
        mWebViewHidden.getSettings().setAppCacheEnabled(true);

        mWebViewHidden.getSettings().setAllowFileAccess(true);
    }

    private void initWebView2() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        //开启 DOM storage API 功能 
        mWebView.getSettings().setDomStorageEnabled(true);

        String cachePath = getCacheDir().getAbsolutePath();
        //设置  Application Caches 缓存目录 
        mWebView.getSettings().setAppCachePath(cachePath);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDatabasePath(cachePath);
        //开启 Application Caches 功能 
        mWebView.getSettings().setAppCacheEnabled(true);

        mWebView.getSettings().setAllowFileAccess(true);
    }

    private void setClient() {
        mWebViewHidden.setWebChromeClient(new WebChromeClient());
        mWebViewHidden.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Logger.d("load start");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Logger.d("load finished");
                //mWebView.loadUrl(url);
            }
        });
    }

    private void setClient2() {
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Logger.d("webview2 load start");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Logger.d("webview2 load finished");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       /* mContentLayout.removeView(mWebView);
        mContentLayout.removeView(mWebViewHidden);
        mWebView.destroy();
        mWebViewHidden.destroy();*/
    }
}
