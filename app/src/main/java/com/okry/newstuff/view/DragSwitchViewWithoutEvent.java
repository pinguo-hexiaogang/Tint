package com.okry.newstuff.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
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
 * 自己不处理事件,都是外面传进来的
 */
public class DragSwitchViewWithoutEvent extends View {
    private String mTextStr;
    private Paint mBackgroundPaint;
    private Paint mTextPaint;
    private int mBackgroundColor = 0x55333333;
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
    private Rect mBackRect = new Rect();
    private Point mTextPoint = new Point();
    private Point mDividerPoint = new Point();
    private Point mDigitalPoint = new Point();
    private Rect mDigitalRect = new Rect();

    private int mDeltaY = 0;
    private int mMaxDeltaY;
    private int mMinDeltaY;
    private List<String> mItemList;
    private Rect mTargetRect = new Rect();
    //保存每个item垂直中心点的值
    private SparseArray<Integer> mCenterYMap = new SparseArray<>();

    private OverScroller mScroller;

    private boolean mFling = false;
    private boolean mIsPressed = false;
    private boolean mInScroll = false;
    private int mLastSelectIndex = 0;
    private int mOverScrollDis = 0;
    private int mTextSize;
    private int mTextSizeBig;

    private IItemChangeListener mItemChangeListener = null;
    private static int REL_SWIPE_MIN_DISTANCE;
    private static int REL_SWIPE_THRESHOLD_VELOCITY;
    private static int REL_SWIPE_MAX_OFF_PATH;
    private boolean mIsHiding = false;
    //每次currentIndex发生变化都通知
    private boolean mDispatchEveryChange = false;


    public DragSwitchViewWithoutEvent(Context context) {
        super(context);
        init();
    }

