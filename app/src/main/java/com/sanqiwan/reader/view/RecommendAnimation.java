package com.sanqiwan.reader.view;

import android.animation.Animator;
import android.animation.*;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.util.DeviceUtil;
import com.sanqiwan.reader.util.UIUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by IBM on 13-12-14.
 */
public class RecommendAnimation extends ViewGroup {

    private static final String MEIZU = "meizu";
    private static final String XIAOMI3 = "mi 3";
    private static final String ANIM_ORIENTATION = "y";
    private static final String ANIM_ALPHA = "alpha";
    private static final int COLOR_SIZE = 7;
    private static final int SEARCH_ICON_PADDING = 12;
    private static final float ALPHA_VALUE = 1.0f;
    private static final float DEFAULT_VALUE = 0.0f;
    private static final int RADIUS_SIZE = 5;
    private static final int DURATION = 1500;
    private static final int DURATION_MIN_SDK = 1000;
    private static final int END_DURATION = 1000;
    private static final int MAX_RANDOM_TIME = 300;
    private static final int DEFAULT_Y = -11;
    private AnimatorSet mBounceAnim = null;
    private AnimationSet mAnimationSet = null;
    private OnClickListener mChildListener;
    private List<Integer> mColorList;
    private List<PositionInfo> mPositionList;
    private Context mContext;
    private boolean mLayout;
    private IHeightMeasurer mHeightMeasurer;

    public RecommendAnimation(Context context) {
        this(context, null);
    }

