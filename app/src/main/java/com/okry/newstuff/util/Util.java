package com.okry.newstuff.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by hexiaogang on 11/13/15.
 */
public class Util {
    public static int dpToPixel(Context context, int dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        return (int) (density * dp);
    }

    public static int dpToPixel(Context context, float dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        return (int) (density * dp);
    }
    public static int test(){
        return 5;
    }
}
