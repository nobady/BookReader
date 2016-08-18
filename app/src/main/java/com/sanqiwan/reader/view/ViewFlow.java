/*
 * Copyright (C) 2011 Patrik Åkerfeldt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sanqiwan.reader.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.*;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;
import com.sanqiwan.reader.R;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A horizontally scrollable {@link android.view.ViewGroup} with items populated from an
 * {@link android.widget.Adapter}. The ViewFlow uses a buffer to store loaded {@link android.view.View}s in.
 * The default size of the buffer is 3 elements on both sides of the currently
 * visible {@link android.view.View}, making up a total buffer size of 3 * 2 + 1 = 7. The
 * buffer size can be changed using the {@code sidebuffer} xml attribute.
 */
public class ViewFlow extends AdapterView<ListAdapter> {

    private static final int SNAP_VELOCITY = 200;
    private static final int INVALID_SCREEN = -1;
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private static final int DEFAULT_SIDE_BUFFER = 3;
    private static final int DEFAULT_DURATION = 1000;
    private static final String LOG_TAG = "ViewFlow";

    private LinkedList<View> mLoadedViews;
    private int mCurrentBufferIndex;
    private int mCurrentAdapterIndex;
    private int mSideBuffer;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchState = TOUCH_STATE_REST;
    private float mLastMotionX, mLastMotionY;
    private int mTouchSlop;
    private int mMaximumVelocity;
    private int mCurrentScreen;
    private int mNextScreen = INVALID_SCREEN;
    private boolean mFirstLayout = true;
    private ViewSwitchListener mViewSwitchListener;
    private ListAdapter mAdapter;
    private int mLastScrollDirection;
    private AdapterDataSetObserver mDataSetObserver;
    private FlowIndicator mIndicator;
    private int mLastOrientation = -1;
    private long mTimeSpan = 3000;
    private Handler mHandler;
    private OnGlobalLayoutListener orientationChangeListener = new OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            getViewTreeObserver().removeGlobalOnLayoutListener(
                    orientationChangeListener);
            setSelection(mCurrentAdapterIndex);
        }
    };

    /**
     * Receives call backs when a new {@link android.view.View} has been scrolled to.
     */
    public static interface ViewSwitchListener {

        /**
         * This method is called when a new View has been scrolled to.
         *
         * @param view     the {@link android.view.View} currently in focus.
         * @param position The position in the adapter of the {@link android.view.View} currently
         *                 in focus.
         */
        void onSwitched(View view, int position);

    }

    public ViewFlow(Context context) {
        super(context);
        mSideBuffer = DEFAULT_SIDE_BUFFER;
        init();
    }

    public ViewFlow(Context context, int sideBuffer) {
        super(context);
        mSideBuffer = sideBuffer;
        init();
    }

    public ViewFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray styledAttrs = context.obtainStyledAttributes(attrs,
                R.styleable.ViewFlow);
        mSideBuffer = styledAttrs.getInt(R.styleable.ViewFlow_sidebuffer, DEFAULT_SIDE_BUFFER);
        init();
    }

    private void init() {
        mLoadedViews = new LinkedList<View>();
        mScroller = new Scroller(getContext());
        final ViewConfiguration configuration = ViewConfiguration
                .get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    public void startAutoFlowTimer() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int count = getChildCount();
                if (count > 0) {
                    snapToScreen((mCurrentScreen + 1) % count);
                }
                scheduleNextFlow();
            }
        };

        scheduleNextFlow();
    }

    private void scheduleNextFlow() {
        Message message = mHandler.obtainMessage(0);
        mHandler.sendMessageDelayed(message, mTimeSpan);
    }

    public void stopAutoFlowTimer() {
        if (mHandler != null)
            mHandler.removeMessages(0);
        mHandler = null;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation != mLastOrientation) {
            mLastOrientation = newConfig.orientation;
            getViewTreeObserver().addOnGlobalLayoutListener(
                    orientationChangeListener);
        }
    }

    public int getViewsCount() {
        return mSideBuffer;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY && !isInEditMode()) {
            throw new IllegalStateException(
                    "ViewFlow can only be used in EXACTLY mode.");
        }

        final int height = MeasureSpec.getSize(heightMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY && !isInEditMode()) {
            throw new IllegalStateException(
                    "ViewFlow can only be used in EXACTLY mode.");
        }

        // The children are given the same width and height as the workspace
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }

        if (mFirstLayout) {
            mScroller.startScroll(0, 0, mCurrentScreen * width, 0, 0);
            mFirstLayout = false;
        }
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth,
                        child.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getChildCount() == 0)
            return false;

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                /*
                 * If being flinged and user touches, stop the fling. isFinished
                 * will be false if being flinged.
                 */
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                // Remember where the motion event started
                mLastMotionX = x;
                mLastMotionY = y;

                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
                        : TOUCH_STATE_SCROLLING;
                if (mHandler != null)
                    mHandler.removeMessages(0);
                break;

            case MotionEvent.ACTION_MOVE:
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                final int yDiff = (int) Math.abs(y - mLastMotionY);

                boolean xMoved = xDiff > mTouchSlop;

                if (xMoved && xDiff > yDiff) {
                    // Scroll if the user moved far enough along the X axis
                    mTouchState = TOUCH_STATE_SCROLLING;
//                    ViewParent parent = getParent();
//                    if (parent != null) {
//                        parent.requestDisallowInterceptTouchEvent(true);
//                    }
                }

                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    // Scroll to follow the motion event
                    final int deltaX = (int) (mLastMotionX - x);
                    mLastMotionX = x;

                    final int scrollX = getScrollX();
                    if (deltaX < 0) {
                        if (scrollX > 0) {
                            scrollBy(Math.max(-scrollX, deltaX), 0);
                        }
                    } else if (deltaX > 0) {
                        final int availableToScroll = getChildAt(
                                getChildCount() - 1).getRight()
                                - scrollX - getWidth();
                        if (availableToScroll > 0) {
                            scrollBy(Math.min(availableToScroll, deltaX), 0);
                        }
                    }
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int velocityX = (int) velocityTracker.getXVelocity();

                    if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
                        // Fling hard enough to move left
                        snapToScreen(mCurrentScreen - 1);
                    } else if (velocityX < -SNAP_VELOCITY
                            && mCurrentScreen < getChildCount() - 1) {
                        // Fling hard enough to move right
                        snapToScreen(mCurrentScreen + 1);
                    } else {
                        snapToDestination();
                    }

                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                }

                mTouchState = TOUCH_STATE_REST;
                if (mHandler != null) {
                    scheduleNextFlow();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (getChildCount() == 0)
            return false;

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float x = ev.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                /*
                 * If being flinged and user touches, stop the fling. isFinished
                 * will be false if being flinged.
                 */
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                // Remember where the motion event started
                mLastMotionX = x;

                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
                        : TOUCH_STATE_SCROLLING;
                if (mHandler != null)
                    mHandler.removeMessages(0);

                break;

            case MotionEvent.ACTION_MOVE:
                final int xDiff = (int) Math.abs(x - mLastMotionX);

                boolean xMoved = xDiff > mTouchSlop;

                if (xMoved) {
                    // Scroll if the user moved far enough along the X axis
                    mTouchState = TOUCH_STATE_SCROLLING;
                }

                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    // Scroll to follow the motion event
                    final int deltaX = (int) (mLastMotionX - x);
                    mLastMotionX = x;

                    final int scrollX = getScrollX();
                    if (deltaX < 0) {
                        if (scrollX > 0) {
                            scrollBy(Math.max(-scrollX, deltaX), 0);
                        }
                    } else if (deltaX > 0) {
                        final int availableToScroll = getChildAt(
                                getChildCount() - 1).getRight()
                                - scrollX - getWidth();
                        if (availableToScroll > 0) {
                            scrollBy(Math.min(availableToScroll, deltaX), 0);
                        }
                    }
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int velocityX = (int) velocityTracker.getXVelocity();

                    if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
                        // Fling hard enough to move left
                        snapToScreen(mCurrentScreen - 1);
                    } else if (velocityX < -SNAP_VELOCITY
                            && mCurrentScreen < getChildCount() - 1) {
                        // Fling hard enough to move right
                        snapToScreen(mCurrentScreen + 1);
                    }
                    // else if (velocityX < -SNAP_VELOCITY
                    // && mCurrentScreen == getChildCount() - 1) {
                    // snapToScreen(0);
                    // }
                    // else if (velocityX > SNAP_VELOCITY
                    // && mCurrentScreen == 0) {
                    // snapToScreen(getChildCount() - 1);
                    // }
                    else {
                        snapToDestination();
                    }

                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                }

                mTouchState = TOUCH_STATE_REST;

                if (mHandler != null) {
                    scheduleNextFlow();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                snapToDestination();
                mTouchState = TOUCH_STATE_REST;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int h, int v, int oldh, int oldv) {
        super.onScrollChanged(h, v, oldh, oldv);
        if (mIndicator != null) {
            /*
             * The actual horizontal scroll origin does typically not match the
             * perceived one. Therefore, we need to calculate the perceived
             * horizontal scroll origin here, since we use a com.sanqiwan.view buffer.
             */
            int hPerceived = h + (mCurrentAdapterIndex - mCurrentBufferIndex)
                    * getWidth();
            mIndicator.onScrolled(hPerceived, v, oldh, oldv);
        }
    }

    private void snapToDestination() {
        final int screenWidth = getWidth();
        final int whichScreen = (getScrollX() + (screenWidth / 2))
                / screenWidth;

        snapToScreen(whichScreen);
    }

    private void snapToScreen(int whichScreen) {
        mLastScrollDirection = whichScreen - mCurrentScreen;
        if (!mScroller.isFinished())
            return;

        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));

        mNextScreen = whichScreen;

        final int newScrollX = whichScreen * getWidth();
        final int delta = newScrollX - getScrollX();
        mScroller.startScroll(getScrollX(), 0, delta, 0, DEFAULT_DURATION);
        invalidate();
    }

    @Override
    public void computeScroll() {
        boolean scrolling = mScroller.computeScrollOffset();
        if (scrolling) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        } else if (mNextScreen != INVALID_SCREEN) {
            mCurrentScreen = Math.max(0,
                    Math.min(mNextScreen, getChildCount() - 1));
            mNextScreen = INVALID_SCREEN;
//            postViewSwitched(mLastScrollDirection);

        }
    }

    /**
     * Scroll to the {@link android.view.View} in the view buffer specified by the index.
     *
     * @param indexInBuffer Index of the view in the view buffer.
     */
    private void setVisibleView(int indexInBuffer, boolean uiThread) {
        mCurrentScreen = Math.max(0,
                Math.min(indexInBuffer, getChildCount() - 1));
        int dx = (mCurrentScreen * getWidth()) - mScroller.getCurrX();
        if (dx != 0) {
            mScroller.startScroll(mScroller.getCurrX(), mScroller.getCurrY(), dx,
                    0, 0);
        }

        if (dx == 0)
            onScrollChanged(mScroller.getCurrX() + dx, mScroller.getCurrY(),
                    mScroller.getCurrX() + dx, mScroller.getCurrY());
        if (uiThread)
            invalidate();
        else
            postInvalidate();
    }

    /**
     * Set the listener that will receive notifications every time the {code
     * ViewFlow} scrolls.
     *
     * @param l the scroll listener
     */
    public void setOnViewSwitchListener(ViewSwitchListener l) {
        mViewSwitchListener = l;
    }

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        setAdapter(adapter, 0);
    }

    public void setAdapter(ListAdapter adapter, int initialPosition) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        mAdapter = adapter;

        if (mAdapter != null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);

        }
        if (mAdapter == null || mAdapter.getCount() == 0)
            return;
        setSideBuffer(mAdapter.getCount());
        setSelection(initialPosition);
    }

    @Override
    public View getSelectedView() {
        return (mCurrentBufferIndex < mLoadedViews.size() ? mLoadedViews
                .get(mCurrentBufferIndex) : null);
    }

    @Override
    public int getSelectedItemPosition() {
        return mCurrentAdapterIndex;
    }

    /**
     * Set the FlowIndicator
     *
     * @param flowIndicator
     */
    public void setFlowIndicator(FlowIndicator flowIndicator) {
        mIndicator = flowIndicator;
        mIndicator.setViewFlow(this);
    }

    @Override
    public void setSelection(int position) {
        mNextScreen = INVALID_SCREEN;
        mScroller.forceFinished(true);
        if (mAdapter == null)
            return;

        position = Math.max(position, 0);
        position = Math.min(position, mAdapter.getCount() - 1);

        ArrayList<View> recycleViews = new ArrayList<View>();
        View recycleView;
        while (!mLoadedViews.isEmpty()) {
            recycleViews.add(recycleView = mLoadedViews.remove());
            detachViewFromParent(recycleView);
        }

        View currentView = makeAndAddView(position, true,
                (recycleViews.isEmpty() ? null : recycleViews.remove(0)));
        mLoadedViews.addLast(currentView);

        for (int offset = 1; offset <= mSideBuffer; offset++) {
            int leftIndex = position - offset;
            int rightIndex = position + offset;
            if (leftIndex >= 0)
                mLoadedViews
                        .addFirst(makeAndAddView(
                                leftIndex,
                                false,
                                (recycleViews.isEmpty() ? null : recycleViews
                                        .remove(0))));
            if (rightIndex < mAdapter.getCount())
                mLoadedViews
                        .addLast(makeAndAddView(rightIndex, true, (recycleViews
                                .isEmpty() ? null : recycleViews.remove(0))));
        }

        mCurrentBufferIndex = mLoadedViews.indexOf(currentView);
        mCurrentAdapterIndex = position;

        for (View view : recycleViews) {
            removeDetachedView(view, false);
        }
        requestLayout();
        setVisibleView(mCurrentBufferIndex, false);
        if (mIndicator != null) {
            mIndicator.onSwitched(mLoadedViews.get(mCurrentBufferIndex),
                    mCurrentAdapterIndex);
        }
        if (mViewSwitchListener != null) {
            mViewSwitchListener
                    .onSwitched(mLoadedViews.get(mCurrentBufferIndex),
                            mCurrentAdapterIndex);
        }
    }

    private void resetFocus() {
        mLoadedViews.clear();
        removeAllViewsInLayout();

        for (int i = Math.max(0, mCurrentAdapterIndex - mSideBuffer); i < Math
                .min(mAdapter.getCount(), mCurrentAdapterIndex + mSideBuffer
                        + 1); i++) {
            mLoadedViews.addLast(makeAndAddView(i, true, null));
            if (i == mCurrentAdapterIndex)
                mCurrentBufferIndex = mLoadedViews.size() - 1;
        }
        requestLayout();
    }

    private void postViewSwitched(int direction) {
        if (direction == 0)
            return;

        if (direction > 0) { // to the right
            mCurrentAdapterIndex++;
            mCurrentBufferIndex++;

            // if(direction > 1) {
            // mCurrentAdapterIndex += mAdapter.getCount() - 2;
            // mCurrentBufferIndex += mAdapter.getCount() - 2;
            // }

            View recycleView = null;

            // Remove view outside buffer range
            if (mCurrentAdapterIndex > mSideBuffer) {
                recycleView = mLoadedViews.removeFirst();
                detachViewFromParent(recycleView);
                // removeView(recycleView);
                mCurrentBufferIndex--;
            }

            // Add view to buffer
            int newBufferIndex = mCurrentAdapterIndex + mSideBuffer;
            if (newBufferIndex < mAdapter.getCount())
                mLoadedViews.addLast(makeAndAddView(newBufferIndex, true,
                        recycleView));

        } else { // to the left
            mCurrentAdapterIndex--;
            mCurrentBufferIndex--;

            // if(direction < -1) {
            // mCurrentAdapterIndex -= mAdapter.getCount() - 2;
            // mCurrentBufferIndex -= mAdapter.getCount() - 2;
            // }

            View recycleView = null;

            // Remove view outside buffer range
            if (mAdapter.getCount() - 1 - mCurrentAdapterIndex > mSideBuffer) {
                recycleView = mLoadedViews.removeLast();
                detachViewFromParent(recycleView);
            }

            // Add new view to buffer
            int newBufferIndex = mCurrentAdapterIndex - mSideBuffer;
            if (newBufferIndex > -1) {
                mLoadedViews.addFirst(makeAndAddView(newBufferIndex, false,
                        recycleView));
                mCurrentBufferIndex++;
            }
        }

        requestLayout();
        setVisibleView(mCurrentBufferIndex, true);

        if (mIndicator != null) {
            mIndicator.onSwitched(mLoadedViews.get(mCurrentBufferIndex),
                    mCurrentAdapterIndex);
        }
        if (mViewSwitchListener != null) {
            mViewSwitchListener
                    .onSwitched(mLoadedViews.get(mCurrentBufferIndex),
                            mCurrentAdapterIndex);
        }
    }

    private View setupChild(View child, boolean addToEnd, boolean recycle) {
        LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new AbsListView.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT, 0);
        }
        if (recycle)
            attachViewToParent(child, (addToEnd ? -1 : 0), p);
        else
            addViewInLayout(child, (addToEnd ? -1 : 0), p, true);
        return child;
    }

    private View makeAndAddView(int position, boolean addToEnd, View convertView) {
        View view = mAdapter.getView(position, convertView, this);
        return setupChild(view, addToEnd, convertView != null);
    }

    class AdapterDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            View v = getChildAt(mCurrentBufferIndex);
            if (v != null) {
                for (int index = 0; index < mAdapter.getCount(); index++) {
                    if (v.equals(mAdapter.getItem(index))) {
                        mCurrentAdapterIndex = index;
                        break;
                    }
                }
            }
            resetFocus();
        }

        @Override
        public void onInvalidated() {
            // Not yet implemented!
        }

    }

    public void setTimeSpan(long timeSpan) {
        this.mTimeSpan = timeSpan;
    }

    public void setSideBuffer(int sideBuffer) {
        this.mSideBuffer = sideBuffer;
    }
}