    public RecommendAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mColorList = new ArrayList<Integer>(COLOR_SIZE);
        mPositionList = new ArrayList<PositionInfo>();
        initHeightMeasurer();
    }

    private void initHeightMeasurer() {
        if (isMeizu()) {
            mHeightMeasurer = new MeizuHeightMeasurer();
        } else {
            mHeightMeasurer = new HeightMeasurer();
        }
    }

    private boolean isMeizu() {
        String manufacturer = Build.MANUFACTURER;
        if (TextUtils.isEmpty(manufacturer)) {
            return false;
        }
        if (manufacturer.toLowerCase().contains(MEIZU)) {
            return true;
        }
        return false;
    }

    private boolean isXiaomi3() {
        String model = DeviceUtil.getModel();
        if (TextUtils.isEmpty(model)) {
            return false;
        }
        if (model.toLowerCase().contains(XIAOMI3)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChild(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(measureWidth(widthMeasureSpec), mHeightMeasurer.measureHeight(heightMeasureSpec));
    }

    private void measureChild(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            SearchRecommendView child = (SearchRecommendView) getChildAt(i);
            //为每个孩子随机生成半径大小宽度和高度都是一样大，两倍的半径
            child.measure(MeasureSpec.makeMeasureSpec(child.getRadius() * 2, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(child.getRadius() * 2, MeasureSpec.EXACTLY));
        }
    }

    private int measureWidth(int widthMeasureSpec) {
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        return width;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!mLayout) {
            mLayout = true;
            initPositionForBalls();
        }
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            SearchRecommendView recommend = (SearchRecommendView) getChildAt(i);
            recommend.layout(recommend.getEndPointX(), recommend.getEndPointY(),
                    recommend.getEndPointX() + recommend.getRadius() * 2, recommend.getEndPointY() + recommend.getRadius() * 2);
        }
    }

    public void startAnimation() {
        mBounceAnim = null;
        createAnimation();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            mBounceAnim.start();
        }
    }


    public void replaceKeys(List<String> recommends) {
        createReplaceAnimation(recommends);
    }

    public void addChildren(List<String> recommends) {
        if (recommends == null) {
            return;
        }
        initColorList(); //初始化可选颜色集合
        for (int i = 0; i < recommends.size(); i++) {
            String text = recommends.get(i);
            SearchRecommendView view = new SearchRecommendView(mContext);
            view.setOnClickListener(mListener);
            view.setBackColor(getBackColor());
            view.setText(text);
            addView(view);
        }
        if (mLayout) {
            initPositionForBalls();
        }
    }

    private void initPositionForBalls() {
        initItemsPositionList();  //初始化可选位置集合与每个位置圆的半径、字体大小和颜色信息
        for (int i = 0; i < getChildCount(); i++) {
            PositionInfo positionInfo = getPositionInfo();
            SearchRecommendView view = (SearchRecommendView) getChildAt(i);
            view.setOnClickListener(mListener);
            view.setTextColor(positionInfo.getTextColor());
            view.setRadius(positionInfo.getRadius());
            view.setTextSize(positionInfo.getTextSize());
            view.setEndPointX(positionInfo.getEndPointX());
            view.setEndPointY(positionInfo.getEndPointY());
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                view.setAlpha(0.0f);
            } else {
                Animation alphaAnimation = new AlphaAnimation(DEFAULT_VALUE, DEFAULT_VALUE);
                alphaAnimation.setDuration(1);
                view.setAnimation(alphaAnimation);
            }
        }
    }

    private OnClickListener mListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mChildListener.onClick(v);
        }
    };

    public void setChildListener(OnClickListener listener) {
        mChildListener = listener;
    }

    private void createAnimation() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            createBounceAnim();
        } else {
            createTranslateAnim();
        }
    }

    private void createBounceAnim() {
        int count = getChildCount();
        SearchRecommendView view;
        if (mBounceAnim != null) {
            return;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            List<Animator> animators = new ArrayList<Animator>();
            for (int i = 0; i < count; i++) {
                view = (SearchRecommendView) getChildAt(i);
                PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat(ANIM_ALPHA, DEFAULT_VALUE, ALPHA_VALUE);
                PropertyValuesHolder yBouncer = PropertyValuesHolder.ofFloat(ANIM_ORIENTATION,
                        UIUtil.dipToPixel(DEFAULT_Y) - view.getRadius() * 2, view.getEndPointY());

                ObjectAnimator yAlphaBouncer = ObjectAnimator.ofPropertyValuesHolder(view,
                        yBouncer, pvhAlpha);
                yAlphaBouncer.setDuration(DURATION + getRandom(MAX_RANDOM_TIME));
                yAlphaBouncer.setInterpolator(new BounceInterpolator());
                yAlphaBouncer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        invalidate();
                    }
                });

                animators.add(i, yAlphaBouncer);
            }
            if (animators.size() > 0) {
                mBounceAnim = new AnimatorSet();
                mBounceAnim.playTogether(animators);
            }
        }
    }

    private void createTranslateAnim() {
        int count = getChildCount();
        SearchRecommendView view;
        if (mAnimationSet != null) {
            mAnimationSet.cancel();
            mAnimationSet.reset();
            return;
        }
        //android 2.3系统运行动画
        for (int i = 0; i < count; i++) {
            view = (SearchRecommendView) getChildAt(i);
            Animation translateAnimation = new TranslateAnimation(
                    DEFAULT_VALUE,
                    DEFAULT_VALUE,
                    UIUtil.dipToPixel(DEFAULT_Y) - view.getRadius() * 2 - view.getEndPointY(),
                    DEFAULT_VALUE);
            Animation alphaAnimation = new AlphaAnimation(DEFAULT_VALUE, ALPHA_VALUE);
            mAnimationSet = new AnimationSet(true);
            mAnimationSet.addAnimation(translateAnimation);
            mAnimationSet.addAnimation(alphaAnimation);
            mAnimationSet.setInterpolator(new BounceInterpolator());

            //设置动画时间 (作用到每个动画)
            mAnimationSet.setDuration(DURATION_MIN_SDK + getRandom(MAX_RANDOM_TIME));
            mAnimationSet.setFillAfter(true);
            view.startAnimation(mAnimationSet);
            mAnimationSet = null;
        }
    }

    private int getRandom(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    private int getTextSize(int random) {
        switch (random) {
            case 0:
                return (int) mContext.getResources().getDimension(R.dimen.search_recommend_key_size_one);
            case 1:
                return (int) mContext.getResources().getDimension(R.dimen.search_recommend_key_size_two);
            case 2:
                return (int) mContext.getResources().getDimension(R.dimen.search_recommend_key_size_three);
            case 3:
                return (int) mContext.getResources().getDimension(R.dimen.search_recommend_key_size_four);
            case 4:
            default:
                return (int) mContext.getResources().getDimension(R.dimen.search_recommend_key_size_five);

        }
    }

    private int getMI3TextSize(int random) {
        switch (random) {
            case 0:
                return (int) mContext.getResources().getDimension(R.dimen.mi3_search_recommend_key_size_one);
            case 1:
                return (int) mContext.getResources().getDimension(R.dimen.mi3_search_recommend_key_size_two);
            case 2:
                return (int) mContext.getResources().getDimension(R.dimen.mi3_search_recommend_key_size_three);
            case 3:
                return (int) mContext.getResources().getDimension(R.dimen.mi3_search_recommend_key_size_four);
            case 4:
            default:
                return (int) mContext.getResources().getDimension(R.dimen.mi3_search_recommend_key_size_five);

        }
    }

    private int getRadius(int random) {
        switch (random) {
            case 0:
                return (int) mContext.getResources().getDimension(R.dimen.search_recommend_radius_size_one);
            case 1:
                return (int) mContext.getResources().getDimension(R.dimen.search_recommend_radius_size_two);
            case 2:
                return (int) mContext.getResources().getDimension(R.dimen.search_recommend_radius_size_three);
            case 3:
                return (int) mContext.getResources().getDimension(R.dimen.search_recommend_radius_size_four);
            case 4:
            default:
                return (int) mContext.getResources().getDimension(R.dimen.search_recommend_radius_size_five);
        }
    }

    private int getBackColor() {
        Random random = new Random();
        int r = random.nextInt(mColorList.size());
        int current = mColorList.get(r);
        removeRepeatColor(current);
        return current;
    }

    private void removeRepeatColor(int currentColor) {
        boolean isRepeat = isRepeat(currentColor);
        if (isRepeat) {
            mColorList.remove((Integer) currentColor);
        }
    }

    private boolean isRepeat(int currentColor) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            SearchRecommendView view = (SearchRecommendView) getChildAt(i);
            if (currentColor == view.getBackColor()) {
                return true;
            }
        }
        return false;
    }

    private PositionInfo getPositionInfo() {
        int random = getRandom(mPositionList.size());
        PositionInfo info = mPositionList.get(random);
        removeUsedPosition(info);
        return info;
    }

    private void removeUsedPosition(PositionInfo info) {
        mPositionList.remove(info);
    }

    private void initColorList() {
        if (mColorList.size() > 0) {
            mColorList.clear();
        }
        mColorList.add(mContext.getResources().getColor(R.color.search_recommend_back_color_one));
        mColorList.add(mContext.getResources().getColor(R.color.search_recommend_back_color_two));
        mColorList.add(mContext.getResources().getColor(R.color.search_recommend_back_color_three));
        mColorList.add(mContext.getResources().getColor(R.color.search_recommend_back_color_four));
        mColorList.add(mContext.getResources().getColor(R.color.search_recommend_back_color_five));
        mColorList.add(mContext.getResources().getColor(R.color.search_recommend_back_color_six));
        mColorList.add(mContext.getResources().getColor(R.color.search_recommend_back_color_seven));
    }

    private void initItemsPositionList() {
        mPositionList.clear();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            PositionInfo positionInfo = new PositionInfo();
            positionInfo.setTextColor(getResources().getColor(R.color.white));
            initPosition(positionInfo);
            mPositionList.add(positionInfo);
        }
    }

    private class PositionInfo {
        private int mRadius;
        private int mTextSize;
        private int mEndPointX;
        private int mEndPointY;
        private int mTextColor;

        public int getRadius() {
            return mRadius;
        }

        public void setRadius(int radius) {
            this.mRadius = radius;
        }

        public int getTextSize() {
            return mTextSize;
        }

        public void setTextSize(int textSize) {
            this.mTextSize = textSize;
        }

        public int getEndPointX() {
            return mEndPointX;
        }

        public void setEndPointX(int pointX) {
            this.mEndPointX = pointX;
        }

        public int getEndPointY() {
            return mEndPointY;
        }

        public void setEndPointY(int endPointY) {
            this.mEndPointY = endPointY;
        }

        public int getTextColor() {
            return mTextColor;
        }

        public void setTextColor(int textColor) {
            this.mTextColor = textColor;
        }
    }

    private void initPosition(PositionInfo positionInfo) {
        int width = getRandomAreaWidth();
        int height = getRandomAreaHeight();
        do {
            generateRandomPosition(positionInfo, width, height);
        } while (isOverlap(positionInfo));
    }

    private void generateRandomPosition(PositionInfo positionInfo, int width, int height) {
        int marginLeft = (int) getResources().getDimension(R.dimen.search_recommend_margin_left);
        int marginTop = (int) getResources().getDimension(R.dimen.search_recommend_margin_top);
        int random = getRandom(RADIUS_SIZE);
        positionInfo.setRadius(getRadius(random));
        int textSize;
        if (isXiaomi3()) {
            textSize = getMI3TextSize(random);
        } else {
            textSize = getTextSize(random);
        }
        positionInfo.setTextSize(textSize);
        positionInfo.setEndPointX(marginLeft + getRandom(width - 2 * positionInfo.getRadius()));
        positionInfo.setEndPointY(marginTop + getRandom(height - 2 * positionInfo.getRadius()));
    }

    private boolean isOverlap(PositionInfo positionInfo) {
        for (int index = 0; index < mPositionList.size(); index++) {
            //计算任意两个圆的圆心之间距离大于或等于它们的半径，保证两个圆不重叠
            double xPow = Math.pow(mPositionList.get(index).getEndPointX() + mPositionList.get(index).getRadius() -
                    (positionInfo.getEndPointX() + positionInfo.getRadius()), 2);
            double yPow = Math.pow(mPositionList.get(index).getEndPointY() + mPositionList.get(index).getRadius() -
                    (positionInfo.getEndPointY() + positionInfo.getRadius()), 2);
            double distance = Math.pow(mPositionList.get(index).getRadius() + positionInfo.getRadius(), 2);
            if (xPow + yPow < distance) {
                return true;
            }
        }
        return false;
    }

    private int getRandomAreaWidth() {
        int marginLeft = (int) getResources().getDimension(R.dimen.search_recommend_margin_left);
        return DeviceUtil.getDeviceWidth() - marginLeft * 2;
    }

    private int getRandomAreaHeight() {
        int marginTop = (int) getResources().getDimension(R.dimen.search_recommend_margin_top);
        int marginBottom = (int) getResources().getDimension(R.dimen.search_recommend_margin_bottom);
        return getHeight() - marginTop - marginBottom;
    }

    private class ShowRecommendListener implements Animation.AnimationListener {

        private List<String> mRecommends;
        private RecommendAnimation mRecommendAnimation;

        public List<String> getRecommends() {
            return mRecommends;
        }

        public void setRecommends(List<String> recommends) {
            this.mRecommends = recommends;
        }

        public ShowRecommendListener(RecommendAnimation recommendAnimation) {
            mRecommendAnimation = recommendAnimation;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mRecommendAnimation.removeAllViews();
            mRecommendAnimation.addChildren(mRecommends);
            mRecommendAnimation.startAnimation();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private void createReplaceAnimation(List<String> recommends) {
        int count = getChildCount();
        SearchRecommendView view;

        for (int i = 0; i < count; i++) {
            view = (SearchRecommendView) getChildAt(i);
            Animation translateAnimation = new TranslateAnimation(
                    DEFAULT_VALUE,
                    DEFAULT_VALUE,
                    DEFAULT_VALUE,
                    getHeight() - view.getEndPointY() + view.getRadius() * 2); //移动位置是整体滑出控件高度。
            translateAnimation.setInterpolator(new DecelerateInterpolator());

            //设置动画时间 (作用到每个动画)
            translateAnimation.setDuration(END_DURATION);
            if (i == count - 1) {
                ShowRecommendListener showRecommendListener = new ShowRecommendListener(this);
                showRecommendListener.setRecommends(recommends);
                translateAnimation.setAnimationListener(showRecommendListener);
            }

            view.startAnimation(translateAnimation);
        }
    }

    private interface IHeightMeasurer {
        public int measureHeight(int heightMeasureSpec);
    }

    private class HeightMeasurer implements IHeightMeasurer {

        @Override
        public int measureHeight(int heightMeasureSpec) {
            int tabarHeight = AppContext.getInstance().getResources().getDimensionPixelSize(R.dimen.tab_bg_height);
            int padding = 2 * UIUtil.dipToPixel(SEARCH_ICON_PADDING);
            int bottomHeight = AppContext.getInstance().getResources().getDrawable(R.drawable.bottom_bg_nor).getMinimumHeight();
            int statusBarHeight = DeviceUtil.getStatusBarHeight();

            int height = DeviceUtil.getDeviceHeight() - tabarHeight - padding - statusBarHeight  - bottomHeight;
            return height;    //测量ViewGroup 的高度
        }

    }

    private class DefaultMeasurer implements IHeightMeasurer {

        @Override
        public int measureHeight(int heightMeasureSpec) {
            int specMode = MeasureSpec.getMode(heightMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            return height;
        }
    }

    private class MeizuHeightMeasurer implements IHeightMeasurer {

        @Override
        public int measureHeight(int heightMeasureSpec) {
            int tabarHeight = AppContext.getInstance().getResources().getDimensionPixelSize(R.dimen.tab_bg_height);
            int padding = 2 * UIUtil.dipToPixel(SEARCH_ICON_PADDING);
            int bottomHeight = AppContext.getInstance().getResources().getDrawable(R.drawable.bottom_bg_nor).getMinimumHeight();
            int statusBarHeight = DeviceUtil.getStatusBarHeight();

            int height = DeviceUtil.getDeviceHeight() - tabarHeight - padding - statusBarHeight * 3 - bottomHeight;
            return height;    //测量ViewGroup 的高度
        }
    }
}
