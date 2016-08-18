package com.sanqiwan.reader.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.util.DeviceUtil;
import com.sanqiwan.reader.util.UIUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by sam on 14-3-31.
 */
public class RecommendLayout extends ViewGroup {

    private List<Integer> mColorList;
    private float mGapSize = 4;
    private double mRadius = 50;
    private float mCenterX, mCenterY;
    private List<HexagonView> mViews;
    public final static int VIEW_SIZE = 7;
    private final static int TEXT_SIZE = 18;
    private static final float FROM_DEGREE = 0.0f;
    private static final float TO_DEGREE = 180.0f;
    private static final long DURATION = 220;
    private static final int DELAY_TIME = 40;

    public RecommendLayout(Context context) {
        super(context);
        initColorList();
        initHexagonViews();
    }

    public RecommendLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initColorList();
        initHexagonViews();
    }

    public RecommendLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initColorList();
        initHexagonViews();
    }

    private void initCenterPoint() {


        double size = UIUtil.dipToPixel((float) mRadius);
        double gap = UIUtil.dipToPixel(mGapSize);
        float leftSize = (float) (Math.sqrt(3.0) * (size + 0.5 * gap));

        mViews.get(0).setCenter(mCenterX, mCenterY);
        mViews.get(1).setCenter(mCenterX, (float) (mCenterY - (2 * size) - gap));
        mViews.get(2).setCenter(mCenterX, (float) (mCenterY + (2 * size) + gap));
        mViews.get(3).setCenter(mCenterX - leftSize, (float) (mCenterY - size - 0.5 * gap));
        mViews.get(4).setCenter(mCenterX - leftSize, (float) (mCenterY + size + 0.5 * gap));
        mViews.get(5).setCenter(mCenterX + leftSize, (float) (mCenterY - size - 0.5 * gap));
        mViews.get(6).setCenter(mCenterX + leftSize, (float) (mCenterY + size + 0.5 * gap));

    }

    private void initHexagonViews() {

        mCenterX = DeviceUtil.getDeviceWidth() / 2;
        mCenterY = DeviceUtil.getDeviceWidth() / 2;

        if ( mCenterX <= 240) {
            mRadius -= 10;
            mCenterY -= 30;
        }

        if (mViews == null) {
            mViews = new ArrayList<HexagonView>();
        }
        if (mViews.size() > 0) {
            mViews.clear();
            removeAllViewsInLayout();
        }
        for (int i = 0; i < VIEW_SIZE; i++) {
            HexagonView v = new HexagonView(getContext());
            v.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            v.setBackColor(getBackColor());
            v.setTextSize(TEXT_SIZE);
            v.setTextColor(Color.WHITE);
            v.setRadius((float) mRadius);
            mViews.add(v);
            addView(v);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        initCenterPoint();
        for (int i = 0; i < getChildCount(); i++) {
            HexagonView view = (HexagonView) getChildAt(i);
            view.layout((int) (view.getCenterPointX() - view.getSideLength()),
                    (int) (view.getCenterPointY() - view.getRadius()),
                    (int) (view.getCenterPointX() + view.getSideLength()),
                    (int) (view.getCenterPointY() + view.getRadius()));
        }
    }

    private int getBackColor() {
        Random random = new Random();
        int r = random.nextInt(mColorList.size());
        return mColorList.get(r);
    }

    private void initColorList() {
        if (mColorList == null) {
            mColorList = new ArrayList<Integer>();
        }

        if (mColorList.size() > 0) {
            mColorList.clear();
        }
        mColorList.add(getResources().getColor(R.color.search_recommend_back_color_one));
        mColorList.add(getResources().getColor(R.color.search_recommend_back_color_two));
        mColorList.add(getResources().getColor(R.color.search_recommend_back_color_three));
        mColorList.add(getResources().getColor(R.color.search_recommend_back_color_four));
        mColorList.add(getResources().getColor(R.color.search_recommend_back_color_five));
        mColorList.add(getResources().getColor(R.color.search_recommend_back_color_six));
        mColorList.add(getResources().getColor(R.color.search_recommend_back_color_seven));
    }

    public void setWords(List<String> words) {
        int delay = 0;
        if (words == null || words.size() < VIEW_SIZE) {
            return;
        }
        for (int i = 0; i < mViews.size(); i++) {
            delay += DELAY_TIME;
            final String word = words.get(i);
            final HexagonView view = mViews.get(i);
            final Rotate3d rotate = new Rotate3d(FROM_DEGREE, TO_DEGREE);
            rotate.setDuration(DURATION);
            rotate.setStartOffset(delay);
            rotate.setInterpolator(new AccelerateDecelerateInterpolator());
            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setBackColor(getBackColor());
                    view.setText(word);
                    view.invalidate();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            view.clearAnimation();
            view.startAnimation(rotate);
        }
    }

    public void setChildOnClickListener(OnClickListener listener) {
        if (mViews == null) {
            return;
        }
        for (int i = 0; i < VIEW_SIZE; i++) {
            mViews.get(i).setOnClickListener(listener);
        }
    }

}
