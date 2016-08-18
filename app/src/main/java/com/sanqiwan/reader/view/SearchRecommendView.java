package com.sanqiwan.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.sanqiwan.reader.R;

/**
 * Created by IBM on 13-12-14.
 */
public class SearchRecommendView extends View {

    private static final float WIDTH_BASE = 1.6f;
    private static final float HEIGHT_BASE = 1.2f;
    private static int DEFAULT_TEXT_SIZE;
    private Context mContext;
    private TextView mTextView;
    private Paint mPaint;
    private Path mPath;
    private int mBackColor;
    private int mEndPointX; //mEndPointX，mEndPointY是控件的左上角
    private int mEndPointY;
    private int mRadius;

    public SearchRecommendView(Context context) {
        this(context, null);
    }

    public SearchRecommendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint();
        mPath = new Path();
        mTextView = new TextView(mContext);
        mTextView.setGravity(Gravity.CENTER);
        DEFAULT_TEXT_SIZE = (int) mContext.getResources().getDimension(R.dimen.default_size);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mTextView.measure(MeasureSpec.makeMeasureSpec(Math.round((WIDTH_BASE * getRadius())), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(Math.round((HEIGHT_BASE * getRadius())), MeasureSpec.EXACTLY));

        int measureWidth = measureWidth(widthMeasureSpec);
        int measuredHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measuredHeight);
    }


    private int measureHeight(int measureSpec) {
        return getRadius() * 2;
    }

    private int measureWidth(int measureSpec) {
        return getRadius() * 2;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int l = getRadius() - mTextView.getMeasuredWidth() / 2;
        int t = getRadius() - mTextView.getMeasuredHeight() / 2;
        mTextView.layout(l, t, l + mTextView.getMeasuredWidth(), t + mTextView.getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.addCircle(getWidth() / 2, getHeight() / 2, getRadius(), Path.Direction.CW);
        mPaint.setColor(getBackColor());
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);
        canvas.drawPath(mPath, mPaint);
        this.setTag(getText());

        int left = getRadius() - mTextView.getWidth() / 2;
        int top = getRadius() - mTextView.getHeight() / 2;
        canvas.save();
        canvas.translate(left, top);
        canvas.clipRect(0, 0, mTextView.getWidth(), mTextView.getHeight());
        mTextView.draw(canvas);
        canvas.restore();
    }

    public String getText() {
        return mTextView.getText().toString();
    }

    public void setText(String showText) {
        mTextView.setText(showText);
    }

    public void setTextColor(int textColor) {
        mTextView.setTextColor(textColor);
    }

    public int getBackColor() {
        return mBackColor;
    }

    public void setBackColor(int backColor) {
        this.mBackColor = backColor;
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int radius) {
        this.mRadius = radius;
    }

    public float getTextSize() {
        return mTextView.getTextSize();
    }

    public void setTextSize(int textSize) {
        if (textSize == 0) {
            textSize = DEFAULT_TEXT_SIZE;
        }
        mTextView.setTextSize(textSize);
    }

    public int getEndPointX() {
        return mEndPointX;
    }

    public void setEndPointX(int endPointX) {
        this.mEndPointX = endPointX;
    }

    public int getEndPointY() {
        return mEndPointY;
    }

    public void setEndPointY(int endPointY) {
        this.mEndPointY = endPointY;
    }
}