    public DragSwitchViewWithoutEvent(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, 0);
        init();
    }

    public DragSwitchViewWithoutEvent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DragSwitchViewWithoutEvent(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRoundRadius = Util.dpToPixel(getContext(), 10);
        mPaddingTop = Util.dpToPixel(getContext(), 12);
        mPaddingBottom = Util.dpToPixel(getContext(), 15);
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
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextSize = Util.dpToPixel(getContext(), 18);
        mTextSizeBig = Util.dpToPixel(getContext(), 20);
        mTextPaint.setTextSize(mTextSize);
        String[] defaultStrs = {"5", "4", "3", "2", "1", "OFF"};
        mItemList = new ArrayList(Arrays.asList(defaultStrs));
        mScroller = new OverScroller(getContext());
        mScroller.setFriction(0.08f);


        DisplayMetrics dm = getResources().getDisplayMetrics();
        REL_SWIPE_MIN_DISTANCE = (int) (50.0f * dm.density + 0.5);
        REL_SWIPE_THRESHOLD_VELOCITY = (int) (300.0f * dm.density + 0.5);
        REL_SWIPE_MAX_OFF_PATH = (int) (250.0f * dm.density + 0.5);
    }

    private void initAttrs(AttributeSet attrs, int defStyle) {
        TypedArray ta = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.DragSwitchViewWithoutEvent,
                defStyle,
                0);
        String name = ta.getString(R.styleable.DragSwitchViewWithoutEvent_left_text);
        if (TextUtils.isEmpty(name)) {
            mTextStr = "";
        } else {
            mTextStr = name;
        }
    }

    public void setItemChangeListener(IItemChangeListener listener) {
        this.mItemChangeListener = listener;
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
        Rect titleBounds = new Rect();
        mTextPaint.getTextBounds(mTextStr, 0, mTextStr.length(), titleBounds);

        mDigitalRect = new Rect();
        String digitalStr = getLongestItem();
        mTextPaint.getTextBounds(digitalStr, 0, digitalStr.length(), mDigitalRect);

        Rect dividerBounds = new Rect();
        mTextPaint.getTextBounds("-", 0, 1, dividerBounds);

        int totalWidth = mPaddingLeft + mPaddingRight + textRect.width() + mSpace;
        int totalHeight = mPaddingTop + mPaddingBottom + textRect.height();
        mBackRect = new Rect(mInitX - totalWidth / 2, mInitY, mInitX - totalWidth / 2 + totalWidth, mInitY + totalHeight);
        mTextPoint = new Point(mBackRect.left + mPaddingLeft + titleBounds.width() / 2, mBackRect.bottom - mPaddingBottom);
        mDigitalPoint = new Point(mBackRect.right - mPaddingRight - mDigitalRect.width() / 2, mTextPoint.y);
        int dividerX = mTextPoint.x + titleBounds.width() / 2 + ((mDigitalPoint.x - mDigitalRect.width() / 2) - (mTextPoint.x + titleBounds.width() / 2)) / 2;
        int dividerY = mBackRect.bottom - mBackRect.height() / 2;
        mDividerPoint = new Point(dividerX, dividerY);
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
        //canvas.drawText("-", mDividerPoint.x, mDividerPoint.y, mTextPaint);
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
                canvas.drawText(mItemList.get(i), digitalX, digitalY, mTextPaint);
            }
            digitalY += mDigitalRect.height() + mDigitalSpace;
        }
        //mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAlpha(255);
    }


    private void autoSettle() {
        Logger.d("autoSettle:check fling");
        if (mFling) {
            return;
        }
        int scrollY = calculateScrollY();
        Logger.d("autoSettle:start,scrollY:" + scrollY);
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

    public void setDispatchEveryChange(boolean enable) {
        this.mDispatchEveryChange = enable;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //Logger.d("computeScroll");
        if (mDispatchEveryChange) {
            callItemChangeListener();
        }
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
        if (mCenterYMap.size() == 0) {
            return 0;
        }
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

    public boolean onDown(MotionEvent e) {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        mFling = false;
        mIsPressed = true;
        removeCallbacks(mHideRunnable);
        return true;
    }

    public boolean onUp(MotionEvent event) {
        Logger.d("Action_up");
        mIsPressed = false;
        mInScroll = false;
        autoSettle();
        hideDelay();
        return false;
    }

    public void onShowPress(MotionEvent e) {

    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!mInScroll && Math.abs(e2.getY() - e1.getY()) < REL_SWIPE_MIN_DISTANCE) {
            return false;
        }
        if (!mIsPressed) {
            return false;
        }
        Logger.d("onScroll,distanceY:" + distanceY);
        mDeltaY -= distanceY * 0.6;
        if (mDeltaY > mMaxDeltaY + mOverScrollDis) {
            mDeltaY = mMaxDeltaY + mOverScrollDis;
        }
        if (mDeltaY < mMinDeltaY - mOverScrollDis) {
            mDeltaY = mMinDeltaY - mOverScrollDis;
        }
        invalidate();
        mInScroll = true;
        showWithAnimation();
        return true;
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Logger.d("onFling,velocityX:" + velocityX + ",velocityY:" + velocityY);
        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();
        if (!mInScroll && (Math.abs(diffX) > REL_SWIPE_MIN_DISTANCE && Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY)) {
            Logger.d("onFling,return");
            return false;
        }

        mFling = true;
        mScroller.fling(0, mDeltaY, 0, (int) velocityY, 0, 0, mMinDeltaY, mMaxDeltaY, 0, mDigitalRect.height());
        invalidate();
        showWithAnimation();
        return true;
    }

    public void disableDrag(boolean disable) {
        this.mDisable = disable;
    }

    public interface IItemChangeListener {
        void onItemChange(int index);
    }

    public void showWithAnimation() {
        if (getVisibility() == View.VISIBLE && !mIsHiding) {
            return;
        }
        mIsHiding = false;
        Logger.d("animation:showWIthAnimation");
        setVisibility(View.VISIBLE);
        setAlpha(0);
        animate().setDuration(10).alpha(1.0f).setListener(null).start();
    }

    public void hideDelay() {
        removeCallbacks(mHideRunnable);
        postDelayed(mHideRunnable, 1500);
    }

    private void hideWidthAnimation() {
        Logger.d("animation:hideWidthAnimation");
        if (getVisibility() != View.VISIBLE) {
            return;
        }
        mIsHiding = true;
        setVisibility(View.VISIBLE);
        setAlpha(1.0f);
        animate().alpha(0).setDuration(1000).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Logger.d("animation:onAnimationEnd");
                setVisibility(View.INVISIBLE);
                mIsHiding = false;
            }
        }).start();
    }

    private Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hideWidthAnimation();
        }
    };
}
