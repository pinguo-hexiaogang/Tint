package com.okry.newstuff.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    public static Bitmap getBitmapByDrawable(Drawable drawable){
        if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }else{
            Bitmap bitmap = null;
            if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    /**
     * 是否有可用的3G、Wifi网络
     *
     * @return 是否有可用的3G、Wifi网络
     */
    public static boolean isAvailableNetWork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return isAvailableNetWork(cm);
    }

    /**
     * 是否有可用的3G、Wifi网络
     *
     * @param cm ConnectivityManager
     * @return 是否有可用的3G、Wifi网络
     * @author liubo
     */
    public static boolean isAvailableNetWork(ConnectivityManager cm) {
        //可用网络判断
        NetworkInfo netWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (netWifi != null) {
            if (netWifi.isConnected()) {
                return true;
            }
        }

        NetworkInfo netMobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (netMobile != null) {
            if (netMobile.isConnected()) {
                return true;
            }
        }

        return false;
    }
}
