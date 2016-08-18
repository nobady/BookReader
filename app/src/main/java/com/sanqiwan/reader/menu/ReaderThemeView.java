package com.sanqiwan.reader.menu;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.engine.ThemeProfile;
import com.sanqiwan.reader.preference.Settings;
import com.sanqiwan.reader.view.HorizontalListLayout;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/28/13
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderThemeView extends Dialog implements View.OnClickListener {

    private static final int STEP = 10;

    public interface Listener {
        public void onThemeChanged(String themeName);
        public void onBrightnessChanged(int brightness);
    }

    private Listener mListener;

    private HorizontalListLayout mHorizontalListLayout;
    private SeekBar mSeekBar;
    private Context mContext;

    public ReaderThemeView(Context context) {
        super(context, R.style.reader_menu_style);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_theme_layout);
        findViewById(android.R.id.content).setOnClickListener(this);
        mHorizontalListLayout = (HorizontalListLayout) findViewById(R.id.theme_thumb_list);
        mHorizontalListLayout.setAdapter(new BackgroundAdapter(mContext));
        findViewById(R.id.decrease).setOnClickListener(this);
        findViewById(R.id.increase).setOnClickListener(this);
        mSeekBar = (SeekBar) findViewById(R.id.brightness);
        mSeekBar.setMax(100);
        mSeekBar.setProgress(Settings.getInstance().getReaderBrightness());
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.decrease:
                mSeekBar.setProgress(mSeekBar.getProgress() - STEP);
                break;
            case R.id.increase:
                mSeekBar.setProgress(mSeekBar.getProgress() + STEP);
                break;
            case android.R.id.content:
                dismiss();
                break;
        }
    }

    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mListener != null) {
                mListener.onBrightnessChanged(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private class BackgroundAdapter extends BaseAdapter implements View.OnClickListener {

        private String[] mTitles;

//        private int[] mImages = new int[] {
//                R.drawable.reader_background_1,
//                R.drawable.reader_background_2,
//                R.drawable.reader_background_3,
//                R.drawable.reader_background_4,
//                R.drawable.reader_background_5,
//                R.drawable.reader_background_6,
//                R.drawable.reader_background_7,
//        };

        private List<String> mThemeNames = ThemeProfile.names();

        private Context mContext;

        private BackgroundAdapter(Context context) {
            mContext = context;
//            mTitles = mContext.getResources().getStringArray(R.array.reader_backgrounds);
        }

        @Override
        public int getCount() {
            return mThemeNames.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getContext());
            textView.setTextColor(mContext.getResources().getColor(R.color.reader_menu_title_color));
            ThemeProfile themeProfile = ThemeProfile.get(mThemeNames.get(position));
            textView.setCompoundDrawablesWithIntrinsicBounds(0, themeProfile.getIconId(), 0, 0);
            textView.setText(themeProfile.getThemeName());
            textView.setPadding(5, 5, 5, 5);
            textView.setOnClickListener(this);
            if (Settings.getInstance().getReaderTheme().equals(mThemeNames.get(position))) {
                textView.setSelected(true);
            }
            textView.setTag(mThemeNames.get(position));
            return textView;
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                // TODO: 加载背景图片
                mListener.onThemeChanged((String) v.getTag());
                notifyDataSetChanged();
            }
        }
    }
}

