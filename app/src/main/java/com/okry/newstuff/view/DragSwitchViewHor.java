package com.okry.newstuff.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import com.okry.newstuff.R;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hexiaogang on 11/12/15.
 */
public class DragSwitchViewHor extends View implements GestureDetector.OnGestureListener {
    private Paint mTextPaint;
    private int mTextSelectedColor;
    private int mTextNormalColor;
    private boolean mDisable = false;

    private GestureDetectorCompat mGestureDetector;
    private OverScroller mScroller;

    private boolean mFling = false;
    private boolean mIsPressed = false;
    private int mTextSize;
    private int mPaddingTop;
    private int mPaddingBottom;
    private int mItemSpace;

    private IItemChangeListener mItemChangeListener = null;
    private static int REL_SWIPE_MIN_DISTANCE;
    private static int REL_SWIPE_MAX_OFF_PATH;
    private static int REL_SWIPE_THRESHOLD_VELOCITY;
    private String[] mItemsStrArray = {"a", "b", "c"};
    private List<ItemInfo> mItemInfoList = new ArrayList<>();
    private int mInitX;
    private int mScrollX = 0;
    private int mMaxScrollX = 0;
    private int mMaxOverScrollX = 0;
    private int mLastSelectIndex = -1;
    private boolean mClickAble = false;
    //点击滚动,就不调用autoSettle了
    private boolean mClickScrolling = false;


    public DragSwitchViewHor(Context context) {
        super(context);
        init();
    }

