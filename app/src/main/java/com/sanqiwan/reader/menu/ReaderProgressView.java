package com.sanqiwan.reader.menu;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import com.sanqiwan.reader.R;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/28/13
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderProgressView extends Dialog implements View.OnClickListener {

    public interface Listener {
        public void onNextChapter();
        public void onPreviousChapter();
        public void onReaderProgressChanged(int progress);
        public int getProgress();
        public int getMax();
        public String getTitle(int progress);
    }

    private SeekBar mReaderProgressAdjustBar;
    private Listener mListener;
    private PopupWindow mProgressTipsWindow;
    private TextView mTitleTextView;
    private Context mContext;

    public ReaderProgressView(Context context) {
        super(context, R.style.reader_menu_style);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_mode_layout);
        findViewById(android.R.id.content).setOnClickListener(this);
        findViewById(R.id.next_chapter).setOnClickListener(this);
        findViewById(R.id.previous_chapter).setOnClickListener(this);
        mReaderProgressAdjustBar = (SeekBar) findViewById(R.id.reader_progress);
        // TODO: 读取阅读进度
        mReaderProgressAdjustBar.setOnSeekBarChangeListener(mReaderProgressListener);
        setProgress();
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private void setProgress() {
        if (mListener != null) {
            mReaderProgressAdjustBar.setMax(mListener.getMax());
            mReaderProgressAdjustBar.setProgress(mListener.getProgress());
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.next_chapter:
                if (mListener != null) {
                    mListener.onNextChapter();
                    mReaderProgressAdjustBar.setProgress(mReaderProgressAdjustBar.getProgress() + 1);
                }
                break;
            case R.id.previous_chapter:
                if (mListener != null) {
                    mListener.onPreviousChapter();
                    mReaderProgressAdjustBar.setProgress(mReaderProgressAdjustBar.getProgress() - 1);
                }
                break;
            case android.R.id.content:
                dismiss();
                break;
        }
    }

    private SeekBar.OnSeekBarChangeListener mReaderProgressListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mListener != null && fromUser) {
                showPopupWindow();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            showPopupWindow();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mProgressTipsWindow.isShowing()) {
                mProgressTipsWindow.dismiss();
            }
            if (mListener != null) {
                mListener.onReaderProgressChanged(seekBar.getProgress());
            }

        }
    };

    private void showPopupWindow() {
        if (mProgressTipsWindow == null) {
            if (mContext == null) {
                return;
            }
            mProgressTipsWindow = new PopupWindow(mContext);
            mTitleTextView = new TextView(mContext);
            mTitleTextView.setBackgroundResource(R.drawable.progress_tip);
            mTitleTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            mTitleTextView.setTextColor(mContext.getResources().getColor(R.color.white));
            mTitleTextView.setTextSize(mContext.getResources().getDimension(R.dimen.read_progress_tip_text_size));
            mProgressTipsWindow.setContentView(mTitleTextView);
            mProgressTipsWindow.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mProgressTipsWindow.setFocusable(false);
            mProgressTipsWindow.setBackgroundDrawable(new BitmapDrawable());
        }
        if (mListener != null) {
            mTitleTextView.setText(mListener.getTitle(mReaderProgressAdjustBar.getProgress()));
        }
        //x偏移：progressbar的宽度*progress数值百分值-弹出框的高度的一半
        int offx = (int) (mReaderProgressAdjustBar.getMeasuredWidth() *
                mReaderProgressAdjustBar.getProgress() / mReaderProgressAdjustBar.getMax() -
                0.5 * (float) mTitleTextView.getMeasuredWidth());
        if (offx >= mReaderProgressAdjustBar.getMeasuredWidth() - mTitleTextView.getMeasuredWidth()) {
            offx = mReaderProgressAdjustBar.getMeasuredWidth() - mTitleTextView.getMeasuredWidth();
        }
        //y偏移：负progressbar的高度+ 负popupwindow的高度
        int offy = - mReaderProgressAdjustBar.getMeasuredHeight() - mTitleTextView.getMeasuredHeight() ;
        if(mProgressTipsWindow.isShowing()) {
            mProgressTipsWindow.update(mReaderProgressAdjustBar, offx, offy, mTitleTextView.getWidth(), mTitleTextView.getHeight());
        } else {
            mProgressTipsWindow.showAsDropDown(mReaderProgressAdjustBar, offx, offy);
        }
    }
}
