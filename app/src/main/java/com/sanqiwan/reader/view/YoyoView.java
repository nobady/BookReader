package com.sanqiwan.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.BounceInterpolator;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.util.UIUtil;

/**
 * Created by chen on 12/16/13.
 */
public class YoyoView extends View implements Animator.AnimatorListener {

    private static final boolean DEBUG = true;
    private static final String TAG = "YoyoView";
    private static final float COEFFICIENT = 0.8f;
    private static final int CLICK_BOUNCE_DISTANCE = UIUtil.dipToPixel(30);
    private static final int DEMO_BOUNCE_DISTANCE = UIUtil.dipToPixel(100);
    private static final int DURATION = 500;
    private static final int CLICK_DURATION = 300;
    private static final int DEMO_DURATION = 800;
    public static final int EXTEND_HEIGHT_TIME = 3;
    private Drawable mYoyo;
    private float mLastMotionX;
    private float mLastMotionY;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private int mTouchSlop;
    private boolean mIsBeingDragged;
    private Animator mAnimator;
    private int mBounceDistance;
    private boolean mCanPerformClick;
    private Rect mYoyoBound;

    public YoyoView(Context context) {
        super(context);
        init(context);
    }

    public YoyoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setClickable(true);
        mAnimator = new Animator();
        mAnimator.setAnimatorListener(this);
        mAnimator.setDuration(DURATION);
        mAnimator.setInterpolator(new BounceInterpolator());
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mYoyo = context.getResources().getDrawable(R.drawable.yoyo);
//        int current = -(int) (mYoyo.getIntrinsicHeight() * 0.55f);
        int current = 0;
        mYoyoBound = new Rect(0, current, mYoyo.getIntrinsicWidth(), current + mYoyo.getIntrinsicHeight());
        mYoyo.setBounds(mYoyoBound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            widthMode = MeasureSpec.EXACTLY;
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            heightMode = MeasureSpec.EXACTLY;
        }

        widthSize = mYoyo.getIntrinsicWidth();
        heightSize = mYoyo.getIntrinsicHeight();

        heightSize *= EXTEND_HEIGHT_TIME;

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(widthSize, widthMode),
                MeasureSpec.makeMeasureSpec(heightSize, heightMode));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mYoyo.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        float x = event.getX();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = mInitialMotionX = x;
                mLastMotionY = mInitialMotionY = y;
                if (mYoyoBound.contains((int) x, (int) y)) {
                    mCanPerformClick = false;
                    mAnimator.stop();
                    return true;
                } else {
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                if (!mIsBeingDragged) {
                    final float xDiff = Math.abs(x - mLastMotionX);
                    final float yDiff = Math.abs(y - mLastMotionY);

                    if (yDiff > mTouchSlop && yDiff > xDiff) {
                        mIsBeingDragged = true;
                        mLastMotionY = y - mInitialMotionY > 0 ? mInitialMotionY + mTouchSlop :
                                mInitialMotionY - mTouchSlop;
                        mLastMotionX = x;
                    }
                }
                if (mIsBeingDragged) {
                    performDrag(y);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mIsBeingDragged) {
                    mAnimator.setFrom(CLICK_BOUNCE_DISTANCE);
                    mAnimator.setTo(0);
                    mAnimator.setDuration(CLICK_DURATION);
                    mAnimator.start();
                } else {
                    mAnimator.setFrom(mYoyo.getBounds().bottom - mYoyoBound.bottom);
                    mAnimator.setTo(0);
                    mAnimator.setDuration(DURATION);
                    mAnimator.start();
                }
                mCanPerformClick = true;
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                resetYoyo();
                break;

        }

        return true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                demo();
            }
        }, 500);
    }

    public void demo() {
        mBounceDistance = CLICK_BOUNCE_DISTANCE;
        mAnimator.setDuration(DEMO_DURATION);
        mAnimator.start();
    }

    private void resetYoyo() {
        mYoyo.setBounds(mYoyoBound);
        invalidate();
    }

    private void performDrag(float y) {
        mLastMotionY = y;
        int current = (int) Math.pow(mLastMotionY - mInitialMotionY, COEFFICIENT);
        Rect bounds = mYoyoBound;
        mYoyo.setBounds(bounds.left, bounds.top, bounds.right, current + bounds.bottom);
        invalidate();
    }

    @Override
    public void onAnimatorStart() {
    }

    @Override
    public void onAnimatorUpdate(Animator animator) {
        int current = (int) animator.getCurrent();
        if (current < 0) {
            current = 0;
        }
        Rect bounds = mYoyoBound;
        mYoyo.setBounds(bounds.left, bounds.top, bounds.right, current + bounds.bottom);
        invalidate();
    }

    @Override
    public void onAnimatorEnd() {
        if (mCanPerformClick) {
            performClick();
        }
    }
}
