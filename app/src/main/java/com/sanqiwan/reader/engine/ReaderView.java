package com.sanqiwan.reader.engine;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.model.Chapter;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/29/13
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderView extends LinearLayout {

    private ScrollView mContainer;
    private TextView mTextView;
    private TextView mNextChapterButton;
    private TextView mHeaderView;

    private RequestNextChapterListener mRequestNextChapterListener;

    public ReaderView(Context context) {
        this(context, null);
    }

    public ReaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.reader_scroll_layout, this);

        ReaderSettings readerSettings = ReaderSettings.getInstance();
        mHeaderView = (TextView) findViewById(R.id.header);
        mHeaderView.setTextSize(readerSettings.getSecondaryFontSize());
        mHeaderView.setTextColor(readerSettings.getSecondaryTextColor());

        mTextView = (TextView) findViewById(R.id.content);
        mTextView.setTextSize(readerSettings.getFontSize());
        mTextView.setTextColor(readerSettings.getTextColor());

        mTextView.setDrawingCacheEnabled(true);
        mNextChapterButton = (TextView) findViewById(R.id.next_button);
        mNextChapterButton.setTextSize(readerSettings.getFontSize());
        mNextChapterButton.setOnClickListener(mNextChapterButtonListener);
        mNextChapterButton.setVisibility(GONE);

        mContainer = (ScrollView) findViewById(R.id.container);
    }

    public void setRequestNextChapterListener(RequestNextChapterListener listener) {
        mRequestNextChapterListener = listener;
    }

    public void setText(Chapter chapter, boolean isLastChapter) {
        mContainer.scrollTo(0, 0);
        mHeaderView.setText(chapter.getTitle());
        mTextView.setText(chapter.getContent());
        if (isLastChapter) {
            mNextChapterButton.setVisibility(View.GONE);
        } else {
            mNextChapterButton.setVisibility(View.VISIBLE);
        }
    }

    public void clear() {
        mHeaderView.setText(null);
        mTextView.setText(null);
        mNextChapterButton.setVisibility(View.GONE);
        mContainer.scrollTo(0, 0);
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(mTextView.getText());
    }

    public void setTextSize(float size) {
        mTextView.setTextSize(size);
    }

    public void setTextColor(int color) {
        mTextView.setTextColor(color);
    }

    public void setHeaderTextSize(float size) {
        mHeaderView.setTextSize(size);
    }

    public void setHeaderTextColor(int color) {
        mHeaderView.setTextColor(color);
    }

    private OnClickListener mNextChapterButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mRequestNextChapterListener != null) {
                mRequestNextChapterListener.onRequestNextChapter();
            }
        }
    };

    public void smoothScrollBy(int dx, int dy) {
        mContainer.smoothScrollBy(dx, dy);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (getBackground() != null) {
            getBackground().setBounds(0, 0, getWidth(), getHeight());
        }
    }

    public boolean isAtTopPosition() {
        return mContainer.getScrollY() == 0;
    }

    public boolean isAtBottomPosition() {
        return mContainer.getScrollY() ==
                mContainer.getChildAt(0).getHeight() - mContainer.getHeight();
    }
}
