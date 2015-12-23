package com.okry.newstuff.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.okry.newstuff.R;

public class MatrixActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new RoundImageView(this));
    }

    class RoundImageView extends View {

        private Bitmap bitmap;
        int bitmapWidth;
        int bitmapHeight;
        Matrix matrix;

        public RoundImageView(Context context) {
            super(context);
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.eft_lightcolor_beauty);
            bitmapWidth = bitmap.getWidth();
            bitmapHeight = bitmap.getHeight();
            matrix = new Matrix();
            matrix.setRotate(30);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // 第一种方法：
            /*Bitmap roundBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight,Bitmap.Config.ARGB_8888);
            canvas = new Canvas(roundBitmap);
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(Color.BLUE);
			canvas.drawRoundRect(new RectF(0, 0, bitmapWidth, bitmapHeight),20.0f, 20.0f, paint);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
			canvas.drawBitmap(bitmap, 0, 0, null);
			canvas.drawBitmap(roundBitmap, 0, 0, paint);*/
            // 第二种方法：
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(0xff000000);
            paint.setTextSize(30);
            canvas.save();
            canvas.drawText("round rect image:", 0, 30, paint);
            canvas.translate(0, 60);
            canvas.drawBitmap(getRoundedCornerBitmap(bitmap), 0, 0, paint);

            canvas.translate(0, bitmapHeight + 10);
            canvas.drawText("生成带倒影的图片", 0, 0, paint);
            canvas.translate(0, 60);
            canvas.drawBitmap(createReflectionImageWithOrigin(bitmap), 0, 0, paint);
            canvas.restore();

        }

        public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
            // 创建一个指定宽度和高度的空位图对象
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            // 用该位图创建画布
            Canvas canvas = new Canvas(output);
            // 画笔对象
            final Paint paint = new Paint();
            // 画笔的颜色
            final int color = 0xff424242;
            // 矩形区域对象
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            // 未知
            final RectF rectF = new RectF(rect);
            // 拐角的半径
            final float roundPx = 12;
            // 消除锯齿
            paint.setAntiAlias(true);
            // 画布背景色
            canvas.drawARGB(0, 0, 0, 0);
            // 设置画笔颜色
            paint.setColor(color);
            // 绘制圆角矩形
            //canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            canvas.drawCircle(bitmapWidth / 2, bitmapHeight / 2, bitmapHeight / 3, paint);
            // 未知
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            // 把该图片绘制在该圆角矩形区域中
            canvas.drawBitmap(bitmap, rect, rect, paint);
            // 最终在画布上呈现的就是该圆角矩形图片，然后我们返回该Bitmap对象
            return output;
        }

        //获得带倒影的图片方法
        public Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
            // 图片与倒影之间的距离间隔
            final int reflectionGap = 2;
            // 原图的宽度
            int width = bitmap.getWidth();
            // 原图的高度
            int height = bitmap.getHeight();
            // 图片旋转，缩放等控制对象
            Matrix matrix = new Matrix();
            // 缩放（这里pre,set,post三种效果是不一样的，注意区别）
            matrix.preScale(1, -1);
            /**
             set是直接设置Matrix的值，每次set一次，整个Matrix的数组都会变掉。
             post是后乘，当前的矩阵乘以参数给出的矩阵。可以连续多次使用post，
             来完成所需的整个变换。例如，要将一个图片旋
             转30度，然后平移到(100,100)的地方，那么可以这样做:
             Matrix m = new Matrix();
             m.postRotate(30);
             m.postTranslate(100, 100);
             这样就达到了想要的效果。
             pre是前乘，参数给出的矩阵乘以当前的矩阵。所以操作是在当前矩阵的最前面发生的。
             例如上面的例子，如果用pre的话，就要这样:
             Matrix m = new Matrix();
             m.setTranslate(100, 100);
             m.preRotate(30);
             旋转、缩放和倾斜都可以围绕一个中心点来进行，如果不指定，默认情况下，
             是围绕(0,0)点来进行。

             关于缩放：
             scale的参数是比例。例如，我们缩放为100%，则有一点要注意，如果直接用
             100/bmp.getWidth()的话，会得到0，因为是整型相除，所以必须其中有一个是
             float型的，直接用100f就好 。
             如：matrix.setScale(100f/bmp.getWidth(), 100f/bmp.getHeight());
             */
            // 创建一个初始的倒影位图
            Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);
            // 新建一个宽度为原图宽度，高度为原图高度的3/2的位图，用于绘制新的位图，即整体的效果图位图对象
            Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Bitmap.Config.ARGB_8888);
            // 由该位图对象创建初始画布(规定了画布的宽高)
            Canvas canvas = new Canvas(bitmapWithReflection);
            // 在该画布上绘制原图
            canvas.drawBitmap(bitmap, 0, 0, null);
            // 创建一个画笔
            Paint deafalutPaint = new Paint();
            // 绘制一个矩形区域，该矩形区域便是原图和倒影图之间的间隔图
            canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);
            // 绘制该倒影图于间隔图的下方
            canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
            // 创建一个画笔
            Paint paint = new Paint();
            // 创建一个线性渐变对象
            LinearGradient shader = new LinearGradient(
                    0, bitmap.getHeight(),
                    0, bitmapWithReflection.getHeight() + reflectionGap,
                    0x70ffffff, 0x00ffffff,
                    Shader.TileMode.CLAMP
            );
            // 把渐变效果应用在画笔上
            paint.setShader(shader);
            // Set the Transfer mode to be porter duff and destination in
            // 未知
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            //paint.setColorFilter(new PorterDuffColorFilter(0xff000000,PorterDuff.Mode.SRC_IN));
            // Draw a rectangle using the paint with our linear gradient
            // 绘制出该渐变效果，也就是最终的倒影效果图
            canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
            // 返回
            return bitmapWithReflection;
        }

    }

    /**
     * 绘制圆角背景以及图片圆角的处理
     .配置文件实现
     <?xml version="1.0" encoding="utf-8"?>
     <layer-list
     * 		xmlns:android="http://schemas.android.com/apk/res/android"> <item
     * 		android:drawable="@drawable/icon_home_button_img"/> <item
     * 		android:drawable="@drawable/icon_home_shape_overlay"/>
     * </layer-list>
     * icon_home_shape_overlay如下
     * <?xml version="1.0" encoding="utf-8"?>
     * <shape
     * 		xmlns:android="http://schemas.android.com/apk/res/android"> <solid
     * 		android:color="#60000000"/>
     * 		<stroke android:width="3dp"
     * 			color="#ff000000"/>
     *		<corners android:radius="10dp" />
     * </shape>
     * 或者直接使用一种效果
     * <?xml version="1.0" encoding="UTF-8"?>
     * <shape
     * 		xmlns:android="http://schemas.android.com/apk/res/android">
     * 		<solid
     * 				android:color="#99FFFFFF"/>
     * 		<corners android:radius="30px"/>
     *		<padding
     * 				android:left="0dp" android:top="0dp" android:right="0dp"
     *				 android:bottom="0dp" />
     * </shape>
     *  然后
     * android:background="@drawable/my_shape_file"
     *
     *
     * 2.图片本身加上圆角 Bitmap myCoolBitmap = ... ; // <-- Your bitmap you want rounded
     *
     * 		int w = myCoolBitmap.getWidth(), h = myCoolBitmap.getHeight();
     * 		Bitmap rounder = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888); Canvas
     * 		canvas = new Canvas(rounder);
     *
     * 		Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
     * 		xferPaint.setColor(Color.RED);
     *
     * 		canvas.drawRoundRect(new RectF(0,0,w,h), 20.0f, 20.0f, xferPaint);
     *
     * 		xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
     * 		canvas.drawBitmap(myCoolBitmap, 0,0, null);
     * 		canvas.drawBitmap(rounder, 0,0, xferPaint);
     * 或者
     * public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
     * 		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
     * 		bitmap.getHeight(), Config.ARGB_8888);
     * 		Canvas canvas = newCanvas(output);
     *
     * 		final int color = 0xff424242; final Paint paint = new Paint(); final Rect
     *		rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); final RectF
     * 		rectF = new RectF(rect); final float roundPx = 12;
     *
     * 		paint.setAntiAlias(true); canvas.drawARGB(0, 0, 0, 0);
     * 		paint.setColor(color); canvas.drawRoundRect(rectF, roundPx, roundPx,paint);
     *
     * 		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
     * 		canvas.drawBitmap(bitmap, rect, rect, paint);
     *
     * 		return output;
     * }
     */

}