    public DragSwitchViewHor(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, 0);
        init();
    }

    public DragSwitchViewHor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DragSwitchViewHor(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs, defStyleAttr);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetectorCompat(getContext(), this);
        mGestureDetector.setIsLongpressEnabled(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextNormalColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

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
                R.styleable.HorDragSwitchView,
                defStyle,
                0);
        mTextSelectedColor = ta.getColor(R.styleable.HorDragSwitchView_hor_selected_color, Color.WHITE);
        mTextNormalColor = ta.getColor(R.styleable.HorDragSwitchView_hor_normal_color, Color.DKGRAY);
        mTextSize = (int) ta.getDimension(R.styleable.HorDragSwitchView_hor_text_size, 15 * getResources().getDisplayMetrics().density);
        mPaddingTop = (int) ta.getDimension(R.styleable.HorDragSwitchView_hor_padding_top, 10 * getResources().getDisplayMetrics().density);
        mPaddingBottom = (int) ta.getDimension(R.styleable.HorDragSwitchView_hor_padding_bottom, 10 * getResources().getDisplayMetrics().density);
        mItemSpace = (int) ta.getDimension(R.styleable.HorDragSwitchView_hor_item_space, 40 * getResources().getDisplayMetrics().density);
        mMaxOverScrollX = (int) ta.getDimension(R.styleable.HorDragSwitchView_hor_max_over_scroll, 20 * getResources().getDisplayMetrics().density);
        mClickAble = ta.getBoolean(R.styleable.HorDragSwitchView_hor_clickable, false);
        int itemsId = ta.getResourceId(R.styleable.HorDragSwitchView_hor_items, 0);

        TypedArray itemsTypedArray = getResources().obtainTypedArray(itemsId);
        if (itemsTypedArray != null) {
            mItemsStrArray = new String[itemsTypedArray.length()];
            for (int i = 0; i < itemsTypedArray.length(); i++) {
                int stringId = itemsTypedArray.getResourceId(i, 0);
                String item = getResources().getString(stringId);
                mItemsStrArray[i] = item;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = mPaddingBottom + mPaddingTop + getMaxTextHeight();
        int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }

    private int getMaxTextHeight() {
        int maxTextHeight = 0;
        for (int i = 0; i < mItemsStrArray.length; i++) {
            Rect bounds = new Rect();
            mTextPaint.getTextBounds(mItemsStrArray[i], 0, mItemsStrArray[i].length(), bounds);
            if (bounds.height() > maxTextHeight) {
                maxTextHeight = bounds.height();
            }
        }
        return maxTextHeight;
    }

    public void setItemChangeListener(IItemChangeListener listener) {
        this.mItemChangeListener = listener;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mItemInfoList.clear();
        mInitX = w / 2;
        int maxTextHeight = getMaxTextHeight();
        Rect firstStrBounds = new Rect();
        mTextPaint.getTextBounds(mItemsStrArray[0], 0, mItemsStrArray[0].length(), firstStrBounds);
        int startX = mInitX - firstStrBounds.width() / 2;
        for (int i = 0; i < mItemsStrArray.length; i++) {
            String string = mItemsStrArray[i];
            ItemInfo itemInfo = new ItemInfo();
            mItemInfoList.add(itemInfo);
            itemInfo.index = i;
            itemInfo.str = string;
            mTextPaint.getTextBounds(itemInfo.str, 0, itemInfo.str.length(), itemInfo.bounds);
            itemInfo.point.y = maxTextHeight + mPaddingTop;
            int preIndex = i - 1;
            if (preIndex < 0) {
                //第一个
                itemInfo.point.x = startX;
            } else {
                ItemInfo preInfo = mItemInfoList.get(preIndex);
                itemInfo.point.x = preInfo.point.x + preInfo.bounds.width() + mItemSpace;
            }
        }
        ItemInfo lastItem = mItemInfoList.get(mItemInfoList.size() - 1);
        mMaxScrollX = lastItem.point.x - mInitX + lastItem.bounds.width() / 2;

    }

    public void setItemStringArray(String[] itemStringArray) {
        if (itemStringArray != null && itemStringArray.length != 0) {
            mItemsStrArray = itemStringArray;
        }
        requestLayout();
    }

    public void setCurrentIndex(final int currentIndex) {
        if (currentIndex < 0 && currentIndex >= mItemsStrArray.length) {
            return;
        }
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                removeOnLayoutChangeListener(this);
                int scrollX = calculateScrollXByIndex(currentIndex);
                mScrollX = scrollX;
                invalidate();
            }
        });
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int currentIndex = calculateCurrentIndex();
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.translate(-mScrollX, 0);
        for (ItemInfo itemInfo : mItemInfoList) {
            if (itemInfo.index == currentIndex) {
                mTextPaint.setColor(mTextSelectedColor);
            } else {
                mTextPaint.setColor(mTextNormalColor);
            }
            mTextPaint.setAlpha(calAlphaBy(currentIndex, itemInfo.index));
            canvas.drawText(itemInfo.str, itemInfo.point.x, itemInfo.point.y, mTextPaint);
        }
        canvas.restore();
        mTextPaint.setAlpha(255);
    }

    /**
     * 当前选中的alpha为255;离当前选中的越远alpha越小
     *
     * @param currentIndex 当前选中的index
     * @param index        准备画的index
     * @return
     */
    private int calAlphaBy(int currentIndex, int index) {
        int dis = Math.abs(currentIndex - index);
        int alpha = 255 - dis * 80;
        if (alpha < 50) {
            alpha = 50;
        }
        return alpha;
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
            autoSettle();
        }
        return ret;
    }

    private void autoSettle() {
        if (mFling || mClickScrolling) {
            return;
        }
        int scrollX = calculateScrollXByIndex(calculateCurrentIndex());
        mScroller.startScroll(mScrollX, 0, scrollX - mScrollX, 0, 500);
        invalidate();
        Logger.d("autoSettle scroll,mScrollX:" + mScrollX + ",scrollX:" + scrollX);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //L.d("computeScroll");
        if (mScroller.computeScrollOffset()) {
            mScrollX = mScroller.getCurrX();
            //Logger.d("mDeltaY is:" + mDeltaY);
            invalidate();
        } else {
            if (mFling) {
                mFling = false;
                autoSettle();
            } else {
                if (mClickScrolling) {
                    mClickScrolling = false;
                }
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
            if (newIndex >= 0 && newIndex <= mItemInfoList.size() - 1 && mLastSelectIndex != newIndex) {
                Logger.d("call onItemChange,the selected item is:" + newIndex);

                mLastSelectIndex = newIndex;
                mItemChangeListener.onItemChange(newIndex);
            }
        }
    }

    private int calculateCurrentIndex() {
        int minDisToCenter = mInitX;
        int currentIndex = 0;
        int scrollX = mScrollX;
        for (int i = 0; i < mItemInfoList.size(); i++) {
            ItemInfo itemInfo = mItemInfoList.get(i);
            int curItemX = itemInfo.point.x - scrollX;
            //字符串包括中点
            if (curItemX <= mInitX && curItemX + itemInfo.bounds.width() >= mInitX) {
                return i;
            } else {
                int disToCenter = 0;
                if (curItemX + itemInfo.bounds.width() <= mInitX) {
                    //在中点左边
                    disToCenter = Math.abs(mInitX - (curItemX + itemInfo.bounds.width()));
                } else {
                    //在中点右边
                    disToCenter = Math.abs(mInitX - curItemX);
                }
                if (minDisToCenter > disToCenter) {
                    minDisToCenter = disToCenter;
                    currentIndex = i;
                }
            }
        }
        Logger.d("currentIndex is:" + currentIndex + ",scrollX:" + scrollX + ",mscrollX:" + mScrollX);
        return currentIndex;
    }

    private int calculateScrollXByIndex(final int index) {
        int scrollX = 0;
        if (index == 0) {
            return 0;
        }
        for (int i = 0; i <= index; i++) {
            if (i == 0) {
                scrollX = mItemInfoList.get(i).bounds.width() / 2;
            } else if (i == index) {
                scrollX = scrollX + mItemInfoList.get(i).bounds.width() / 2 + mItemSpace;
            } else {
                scrollX += mItemInfoList.get(i).bounds.width() + mItemSpace;
            }

        }
        Logger.d("calculateScrollXByIndex," + "currentIndex:" + index + ",scrollX:" + scrollX);
        return scrollX;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        mFling = false;
        mIsPressed = true;
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (mClickAble) {
            int clickX = (int) e.getX();
            int clickIndex = calculateIndexByX(clickX);
            if (clickIndex != -1 && clickIndex < mItemInfoList.size()) {
                int scrollX = calculateScrollXByIndex(clickIndex);
                mClickScrolling = true;
                mScroller.startScroll(mScrollX, 0, scrollX - mScrollX, 0, 500);
                Logger.d("onSingleTapUp scroll,mScrollX:" + mScrollX + ",scrollX:" + scrollX + ",click index:" + clickIndex);
                invalidate();
                return true;
            }
        }
        return false;
    }

    /**
     * 根据给出的X的位置,计算选中贴纸的index
     *
     * @param x
     * @return
     */
    private int calculateIndexByX(int x) {
        for (int i = 0; i < mItemInfoList.size(); i++) {
            ItemInfo itemInfo = mItemInfoList.get(i);
            int itemX = itemInfo.point.x - mScrollX;
            int right;
            int left;
            right = itemX + itemInfo.bounds.width() + mItemSpace / 2;
            left = itemX - mItemSpace / 2;
            if (x >= left && x <= right) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Logger.d("onScroll,distanceX:" + distanceX);
       /* if (Math.abs(e2.getX() - e1.getX()) < REL_SWIPE_MIN_DISTANCE) {
            return false;
        }*/
       /* if (!mIsPressed) {
            return false;
        }*/
        mScrollX += distanceX;
        if (mScrollX > mMaxScrollX + mMaxOverScrollX) {
            mScrollX = mMaxScrollX + mMaxOverScrollX;
        } else if (mScrollX < -mMaxOverScrollX) {
            mScrollX = -mMaxOverScrollX;
        }
        invalidate();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Logger.d("onFling");
        mFling = true;
        mScroller.fling(mScrollX, 0, (int) -velocityX, 0, 0, mMaxScrollX, 0, 0, mMaxOverScrollX, 0);
        invalidate();
        return true;
    }

    public void disableDrag(boolean disable) {
        this.mDisable = disable;
    }

    public interface IItemChangeListener {
        void onItemChange(int index);
    }

    public static class ItemInfo {
        Point point = new Point();
        Rect bounds = new Rect();
        String str;
        int index;
    }
}
