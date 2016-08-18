package com.sanqiwan.reader.menu;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.preference.Settings;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/28/13
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderSettingsView extends Dialog implements View.OnClickListener {

    private static final int COLUMN_COUNT = 2;

    public enum ReadMode {
        Scrolling,
        Paging,
    }

    public enum Orientation {
        Horizontal,
        Vertical,
    }

    public interface Listener {
        public void onModeChanged(ReadMode readMode);
        public void onOrientationChanged(Orientation orientation);
        public void onInterceptVolumeKey(boolean intercept);
        public void onLightModeChanged(boolean alwaysOn);
    }

    private Listener mListener;
    private Context mContext;
    private CheckBox mVolumeTurnCheckbox;
    private CheckBox mBackgroundLightCheckbox;

    public ReaderSettingsView(Context context) {
        super(context, R.style.reader_menu_style);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_settings_layout);
        findViewById(android.R.id.content).setOnClickListener(this);

        findViewById(R.id.reader_paging).setOnClickListener(this);
        findViewById(R.id.scrolling).setOnClickListener(this);
        findViewById(R.id.horizontal).setOnClickListener(this);
        findViewById(R.id.vertical).setOnClickListener(this);
        findViewById(R.id.volume_turn).setOnClickListener(this);
        findViewById(R.id.background_light).setOnClickListener(this);

        mVolumeTurnCheckbox = (CheckBox) findViewById(R.id.volume_turn_checkBox);
        mBackgroundLightCheckbox = (CheckBox) findViewById(R.id.background_light_checkBox);

        mVolumeTurnCheckbox.setChecked(Settings.getInstance().isReaderVolumePaging());
        mBackgroundLightCheckbox.setChecked(Settings.getInstance().isReaderBackgroundLightAlways());
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.reader_paging:
                if (mListener != null) {
                    mListener.onModeChanged(ReadMode.Paging);
                }
                break;
            case R.id.scrolling:
                if (mListener != null) {
                    mListener.onModeChanged(ReadMode.Scrolling);
                }
                break;
            case R.id.horizontal:
                if (mListener != null) {
                    mListener.onOrientationChanged(Orientation.Horizontal);
                }
                break;
            case R.id.vertical:
                if (mListener != null) {
                    mListener.onOrientationChanged(Orientation.Vertical);
                }
                break;
            case R.id.volume_turn:
                if (mListener != null) {
                    boolean result = !mVolumeTurnCheckbox.isChecked();
                    mVolumeTurnCheckbox.setChecked(result);
                    mListener.onInterceptVolumeKey(result);
                }
                break;
            case R.id.background_light:
                if (mListener != null) {
                    boolean result = !mBackgroundLightCheckbox.isChecked();
                    mBackgroundLightCheckbox.setChecked(result);
                    mListener.onLightModeChanged(result);
                }
                break;
            case android.R.id.content:
                dismiss();
                break;
        }
    }
}
