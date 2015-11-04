package com.okry.newstuff;

import android.app.Application;

import com.orhanobut.logger.AndroidLogTool;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * Created by hexiaogang on 10/28/15.
 */
public class NewStuffApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        logInit();
    }

    private void logInit() {
        Logger.init("Denny")                 // default PRETTYLOGGER or use just init()
                .methodCount(1)                 // default 2
                .hideThreadInfo()               // default shown
                .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                .methodOffset(0)                // default 0
                .logTool(new AndroidLogTool()); // custom log tool, optional
    }
}
