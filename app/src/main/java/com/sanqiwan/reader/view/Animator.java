package com.sanqiwan.reader.view;

import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.sanqiwan.reader.util.UIUtil;


/**
 * Created by chen on 12/17/13.
 */
public class Animator implements Runnable {

    private static final String TAG = "Animator";
    private Handler mHandler;
    private long mStartTime;
    private long mDuration;
    private Interpolator mInterpolator;
    private AnimatorListener mListener;
    private boolean mStopped;
    private float mCurrent;
    private float mFrom;
    private float mTo;
    private boolean mRepeat;

    public Animator() {
        mHandler = UIUtil.getHandler();
        mInterpolator = new LinearInterpolator();
    }

    public void start() {
        mStartTime = SystemClock.uptimeMillis();
        mCurrent = mFrom;
        mStopped = false;
        mHandler.post(this);
        mListener.onAnimatorStart();
    }

    public void stop() {
        mStopped = true;
        mHandler.removeCallbacks(this);
    }

    public void setFrom(float from) {
        mFrom = from;
    }

    public void setTo(float to) {
        mTo = to;
    }

    public float getCurrent() {
        return mCurrent;
    }

    public void setRepeat(boolean repeat) {
        mRepeat = repeat;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }
    }

    public void setAnimatorListener(AnimatorListener listener) {
        mListener = listener;
    }

    @Override
    public void run() {
        long currentTime = SystemClock.uptimeMillis();
        float delta = currentTime - mStartTime;
        float fraction = delta / mDuration;
        fraction = mInterpolator.getInterpolation(fraction);
        mCurrent = mFrom + (mTo - mFrom) * fraction;
        mListener.onAnimatorUpdate(this);
        if (currentTime < mStartTime + mDuration && !mStopped) {
            mHandler.removeCallbacks(this);
            mHandler.post(this);
        } else if (mRepeat && !mStopped) {
            start();
        } else {
            mListener.onAnimatorEnd();
        }
    }

    public interface AnimatorListener {
        public void onAnimatorStart();
        public void onAnimatorUpdate(Animator animator);
        public void onAnimatorEnd();
    }
}
