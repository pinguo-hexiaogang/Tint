package com.okry.newstuff.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.okry.newstuff.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BimfaceActivity extends AppCompatActivity {
    @InjectView(R.id.webview)
    WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bimface);
        ButterKnife.inject(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        Log.d("xiaogang", "test=========");
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }
        });
        webView.loadUrl("file:///android_asset/bimface_demo.html");
        //webView.loadUrl("https://bimface.com/developer-jsdemo#822");

    }
}
