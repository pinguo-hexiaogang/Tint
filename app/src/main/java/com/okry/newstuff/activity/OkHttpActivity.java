package com.okry.newstuff.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.okry.newstuff.R;
import com.orhanobut.logger.Logger;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class OkHttpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http);
        request();

    }

    public void uriTest() {
        String testStr = "test://xiaogang@google.com:899/hexiaogang/kjkkk";
        Uri uri = Uri.parse(testStr);
        String schme = uri.getScheme();
        String host = uri.getHost();
        String path = uri.getPath();
        String au = uri.getAuthority();
        System.out.print("schkkkk");
    }

    private void request() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://www.baidu.com").build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Logger.d("faile");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Logger.d("success" + response.toString());
            }
        });

    }
}
