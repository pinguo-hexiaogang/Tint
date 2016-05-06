package com.okry.newstuff.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.okry.newstuff.R;

public class PanelView extends FrameLayout {

    final static String TAG = "PanelView";

    final static int CENTER_TOP = 1;
    final static int LEFT_CENTER = 3;
    final static int RIGHT_CENTER = 4;
    final static int CENTER_BOTTOM = 6;

    private int mColumns;

    private float mArrowHeight;
    private int mArrowAngle;
    private int mArrowPosition;
    private int mArrowColor;


    private float mSeparatorSize;
    private boolean mRowSeparatorEnabled;
    private boolean mColumnSeparatorEnabled;
    private int mSeparatorColor;
    private float mSeparatorPaddingLeftRight;
    private float mSeparatorPaddingTopBottom;
    private int mEveryHeight;
    private int mEveryWidth;
    private int mRoundCornerRadius;

    private Paint mPaint;
    private Path mArrowPath;

    public PanelView(Context context) {
        super(context);
        initValues();
        prepareViews();
    }

    public PanelView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.CustomPanelStyle);
    }

    public PanelView(Context context, AttributeSet attributeSet, int defaultStyle) {
        super(context, attributeSet, defaultStyle);
        initValues();
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.panelStyle, defaultStyle, R.style.defaultPanelStyle);
        mColumns = a.getInteger(R.styleable.panelStyle_column_count, 5);
        mArrowHeight = a.getDimension(R.styleable.panelStyle_arrow_height, 10);
        mArrowAngle = a.getInteger(R.styleable.panelStyle_arrow_angle, 45);
        mSeparatorSize = a.getDimension(R.styleable.panelStyle_separator_size, 1);
        mRowSeparatorEnabled = a.getBoolean(R.styleable.panelStyle_row_separator, true);
        mColumnSeparatorEnabled = a.getBoolean(R.styleable.panelStyle_column_separator, true);
        mSeparatorColor = a.getColor(R.styleable.panelStyle_separator_color, Color.WHITE);
        mSeparatorPaddingLeftRight = a.getDimension(R.styleable.panelStyle_separator_paddingLeftRight, 5);
        mSeparatorPaddingTopBottom = a.getDimension(R.styleable.panelStyle_separator_paddingTopBottom, 5);
        mArrowHeight = a.getDimension(R.styleable.panelStyle_arrow_height, 20);
        mArrowAngle = a.getInteger(R.styleable.panelStyle_arrow_angle, 45);
        mArrowPosition = a.getInt(R.styleable.panelStyle_arrow_position, 1);
        mArrowColor = a.getColor(R.styleable.panelStyle_arrow_background, Color.WHITE);
        a.recycle();

        prepareViews();
    }

    private void initValues() {
        mColumns = 2;
        mArrowHeight = 100;
        mArrowAngle = 45;

        mSeparatorSize = 5 * getContext().getResources().getDisplayMetrics().density;
        mRoundCornerRadius = (int) (3 * getContext().getResources().getDisplayMetrics().density);
        mRowSeparatorEnabled = true;
        mColumnSeparatorEnabled = true;
        mSeparatorColor = Color.WHITE;
    }


    private void prepareViews() {
        mPaint = new Paint();
        mPaint.setColor(mArrowColor);
        mPaint.setStrokeWidth(1);
        mArrowPath = new Path();

        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalArgumentException("width should be exactly");
        }
        int showChildCounts = getShowChildCounts();
        if (showChildCounts == 0) {
            setMeasuredDimension(width, getSuggestedMinimumHeight());
            return;
        }


        int everyWidth = (width - getPaddingLeft() - getPaddingRight()) / mColumns;
        int maxEveryHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int heightSpec = getChildMeasureSpec(heightMeasureSpec, 0, child.getLayoutParams().height);
                int heigh1 = MeasureSpec.getSize(heightSpec);
                int widthSpec = MeasureSpec.makeMeasureSpec(everyWidth, MeasureSpec.EXACTLY);
                child.measure(widthSpec, heightSpec);
                maxEveryHeight = Math.max(maxEveryHeight, child.getMeasuredHeight());
            }
        }
        mEveryHeight = maxEveryHeight;
        mEveryWidth = everyWidth;
        int measureHeight = (int) Math.ceil(showChildCounts * 1.0f / mColumns) * maxEveryHeight + (int) mArrowHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(width, measureHeight);
    }

    private int getShowChildCounts() {
        int counts = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view.getVisibility() != View.GONE) {
                counts++;
            }

        }
        return counts;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int columnIndex = 1;
        int rowIndex = 1;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int childLeft = getPaddingLeft() + mEveryWidth * (columnIndex - 1);
                int childTop = (int) mArrowHeight + getPaddingTop() + mEveryHeight * (rowIndex - 1);
                child.layout(childLeft, childTop, childLeft + mEveryWidth, childTop + mEveryHeight);
                columnIndex++;
                if (columnIndex > mColumns) {
                    columnIndex = 1;
                    rowIndex++;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArrow(canvas);
        drawBack(canvas);
    }

    private void drawBack(Canvas canvas) {
        RectF rectF = new RectF(0, mArrowHeight, getWidth(), getHeight());
        canvas.drawRoundRect(rectF, (float) mRoundCornerRadius, (float) mRoundCornerRadius, mPaint);
    }

    private void drawArrow(Canvas canvas) {
        Log.d(TAG, "arrow_height=" + mArrowHeight);
        Log.d(TAG, "arrow_position=" + mArrowPosition);

        float x = getPaddingLeft();
        float y = getPaddingTop();
        float center_x = getWidth() / 2;
        float center_y = getHeight() / 2;

        float dist = mArrowHeight / (float) Math.tan(mArrowAngle * Math.PI / 180);

        if (mArrowPosition == CENTER_TOP) {
            mArrowPath.reset();
            mArrowPath.moveTo(getWidth() / 2 - dist, mArrowHeight);
            mArrowPath.lineTo(getWidth() / 2, 0);
            mArrowPath.lineTo(getWidth() / 2 + dist, mArrowHeight);
            mArrowPath.close();
            canvas.drawPath(mArrowPath, mPaint);

        } else if (mArrowPosition == CENTER_BOTTOM) {
            mArrowPath.reset();
            mArrowPath.moveTo(getWidth() / 2 - dist, getHeight() - mArrowHeight);
            mArrowPath.lineTo(getWidth() / 2, getHeight());
            mArrowPath.lineTo(getWidth() / 2 + dist, getHeight() - mArrowHeight);
            mArrowPath.close();
            canvas.drawPath(mArrowPath, mPaint);

        } else if (mArrowPosition == LEFT_CENTER) {
            mArrowPath.reset();
            mArrowPath.moveTo(dist, getHeight() / 2 - mArrowHeight);
            mArrowPath.lineTo(0, getHeight() / 2);
            mArrowPath.lineTo(dist, getHeight() / 2 + mArrowHeight);
            mArrowPath.close();
            canvas.drawPath(mArrowPath, mPaint);

        } else if (mArrowPosition == RIGHT_CENTER) {
            mArrowPath.reset();
            mArrowPath.moveTo(getWidth() - dist, getHeight() / 2 - mArrowHeight);
            mArrowPath.lineTo(getWidth(), getHeight() / 2);
            mArrowPath.lineTo(getWidth() - dist, getHeight() / 2 + mArrowHeight);
            mArrowPath.close();
            canvas.drawPath(mArrowPath, mPaint);
        }
    }

    public static class PanelItem {
        private int mLabel;
        private int mIcon;

        public PanelItem(int captionId, int iconId) {
            mLabel = captionId;
            mIcon = iconId;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int i);
    }
}
