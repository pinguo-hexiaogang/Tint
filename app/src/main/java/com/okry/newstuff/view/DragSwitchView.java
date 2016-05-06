package com.okry.newstuff.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import com.okry.newstuff.R;
import com.okry.newstuff.util.Util;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hexiaogang on 11/12/15.
 */
public class DragSwitchView extends View implements GestureDetector.OnGestureListener {
    private String mTextStr;
    private Paint mBackgroundPaint;
    private Paint mTextPaint;
    private int mBackgroundColor = 0x66ff7a73;
    private int mTextColor = 0xffffffff;
    private int mRoundRadius;
    private boolean mDisable = false;

    private int mInitX;
    private int mInitY;
    private int mPaddingTop;
    private int mPaddingBottom;
    private int mPaddingLeft;
    private int mPaddingRight;
    //文字和数字之间的space
    private int mSpace;
    //数字之间的间隔
    private int mDigitalSpace;
    private Rect mBackRect;
    private Point mTextPoint;
    private Point mDigitalPoint;
    private Rect mDigitalRect;

    private int mDeltaY = 0;
    private int mMaxDeltaY;
    private int mMinDeltaY;
    private List<String> mItemList;
    private Rect mTargetRect;
    //保存每个item垂直中心点的值
    private SparseArray<Integer> mCenterYMap = new SparseArray<>();

    private GestureDetectorCompat mGestureDetector;
    private OverScroller mScroller;

    private boolean mFling = false;
    private boolean mIsPressed = false;
    private boolean mInScroll = false;
    private int mLastSelectIndex = 0;
    private boolean mIsInLongPress = false;
    private boolean mIsFirstScroll = true;
    private int mOverScrollDis = 0;
    private int mTextSize;
    private int mTextSizeBig;

    private IItemChangeListener mItemChangeListener = null;
    private IGestureListener mCustomGestureListener = null;
    private static int REL_SWIPE_MIN_DISTANCE;
    private static int REL_SWIPE_MAX_OFF_PATH;
    private static int REL_SWIPE_THRESHOLD_VELOCITY;


    public DragSwitchView(Context context) {
        super(context);
        init();
    }

