package com.sanqiwan.reader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.TOCAdapter;
import com.sanqiwan.reader.data.HistoryManager;
import com.sanqiwan.reader.data.XBookManager;
import com.sanqiwan.reader.engine.*;
import com.sanqiwan.reader.model.*;
import com.sanqiwan.reader.preference.Settings;
import com.sanqiwan.reader.preload.*;
import com.sanqiwan.reader.util.BatteryUtil;
import com.sanqiwan.reader.menu.ReaderMenuDialog;
import com.sanqiwan.reader.menu.ReaderSettingsView;
import com.sanqiwan.reader.view.SubscriptionDialog;
import com.sanqiwan.reader.view.TouchLayout;


/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/29/13
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderActivity extends BaseReaderActivity implements View.OnClickListener {


    private static BaseReaderActivity sReaderActivity;
    private static final String KEY_BOOK_ID = "key_book_id";
    private static final String KEY_FROM_LOGIN = "key_from_login";
    private static final String KEY_CHAPTER_ID = "key_chapter_id";

    private boolean mKeyVolumeDownFlag = false;
    private boolean mKeyVolumeUpFlag = false;
    private boolean mKeyMenuFlag = false;

    private IReader mReader;

    private DrawerLayout mDrawerLayout;
    private ListView mTOCListView;
    private TOCAdapter mTOCAdapter;
    private TouchLayout mContainer;
    private ProgressBar mProgressBar;
    private TextView mEmptyView;
    private ViewGroup mSubscriptionLayout;
    private ReaderMenuDialog mReaderMenuDialog;

    private HistoryManager mHistoryManager;

    private Settings mSettings;

    private XBook mBook;
    private long mBookId;
    private long mChapterId;
    private Subscriber mSubscriber;
    private XBookManager mBookManager;
    private ImageView mFirstReadingGuide;

    public static void openBook(Context context, long bookId, long chapterId) {
        Intent intent = getOpenBookIntent(context, bookId, chapterId);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static void openBook(Context context, long bookId) {
        openBook(context, bookId, 0);
    }
    public static void startActivityFromLogin(Context context) {
        Intent intent = new Intent(context, ReaderActivity.class);
        intent.putExtra(KEY_FROM_LOGIN, true);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static Intent getOpenBookIntent(Context context, long bookId, long chapterId) {
        Intent intent = new Intent(context, ReaderActivity.class);
        intent.putExtra(KEY_BOOK_ID, bookId);
        intent.putExtra(KEY_CHAPTER_ID, chapterId);
        return intent;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sReaderActivity = ReaderActivity.this;
        mHistoryManager = new HistoryManager();
        mBookManager = new XBookManager();
        mSubscriber = new Subscriber(mSubscriberCallback);
        mSettings = Settings.getInstance();
        initSettings();
        setContentView(R.layout.reader_layout);
        initViews();
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        restoreActivity();
        if (intent.getBooleanExtra(KEY_FROM_LOGIN, false)) {
            if (mReader != null) {
                mReader.refresh();
            }
        }
    }

    private void restoreActivity() {
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if (!intent.hasExtra(KEY_BOOK_ID)) {
            return;
        }
        mBookId = intent.getLongExtra(KEY_BOOK_ID, 0);
        mChapterId = intent.getLongExtra(KEY_CHAPTER_ID, 0);
        if (mChapterId == 0) {
            HistoryItem historyItem = mHistoryManager.getHistoryItem(mBookId);
            if (historyItem != null) {
                mChapterId = historyItem.getChapterId();
            }
        }

        BookLoader bookLoader = createBookLoader();
        bookLoader.executeLoad();
    }

    private BookLoader createBookLoader() {
        String bookPath = new XBookManager().getBookPath(mBookId);
        BookLoader bookLoader;
        if (TextUtils.isEmpty(bookPath)) {
            bookLoader = new OnlineBookLoader(mBookId, new BookLoadCallback(mChapterId));
        } else {
            bookLoader = new LocalBookLoader(bookPath, new BookLoadCallback(mChapterId));
        }
        return bookLoader;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(KEY_BOOK_ID, mBookId);
        outState.putLong(KEY_CHAPTER_ID, mChapterId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BatteryUtil.startMonitor(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BatteryUtil.stopMonitor(this);
    }

    private SubscriptionDialog.Callback mCallback = new SubscriptionDialog.Callback() {
        @Override
        public void onSubscribed() {
            mReader.refresh();
        }

        @Override
        public void onSubscribeFailed() {
        }

        @Override
        public void onRecharged() {
            MainActivity.openPaymentFragmentFromActivity(ReaderActivity.this);
        }
    };

    private class BookLoadCallback implements BookLoader.Callback {

        private long mChapterId;

        private BookLoadCallback(long chapterId) {
            mChapterId = chapterId;
        }

        @Override
        public void onBookLoaded(XBook book) {
            setBook(book);
            TOC toc = book.getTOC();
            mTOCAdapter.setTOC(toc);
            int index = 0;
            if (mChapterId != 0) {
                index = toc.getItemIndexById(mChapterId);
            }
            mReader.gotoChapter(index);
            mTOCListView.setSelection(index);
            mTOCAdapter.setCurrentIndex(index);
        }

        @Override
        public void onBookLoadFailed(long bookId) {
            if (mReader.isEmpty()) {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    private void initViews() {
        mContainer = (TouchLayout) findViewById(R.id.reader_content);
        mContainer.setTouchCenterListener(mTouchCenterListener);

        mSubscriptionLayout = (ViewGroup) findViewById(R.id.subscription_tips_layout);
        findViewById(R.id.subscription).setOnClickListener(this);
        mContainer.setExceptionView(mSubscriptionLayout);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_layout);
        mDrawerLayout.setScrimColor(getResources().getColor(R.color.translucent));

        mTOCListView = (ListView) findViewById(R.id.toc_list);
        mTOCAdapter = new TOCAdapter(this);
        mTOCListView.setAdapter(mTOCAdapter);
        mTOCListView.setOnItemClickListener(mTOCItemClickListener);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mEmptyView = (TextView) findViewById(R.id.empty_view);

        setupReaderView(mSettings.isReaderPageMode());

        mFirstReadingGuide = (ImageView) findViewById(R.id.new_guide);
        if (mSettings.isFirstReading()) {
            mFirstReadingGuide.setVisibility(View.VISIBLE);
            mFirstReadingGuide.setOnClickListener(this);
        } else {
            mFirstReadingGuide.setVisibility(View.GONE);
        }
    }

    //阅读界面设置初始化
    private void initSettings() {
        updateLightMode(mSettings.isReaderBackgroundLightAlways());
        int orientation = mSettings.getReaderOrientation();
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setBrightness(mSettings.getReaderBrightness());
    }

    private FrameLayout.LayoutParams mDefaultParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    private boolean setupReaderView(boolean pagingRead) {
        if (mReader != null) {
            if ((mReader instanceof PagingReader && pagingRead)
                    || (mReader instanceof ScrollingReader && !pagingRead)) {
                return false;
            }
        }
        if (mReader != null) {
            mContainer.removeView(mReader.getView());
            mReader.destroy();
        }

        if (pagingRead) {
            mReader = new PagingReader(this);
        } else {
            mReader = new ScrollingReader(this);
        }

        mReader.setChapterLoader(mSubscriber);
        mReader.setReaderClient(mReaderClient);
        ReaderSettings readerSettings = mReader.getReaderSettings();
        readerSettings.setFontSize(mSettings.getReaderFontSize());
        View view = mReader.getView();
        mContainer.addView(view, 0, mDefaultParams);
        updateTheme(mSettings.getReaderTheme());
        return true;
    }

    private IReaderClient mReaderClient = new IReaderClient() {
        @Override
        public void onChapterLoadStarted(int index) {
            mProgressBar.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            mSubscriptionLayout.setVisibility(View.GONE);
        }

        @Override
        public void onChapterLoadFinished(int index, Chapter chapter) {
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageSelected(int index) {
            mTOCListView.setSelection(mReader.getChapterIndex());
            mTOCAdapter.setCurrentIndex(mReader.getChapterIndex());
            if (mBook != null) {
                HistoryItem historyItem = mHistoryManager.getHistoryItem(mBook.getBookId());
                VolumeItem volumeItem = mBook.getTocItemByIndex(index);
                if (historyItem == null) {
                    historyItem = new HistoryItem();
                    historyItem.setBookId(mBook.getBookId());
                    historyItem.setBookName(mBook.getBookDetail().getBookName());
                    historyItem.setChapterId(volumeItem.getId());
                    historyItem.setChapterName(volumeItem.getName());
                    historyItem.setTime(System.currentTimeMillis());
                    String cover = mBook.getBookDetail().getBookCover();
                    if (!TextUtils.isEmpty(cover)){
                        historyItem.setCover(cover);
                    }
                    mHistoryManager.addHistoryItem(historyItem);
                } else if (historyItem.getChapterId() != volumeItem.getId()) {
                    historyItem.setChapterId(volumeItem.getId());
                    historyItem.setChapterName(volumeItem.getName());
                    historyItem.setTime(System.currentTimeMillis());
                    String cover = mBook.getBookDetail().getBookCover();
                    if (!TextUtils.isEmpty(cover)){
                        historyItem.setCover(cover);
                    }
                    mHistoryManager.updateHistoryItem(historyItem);
                }
            }
        }
    };

    private Subscriber.Callback mSubscriberCallback = new Subscriber.Callback() {

        @Override
        public void onSubscribeFailed(long bookId, int index, boolean showTips) {
            if (index == mReader.getChapterIndex()) {
                if (showTips) {
                    showSubscriptionTips();
                } else {
                    if (mReader.isEmpty()) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        }

        @Override
        public void onChapterLoaded(Chapter chapter) {
            mBook.addChapter(chapter);
            mReader.onChapterLoaded(chapter);
        }

        @Override
        public void onChapterLoadFailed(long bookId, int index) {
            if (index == mReader.getChapterIndex()) {
                mEmptyView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        }

        @Override
        public void onShowVipTips(long bookId, int index) {
            if (index == mReader.getChapterIndex()) {
                showVipTips();
            }
        }
    };

    AdapterView.OnItemClickListener mTOCItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            mDrawerLayout.closeDrawer(mTOCListView);
            parent.post(new Runnable() {
                @Override
                public void run() {
                    mReader.gotoChapter(position);
                }
            });
        }
    };

    public String getBookTitle() {
        if (mBook != null) {
            return mBook.getBookDetail().getBookName();
        }
        return null;
    }
    public void toggleToc() {
        if (mDrawerLayout.isDrawerOpen(mTOCListView)) {
            mDrawerLayout.closeDrawer(mTOCListView);
        } else {
            mDrawerLayout.openDrawer(mTOCListView);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mSettings.isReaderVolumePaging()) {
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
        if (mSettings.isReaderVolumePaging()) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && mKeyVolumeDownFlag) {
                mReader.nextPage();
                mKeyVolumeDownFlag = false;
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && mKeyVolumeUpFlag) {
                mReader.previousPage();
                mKeyVolumeUpFlag = false;
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_MENU && mKeyMenuFlag) {
                toggleMenu();
                mKeyMenuFlag = false;
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.subscription:
                if (UserManager.getInstance().isLogin()) {
                    showSubscriptionTips();
                } else {
                    MainActivity.gotoLogin(ReaderActivity.this, MineFragment.ACTION_FOR_LOGIN);
                }
                break;
            case R.id.new_guide:
                mSettings.setFirstReading(false);
                mFirstReadingGuide.setVisibility(View.GONE);
                break;
        }
    }

    private TouchLayout.TouchCenterListener mTouchCenterListener = new TouchLayout.TouchCenterListener() {
        @Override
        public void onTouchCenter() {
            toggleMenu();
        }
    };

    private void toggleMenu() {
        if (mReaderMenuDialog == null) {
            mReaderMenuDialog = new ReaderMenuDialog(this);
        }
        if (mReaderMenuDialog.isShowing()) {
            mReaderMenuDialog.dismiss();
        } else {
            mReaderMenuDialog.show();
        }
    }

    private void setBook(XBook book) {
        mBook = book;
        mSubscriber.setBook(book);
        mReader.openBook(book);
    }

    private void showVipTips() {
        mReader.clear();
        mEmptyView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mSubscriptionLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MineFragment.ACTION_FOR_LOGIN && resultCode == RESULT_OK) {
            mReader.refresh();
        }
    }

    private void showSubscriptionTips() {
        mProgressBar.setVisibility(View.GONE);
        long chapterId = mBook.getTOC().getItem(mReader.getChapterIndex()).getId();
        SubscriptionDialog subscriptionDialog = new SubscriptionDialog(this, mBookId, chapterId);
        subscriptionDialog.setCallback(mCallback);
        subscriptionDialog.show();
        mSubscriptionLayout.setVisibility(View.VISIBLE);
    }

    private void setBrightnessAuto() {
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.screenBrightness = -1.0f;
        getWindow().setAttributes(attrs);
    }

    public void setBrightness(int percent) {
        if (percent < 1) {
            percent = 1;
        } else if (percent > 100) {
            percent = 100;
        }
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.screenBrightness = percent / 100.0f;
        getWindow().setAttributes(attrs);
        mSettings.setReaderBrightness(percent);
    }

    public int getBrightness() {
        final int level = (int) (100 * getWindow().getAttributes().screenBrightness);
        return (level >= 0) ? level : 50;
    }

    public void updateReadProgress(int progress) {
        mReader.gotoChapter(progress);
    }

    private static final int STEP = 2;

    private static final float MIN_FONT_SIZE = 12;
    private static final float MAX_FONT_SIZE = 30;

    public void zoomIn() {
        ReaderSettings readerSettings = mReader.getReaderSettings();
        float fontSize = readerSettings.getFontSize();
        if (fontSize < MAX_FONT_SIZE) {
            readerSettings.setFontSize(fontSize + STEP);
            mSettings.setReaderFontSize(readerSettings.getFontSize());
        }
    }

    public void zoomOut() {
        ReaderSettings readerSettings = mReader.getReaderSettings();
        float fontSize = readerSettings.getFontSize();
        if (fontSize > MIN_FONT_SIZE) {
            readerSettings.setFontSize(fontSize - STEP);
            mSettings.setReaderFontSize(readerSettings.getFontSize());
        }
    }

    public void updateReadMode(ReaderSettingsView.ReadMode readMode) {
        int index = mReader.getChapterIndex();
        boolean changed;
        if (readMode == ReaderSettingsView.ReadMode.Paging) {
            changed = setupReaderView(true);
            mSettings.setReaderPageMode(true);
        } else {
            changed = setupReaderView(false);
            mSettings.setReaderPageMode(false);
        }
        if (changed) {
            mReader.openBook(mBook);
            mReader.gotoChapter(index);
        }
    }

    public void updateOrientation(ReaderSettingsView.Orientation orientation) {
        int nowOrientation = this.getResources().getConfiguration().orientation;
        if (orientation == ReaderSettingsView.Orientation.Horizontal
                && nowOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mSettings.setReaderOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        if (orientation == ReaderSettingsView.Orientation.Vertical
                && nowOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mSettings.setReaderOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void updateLightMode(boolean alwaysOn) {
        if (alwaysOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        mSettings.setReaderBackgroundLightAlways(alwaysOn);
    }

    public void updateVolumePaging(boolean intercept) {
        mSettings.setReaderVolumePaging(intercept);
    }

    public void nextPage() {
        mReader.nextPage();
    }

    public void previousPage() {
        mReader.previousPage();
    }

    public void updateTheme(String themeName) {
        ThemeProfile theme = ThemeProfile.get(themeName);
        if (theme.isUsable()) {
            ReaderSettings readerSettings = mReader.getReaderSettings();
            readerSettings.setTextColor(theme.getTextColor());
            readerSettings.setWallpaper(theme.getBackgroundDrawbale());
            readerSettings.setPageShadow(theme.getPageShadow());
            readerSettings.setSecondaryTextColor(theme.getTipTextColor());
            mTOCListView.setBackgroundDrawable(theme.getBackgroundDrawbale());
            mTOCListView.setDivider(theme.getTocLineDrawable());
            mTOCAdapter.setTocTextColor(theme.getTocTextColor());
            mSettings.setReaderTheme(themeName);
        }
    }

    public void nextChapter() {
        mReader.nextChapter();
    }

    public void previousChapter() {
        mReader.previousChapter();
    }

    public int getProgress() {
        return mReader.getChapterIndex();
    }

    public int getMax() {
        if (mBook != null) {
            //需要的值是0 ～ size-1
            return mBook.getTOC().getSize() - 1;
        }
        return 0;
    }

    public String getTitle(int progress) {
        if (mBook != null) {
            return mBook.getTOC().getItem(progress).getName();
        }
        return null;
    }

    public static void finishReaderActivity() {
        if (sReaderActivity != null) {
            sReaderActivity.finish();
        }
    }

    @Override
    protected void onDestroy() {
        sReaderActivity = null;
        super.onDestroy();
    }

    @Override
    public void finish() {
        MainActivity.startMainActivity(ReaderActivity.this);
        super.finish();
    }
}
