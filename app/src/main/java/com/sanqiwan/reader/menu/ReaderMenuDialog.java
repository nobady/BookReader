package com.sanqiwan.reader.menu;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.preference.Settings;
import com.sanqiwan.reader.ui.ReaderActivity;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/29/13
 * Time: 11:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderMenuDialog extends Dialog implements View.OnClickListener {

    private static final int INDEX_PROGRESS = 0;
    private static final int INDEX_BACKGROUND = 1;
    private static final int INDEX_FONT = 2;
    private static final int INDEX_SETTINGS = 3;
    private static final int PLUGIN_COUNT = 4;

    private boolean mKeyVolumeDownFlag = false;
    private boolean mKeyVolumeUpFlag = false;
    private boolean mKeyMenuFlag = false;

    private ReaderActivity mReaderActivity;
    private ImageView mBackButton;
    private TextView mTitleView;
    private ViewGroup mPluginHolder;
    private TextView mTocButton;
    private View[] mViews = new View[PLUGIN_COUNT];

    public ReaderMenuDialog(ReaderActivity context) {
        super(context, R.style.reader_menu_style);
        mReaderActivity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_menu_layout);
        initViews();
    }

    private void initViews() {

        mBackButton = (ImageView) findViewById(R.id.btn_return);
        mBackButton.setOnClickListener(this);
        mTitleView = (TextView) findViewById(R.id.top_title);
        mTitleView.setText(mReaderActivity.getBookTitle());
        mPluginHolder = (ViewGroup) findViewById(R.id.plugin_holder);
        findViewById(R.id.menu_center).setOnClickListener(this);

        View view = findViewById(R.id.progress);
        view.setOnClickListener(this);
        mViews[INDEX_PROGRESS] = view;

        view = findViewById(R.id.background);
        mViews[INDEX_BACKGROUND] = view;
        view.setOnClickListener(this);

        view = findViewById(R.id.font);
        mViews[INDEX_FONT] = view;
        view.setOnClickListener(this);

        view = findViewById(R.id.setting);
        mViews[INDEX_SETTINGS] = view;
        view.setOnClickListener(this);

        mTocButton = (TextView) findViewById(R.id.toc);
        mTocButton.setOnClickListener(this);
    }

    private void showSubMenu(int index) {
        dismiss();
        getPluginView(index).show();
    }

    private Dialog getPluginView(int index) {
        switch (index) {
            case INDEX_PROGRESS:
                ReaderProgressView readerProgressView = new ReaderProgressView(getContext());
                readerProgressView.setListener(mReaderModeListener);
                return readerProgressView;
            case INDEX_BACKGROUND:
                ReaderThemeView readerThemeView = new ReaderThemeView(getContext());
                readerThemeView.setListener(mWallpaperListener);
                return readerThemeView;
            case INDEX_FONT:
                ReaderFontView readerFontView = new ReaderFontView(getContext());
                readerFontView.setListener(mReaderFontListener);
                return readerFontView;
            case INDEX_SETTINGS:
                ReaderSettingsView readerSettingsView = new ReaderSettingsView(getContext());
                readerSettingsView.setListener(mReaderSettingsListener);
                return readerSettingsView;

        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return:
                dismiss();
                mReaderActivity.finish();
                break;
            case R.id.progress:
                showSubMenu(INDEX_PROGRESS);
                break;
            case R.id.background:
                showSubMenu(INDEX_BACKGROUND);
                break;
            case R.id.font:
                showSubMenu(INDEX_FONT);
                break;
            case R.id.setting:
                showSubMenu(INDEX_SETTINGS);
                break;
            case R.id.toc:
                dismiss();
                mReaderActivity.toggleToc();
                break;
            case R.id.menu_center:
                dismiss();
                break;
        }
    }

    private ReaderProgressView.Listener mReaderModeListener = new ReaderProgressView.Listener() {

        @Override
        public void onNextChapter() {
            mReaderActivity.nextChapter();
        }

        @Override
        public void onPreviousChapter() {
            mReaderActivity.previousChapter();
        }

        @Override
        public void onReaderProgressChanged(int progress) {
            mReaderActivity.updateReadProgress(progress);
        }

        @Override
        public int getProgress() {
            return mReaderActivity.getProgress();
        }

        @Override
        public int getMax() {
            return mReaderActivity.getMax();
        }

        @Override
        public String getTitle(int progress) {
            return mReaderActivity.getTitle(progress);
        }


    };

    private ReaderThemeView.Listener mWallpaperListener = new ReaderThemeView.Listener() {
        @Override
        public void onThemeChanged(String themeName) {
            mReaderActivity.updateTheme(themeName);
        }

        @Override
        public void onBrightnessChanged(int brightness) {
            mReaderActivity.setBrightness(brightness);
        }
    };

    private ReaderFontView.Listener mReaderFontListener = new ReaderFontView.Listener() {

        @Override
        public void onZoomIn() {
            mReaderActivity.zoomIn();
        }

        @Override
        public void onZoomOut() {
            mReaderActivity.zoomOut();
        }

    };

    private ReaderSettingsView.Listener mReaderSettingsListener = new ReaderSettingsView.Listener() {
        @Override
        public void onModeChanged(ReaderSettingsView.ReadMode readMode) {
            mReaderActivity.updateReadMode(readMode);
        }

        @Override
        public void onOrientationChanged(ReaderSettingsView.Orientation orientation) {
            mReaderActivity.updateOrientation(orientation);
        }

        @Override
        public void onInterceptVolumeKey(boolean intercept) {
            mReaderActivity.updateVolumePaging(intercept);
        }

        @Override
        public void onLightModeChanged(boolean alwaysOn) {
            mReaderActivity.updateLightMode(alwaysOn);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Settings.getInstance().isReaderVolumePaging()) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                mKeyVolumeDownFlag = true;
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                mKeyVolumeUpFlag = true;
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                mKeyMenuFlag = true;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (Settings.getInstance().isReaderVolumePaging()) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && mKeyVolumeDownFlag) {
                mReaderActivity.nextPage();
                mKeyVolumeDownFlag = false;
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && mKeyVolumeUpFlag) {
                mReaderActivity.previousPage();
                mKeyVolumeUpFlag = false;
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_MENU && mKeyMenuFlag) {
                if (isShowing()) {
                    dismiss();
                } else {
                    show();
                }
                mKeyMenuFlag = false;
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
