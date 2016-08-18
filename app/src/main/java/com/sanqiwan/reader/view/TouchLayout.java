package com.sanqiwan.reader.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/13/13
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class TouchLayout extends FrameLayout {

    public interface TouchCenterListener {
        public void onTouchCenter();
    }

    private View mExceptionView;
    public TouchLayout(Context context) {
        super(context);
    }

    public TouchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private TouchCenterListener mTouchCenterListener;
    private GestureDetector mGestureDetector = new GestureDetector(new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (isInside(e.getX(), e.getY()) && mTouchCenterListener != null) {
                mTouchCenterListener.onTouchCenter();
                return true;
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    });

    public void setTouchCenterListener(TouchCenterListener listener) {
        mTouchCenterListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!isInExceptionView(event) && mGestureDetector.onTouchEvent(event)) {
            event.setAction(MotionEvent.ACTION_CANCEL);
            return super.dispatchTouchEvent(event);
        }
        return super.dispatchTouchEvent(event);
    }

    public boolean isInside(float x, float y) {
        float centerLeft = getWidth() / 4;
        float centerRight = getWidth() * 3 / 4;
        float centerTop = getHeight() / 4;
        float centerBottom = getHeight() * 3 / 4;
        if (x >= centerLeft && x <= centerRight && y >= centerTop && y <= centerBottom) {
            return true;
        }
        return false;
    }

    private Rect mTempRect = new Rect();

    public boolean isInExceptionView(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (mExceptionView != null && mExceptionView.getVisibility() == VISIBLE) {
            mExceptionView.getHitRect(mTempRect);
            return mTempRect.contains(x, y);
        }
        return false;
    }

    public void setExceptionView(View view) {
        mExceptionView = view;
    }
}