    public DragSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, 0);
        init();
    }

    public DragSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DragSwitchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs, defStyleAttr);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetectorCompat(getContext(), this);
        mGestureDetector.setIsLongpressEnabled(true);

        mRoundRadius = Util.dpToPixel(getContext(), 18);
        mPaddingTop = Util.dpToPixel(getContext(), 8);
        mPaddingBottom = Util.dpToPixel(getContext(), 10);
        mPaddingLeft = Util.dpToPixel(getContext(), 20);
        mPaddingRight = Util.dpToPixel(getContext(), 20);
        mSpace = Util.dpToPixel(getContext(), 10);
        mDigitalSpace = Util.dpToPixel(getContext(), 35);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextSize = Util.dpToPixel(getContext(), 18);
        mTextSizeBig = Util.dpToPixel(getContext(), 20);
        mTextPaint.setTextSize(mTextSize);
        String[] defaultStrs = {"1", "2", "3", "4", "5", "6"};
        mItemList = new ArrayList(Arrays.asList(defaultStrs));
        mScroller = new OverScroller(getContext());
        mScroller.setFriction(0.08f);


        DisplayMetrics dm = getResources().getDisplayMetrics();
        REL_SWIPE_MIN_DISTANCE = (int) (50.0f * dm.density + 0.5);
        REL_SWIPE_MAX_OFF_PATH = (int) (250.0f * dm.density + 0.5);
        REL_SWIPE_THRESHOLD_VELOCITY = (int) (200.0f * dm.density + 0.5);
    }

    private void initAttrs(AttributeSet attrs, int defStyle) {
        TypedArray ta = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.DragSwitchView,
                defStyle,
                0);
        String name = ta.getString(R.styleable.DragSwitchView_name);
        if (TextUtils.isEmpty(name)) {
            mTextStr = "";
        } else {
            mTextStr = name;
        }
    }

    public void setItemChangeListener(IItemChangeListener listener) {
        this.mItemChangeListener = listener;
    }

    public void setGestureListener(IGestureListener listener) {
        this.mCustomGestureListener = listener;
    }

    private String getLongestItem() {
        String longestStr = "";
        for (String str : mItemList) {
            if (str.length() >= longestStr.length()) {
                longestStr = str;
            }
        }
        return longestStr;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mInitX = w / 2;
        mInitY = h / 5;

        String textDraw = mTextStr + getLongestItem();
        Rect textRect = new Rect();
        mTextPaint.getTextBounds(textDraw, 0, textDraw.length(), textRect);

        mDigitalRect = new Rect();
        String digitalStr = getLongestItem();
        mTextPaint.getTextBounds(digitalStr, 0, digitalStr.length(), mDigitalRect);

        int totalWidth = mPaddingLeft + mPaddingRight + textRect.width() + mSpace;
        int totalHeight = mPaddingTop + mPaddingBottom + textRect.height();
        mBackRect = new Rect(mInitX - totalWidth / 2, mInitY, mInitX - totalWidth / 2 + totalWidth, mInitY + totalHeight);
        mTextPoint = new Point(mBackRect.left + mPaddingLeft, mBackRect.bottom - mPaddingBottom);
        mDigitalPoint = new Point(mBackRect.right - mPaddingRight - mDigitalRect.width(), mTextPoint.y);
        mMaxDeltaY = 0;
        mMinDeltaY = -(mDigitalSpace + mDigitalRect.height()) * (mItemList.size() - 1);
        mTargetRect = new Rect(mDigitalPoint.x, mDigitalPoint.y - mDigitalRect.height(), mDigitalPoint.x + mDigitalRect.width(), mDigitalPoint.y);
        mOverScrollDis = mDigitalSpace / 2;
    }

    public void setItemList(List<String> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            return;
        }
        mItemList = itemList;
        requestLayout();
    }

    public void setCurrentIndex(int currentIndex) {
        Logger.d("setCurrentIndex:" + currentIndex);
        if (currentIndex < 0) {
            currentIndex = 0;
        } else if (currentIndex > (mItemList.size() - 1)) {
            currentIndex = mItemList.size() - 1;
        }
        mLastSelectIndex = currentIndex;
        Rect digitalRect = new Rect();
        String digitalStr = getLongestItem();
        mTextPaint.getTextBounds(digitalStr, 0, digitalStr.length(), digitalRect);
        mDeltaY = -currentIndex * (digitalRect.height() + mDigitalSpace);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCenterYMap.clear();
        canvas.drawRoundRect(new RectF(mBackRect), mRoundRadius, mRoundRadius, mBackgroundPaint);
        canvas.drawText(mTextStr, mTextPoint.x, mTextPoint.y, mTextPaint);
        int digitalY = mDigitalPoint.y + mDeltaY;
        int digitalX = mDigitalPoint.x;
        for (int i = 0;
             i < mItemList.size();
             i++) {
            Rect digitalRect = new Rect(digitalX, digitalY - mDigitalRect.height(), digitalX + mDigitalRect.width(), digitalY);
            mCenterYMap.put(i, (digitalRect.top + digitalRect.bottom) / 2);
            /*
             *设置字体透明度
             * 1.字体全部在背景内，不透明
             * 2.字体部分在背景内，半透明
             * 3.字体全部在背景外，全透明
             */
            if (mBackRect.contains(digitalRect)) {
                //mTextPaint.setTextSize(mTextSizeBig);
                mTextPaint.setAlpha(255);
                canvas.drawText(mItemList.get(i), digitalX, digitalY, mTextPaint);
            } else if (/*mIsPressed && */(mBackRect.contains(digitalRect.left, digitalRect.top) || mBackRect.contains(digitalRect.left, digitalRect.bottom))) {
                mTextPaint.setAlpha(100);
                canvas.drawText(mItemList.get(i), digitalX, digitalY, mTextPaint);
            } else {
                mTextPaint.setAlpha(100);
                mTextPaint.setAlpha(100);
                canvas.drawText(mItemList.get(i), digitalX, digitalY, mTextPaint);
            }
            digitalY += mDigitalRect.height() + mDigitalSpace;
        }
        //mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAlpha(255);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDisable) {
            return false;
        }
        boolean ret = mGestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Logger.d("Action_up");
            mIsPressed = false;
            mInScroll = false;
            if (mIsInLongPress) {
                if (mCustomGestureListener != null) {
                    mIsInLongPress = false;
                    mCustomGestureListener.onLongPress(false);
                }
            } else {
                autoSettle();
                if (mCustomGestureListener != null) {
                    mCustomGestureListener.onUp();
                }
            }
        }
        return ret;
    }

    private void autoSettle() {
        if (mFling) {
            return;
        }
        int scrollY = calculateScrollY();
        if (scrollY != 0) {
            mScroller.startScroll(0, mDeltaY, 0, scrollY, 500);
            invalidate();
        } else {
            if (!mIsPressed) {
                //通知状态改变
                callItemChangeListener();
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //L.d("computeScroll");
        if (mScroller.computeScrollOffset()) {
            mDeltaY = mScroller.getCurrY();
            //Logger.d("mDeltaY is:" + mDeltaY);
            invalidate();
        } else {
            if (mFling) {
                mFling = false;
                autoSettle();
            } else {
                //通知状态改变
                if (!mIsPressed) {
                    callItemChangeListener();
                }
            }
        }
    }

    private void callItemChangeListener() {
        if (mItemChangeListener != null) {
            int newIndex = calculateCurrentIndex();
            if (newIndex >= 0 && newIndex <= mItemList.size() - 1 && mLastSelectIndex != newIndex) {
                Logger.d("call onItemChange,the selected item is:" + newIndex);
                Logger.d(Log.getStackTraceString(new Throwable()));

                mLastSelectIndex = newIndex;
                mItemChangeListener.onItemChange(newIndex);
            }
        }
    }

    private int calculateCurrentIndex() {
        int targetCenterY = (mTargetRect.top + mTargetRect.bottom) / 2;
        int minDelta = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < mItemList.size(); i++) {
            if (mCenterYMap.get(i) == null) {
                Logger.d("calculateCurrentIndex,get is null,continue");
                continue;
            }
            int delta = targetCenterY - mCenterYMap.get(i);
            if (Math.abs(minDelta) > Math.abs(delta)) {
                minDelta = delta;
                index = i;
            }
        }
        Logger.d("calculateCurrentIndex,index is:" + index);
        return index;
    }

    private int calculateScrollY() {
        int targetCenterY = (mTargetRect.top + mTargetRect.bottom) / 2;
        int minDelta = Integer.MAX_VALUE;
        for (int i = 0;
             i < mItemList.size();
             i++) {
            int delta = targetCenterY - mCenterYMap.get(i);
            if (Math.abs(minDelta) > Math.abs(delta)) {
                minDelta = delta;
            }
        }
        return minDelta;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (mCustomGestureListener != null) {
            mCustomGestureListener.onDown();
        }
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        mFling = false;
        mIsPressed = true;
        mIsFirstScroll = true;
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!mInScroll && Math.abs(e2.getY() - e1.getY()) < REL_SWIPE_MIN_DISTANCE) {
            return false;
        }
        if (!mIsPressed) {
            return false;
        }
        Logger.d("onScroll,distanceY:" + distanceY);
        if (mIsFirstScroll && mCustomGestureListener != null) {
            mIsFirstScroll = false;
            mCustomGestureListener.onStatusChange();
        }
        mDeltaY -= distanceY * 0.6;
        if (mDeltaY > mMaxDeltaY + mOverScrollDis) {
            mDeltaY = mMaxDeltaY + mOverScrollDis;
        }
        if (mDeltaY < mMinDeltaY - mOverScrollDis) {
            mDeltaY = mMinDeltaY - mOverScrollDis;
        }
        invalidate();
        mInScroll = true;
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (mCustomGestureListener != null) {
            mIsInLongPress = true;
            mCustomGestureListener.onLongPress(true);
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Logger.d("onFling");
        float diffX = e2.getX() - e1.getX();
        if (!mInScroll && Math.abs(diffX) > REL_SWIPE_MIN_DISTANCE && Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
            if (mCustomGestureListener != null) {
                if (diffX > 0) {
                    mCustomGestureListener.flingHorizontal(true);
                } else {
                    mCustomGestureListener.flingHorizontal(false);
                }
            }
            Logger.d("fling left:" + ((diffX > 0) ? "true" : "false"));
            return true;
        }
        mFling = true;
        mScroller.fling(0, mDeltaY, 0, (int) velocityY, 0, 0, mMinDeltaY, mMaxDeltaY, 0, mDigitalRect.height());
        invalidate();
        if (mCustomGestureListener != null) {
            mCustomGestureListener.onStatusChange();
        }
        return true;
    }

    public void disableDrag(boolean disable) {
        this.mDisable = disable;
    }

    public static interface IItemChangeListener {
        public void onItemChange(int index);
    }

    public static interface IGestureListener {
        public void onLongPress(boolean isDown);

        public void onStatusChange();

        public void onUp();

        public void onDown();

        public void flingHorizontal(boolean left);
    }
}
