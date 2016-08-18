package com.sanqiwan.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sanqiwan.reader.util.UIUtil;

/**
 * Created by sam on 14-3-26.
 * 正六边行控件
 */
public class HexagonView extends View {

    private TextView mTextView;
    private int mTextSize = 18;
    private int mTextColor = Color.BLACK;
    private int mBackColor = Color.BLUE;
    private String mText;
    private Paint mPaint;
    private Path mPath;
    private float mCenterPointX; //中心点
    private float mCenterPointY;
    private double mRadius;
    private double mSideLength;  //边长


    public HexagonView(Context context) {
        super(context);
        init();
    }

    public HexagonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HexagonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }


    protected  void init() {
        mTextView = new TextView(getContext());
        mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextColor(mTextColor);
        mTextView.setTextSize(mTextSize);
        mTextView.setPadding(20, 0, 20, 0);
        mTextView.setText(mText);
        mPath = new Path();
        mPaint = new Paint();
        mSideLength = Math.sqrt(4.0 / 3.0) * getRadius();

    }

    public void setCenter(float x, float y) {
        mCenterPointX = x;
        mCenterPointY = y;
    }

    public void setRadius(float radius) {
        mRadius = UIUtil.dipToPixel(radius);
        mSideLength = Math.sqrt(4.0 / 3.0) * getRadius();
        Log.i("radius", "radiuse" + mRadius);
    }

    public void setBackColor(int color) {
        mBackColor = color;
    }

    public void setTextColor(int color) {
        mTextColor = color;
        mTextView.setTextColor(color);
    }

    public void setTextSize(int size) {
        mTextSize = size;
        mTextView.setTextSize(mTextSize);
    }

    public void setText(String text) {
        mText = text;
        mTextView.setText(mText);
        setTag(text);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mTextView.measure(MeasureSpec.makeMeasureSpec(Math.round((float)(getSideLength() + 2.0 * Math.sqrt(mRadius*mRadius*1.0/3.0))), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(Math.round((float)mRadius), MeasureSpec.EXACTLY));

        int measureWidth = measureWidth(widthMeasureSpec);
        int measuredHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measuredHeight);
    }

    private int measureHeight(int measureSpec) {
        return  (int) (getRadius() * 2);
    }

    private int measureWidth(int measureSpec) {
        return (int) (2 * mSideLength);
    }

    public double getRadius() {
        return mRadius;
    }

    public double getSideLength() {
        return mSideLength;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int l = (int) (mSideLength - mTextView.getMeasuredWidth() / 2);
        int t = (int) (mRadius - mTextView.getMeasuredHeight() / 2);
        mTextView.layout(l, t, l + mTextView.getMeasuredWidth(), t + mTextView.getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.moveTo((float) (0.5 * getSideLength()), 0);
        mPath.lineTo((float) (1.5 * getSideLength()), 0);
        mPath.lineTo((float) (2*getSideLength()), (float) getRadius());
        mPath.lineTo((float) (1.5*getSideLength()), (float) (2*getRadius()));
        mPath.lineTo((float) (0.5*getSideLength()), (float) (2*getRadius()));
        mPath.lineTo(0, (float) getRadius());
        mPath.lineTo((float) (0.5*getSideLength()), 0);
        mPaint.setColor(mBackColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);
        canvas.drawPath(mPath, mPaint);

        int left = (int)getSideLength() - mTextView.getWidth() / 2;
        int top = (int)getRadius() - mTextView.getHeight() / 2;
        canvas.save();
        canvas.translate(left, top);
        canvas.clipRect(0, 0, mTextView.getMeasuredWidth(), mTextView.getMeasuredHeight());
        mTextView.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (inHexagon(x, y)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return performClick();
            }
        }
        return false;
    }

    /**
     * 判断点击点是否在六边形区域内
     * @param x
     * @param y
     * @return
     */
    private boolean inHexagon(float x, float y) {
        if (x >= 0.5 * getSideLength() && x <= 1.5 * getSideLength()) {  //在中间方形区域
            return true;
        }

        if ( x >= 0.5 * getSideLength() ) {    //将x转换到第一象限
            x =  (float) (2 * getSideLength() - x);
        }

        if ( y >= getRadius() ) {      //将y转换到第一象限
            y =  (float) (2 * getRadius() - y);
        }

        if ( y >= Math.sqrt(3.0) * (0.5*getSideLength() - x)) {   //假设x在六边形中，判断y是否在
            return true;
        }
        return false;
    }

    public int getTextSize() {
        return mTextSize;
    }


    public int getTextColor() {
        return mTextColor;
    }


    public int getBackColor() {
        return mBackColor;
    }


    public String getText() {
        return mText;
    }

    public float getCenterPointX() {
        return mCenterPointX;
    }


    public float getCenterPointY() {
        return mCenterPointY;
    }

}
