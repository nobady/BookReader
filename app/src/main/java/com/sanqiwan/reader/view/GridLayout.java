package com.sanqiwan.reader.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.sanqiwan.reader.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-10
 * Time: 下午12:37
 * To change this template use File | Settings | File Templates.
 */
public class GridLayout extends ViewGroup {
    private static final int DEFAULT_VALUE = 1;
    private static final int BORDER_WIDTH = 1;
    private int mColumnCount;
    private Context mContext;
    private boolean mDivideLastRow;
    private List<View> mVisibleChildren;

    public GridLayout(Context context) {
        this(context, null);
        mContext = context;
    }

    public GridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.GridLayout);
        //获取自定义属性和默认值
        mColumnCount = mTypedArray.getInteger(R.styleable.GridLayout_columns, DEFAULT_VALUE);
        mDivideLastRow = mTypedArray.getBoolean(R.styleable.GridLayout_divide_last_row, true);
        mTypedArray.recycle();
    }

    public void setColumnCount(int columnCount) {
        mColumnCount = columnCount;
        requestLayout();
    }

    public void setDivideLastRow(boolean divide) {
        mDivideLastRow = divide;
        requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top = BORDER_WIDTH;
        int left = BORDER_WIDTH;
        int childCount = mVisibleChildren.size();
        if (childCount == 0) {
            return;
        }
        if (mDivideLastRow && (childCount % mColumnCount > 0)) {
            childCount -= childCount % mColumnCount;
        }
        int childWidth = mVisibleChildren.get(0).getMeasuredWidth();
        int childHeight = mVisibleChildren.get(0).getLayoutParams().height;
        for (int i = 0; i < childCount; i++) {
            View v = mVisibleChildren.get(i);
            v.layout(left, top, left + childWidth, top + childHeight);
            if (i % mColumnCount < mColumnCount) {     //换列
                left = left + childWidth + BORDER_WIDTH;
            }
            if (i % mColumnCount == mColumnCount - 1) {//换行
                left = BORDER_WIDTH;
                top = top + childHeight + BORDER_WIDTH;
            }
        }

        if (childCount < mVisibleChildren.size()) {
            childWidth = mVisibleChildren.get(childCount).getMeasuredWidth();
            childHeight = mVisibleChildren.get(childCount).getLayoutParams().height;
        }
        for (int i = childCount; i < mVisibleChildren.size(); i++) {
            View v = mVisibleChildren.get(i);
            v.layout(left, top, left + childWidth, top + childHeight);
            if (childCount % mColumnCount < mColumnCount) {     //换列
                left = left + childWidth + BORDER_WIDTH;
            }

        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getVisibleChildren();
        measureChild(widthMeasureSpec);

        int measureWidth = measureWidth(widthMeasureSpec, mVisibleChildren.size());
        int measuredHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measuredHeight);
    }

    private void measureChild(int widthMeasureSpec) {
        int childCount = mVisibleChildren.size();
        int remainItems = childCount % mColumnCount;
        if (mDivideLastRow && (remainItems > 0)) {
            childCount -= remainItems;
        }
        int childSize = measureChildrenSize(widthMeasureSpec, mColumnCount);
        for (int i = 0; i < childCount; i++) {
            View child = mVisibleChildren.get(i);
            final LayoutParams lp = child.getLayoutParams();
            child.measure(MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY));
        }
        if (remainItems > 0) {
            childSize = measureChildrenSize(widthMeasureSpec, remainItems);
            for (int i = childCount; i < mVisibleChildren.size(); i++) {
                View child = mVisibleChildren.get(i);
                final LayoutParams lp = child.getLayoutParams();
                child.measure(MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY));
            }
        }
    }

    private int measureChildrenSize(int widthMeasureSpec, int columns) {
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        return (specSize - columns * BORDER_WIDTH - BORDER_WIDTH) / columns;
    }

    private int measureHeight(int measureSpec) {
        int count = mVisibleChildren.size();
        int rows = count / mColumnCount;
        if (count % mColumnCount > 0) {
            rows += 1;
        }
        int height = MeasureSpec.getSize(measureSpec);
        if (mVisibleChildren.size() > 0) {
            height = getChildAt(0).getMeasuredHeight();
        } else {
            return height;
        }
        return rows * height + rows * BORDER_WIDTH + BORDER_WIDTH;
    }

    private int measureWidth(int measureSpec, int visibleColumns) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int columnCount;
        if (visibleColumns >= mColumnCount) {
            columnCount = mColumnCount;
        } else {
            columnCount = visibleColumns;
        }
        int width = MeasureSpec.getSize(measureSpec);
        if (mVisibleChildren.size() > 0) {
            width = getChildAt(0).getMeasuredWidth();
        } else {
            return width;
        }
        return columnCount * width + columnCount * BORDER_WIDTH + BORDER_WIDTH;
    }

    private void getVisibleChildren() {
        if (mVisibleChildren == null) {
            mVisibleChildren = new ArrayList<View>();
        }
        if (mVisibleChildren.size() > 0) {
            mVisibleChildren.clear();
        }
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            if (child.getVisibility() != GONE) {
                mVisibleChildren.add(child);
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint paint = new Paint();
        //设置边框颜色.
        paint.setColor(mContext.getResources().getColor(R.color.payment_line));
        canvas.drawRect(new RectF(0, 0, getWidth(), getHeight()), paint);
        super.dispatchDraw(canvas);
    }
}
