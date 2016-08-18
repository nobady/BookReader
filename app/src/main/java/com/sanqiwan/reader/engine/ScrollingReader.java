package com.sanqiwan.reader.engine;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.model.Chapter;
import com.sanqiwan.reader.model.XBook;
import com.sanqiwan.reader.util.UIUtil;

import java.util.Observable;
import java.util.Observer;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/12/13
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScrollingReader implements IReader, RequestNextChapterListener, Observer {

    private static final int MARGIN = UIUtil.dipToPixel(40);

    private Context mContext;
    private XBook mBook;
    private ReaderView mReaderView;
    private int mCurrentChapterIndex;
    private IReaderClient mReaderClient;

    private IChapterLoader mChapterLoader;

    public ScrollingReader(Context context) {
        mContext = context;
        getReaderSettings().addObserver(this);
    }

    private boolean isLastChapter() {
        if (mBook == null) {
            return false;
        }
        return mCurrentChapterIndex == (mBook.getTOC().getSize() - 1);
    }

    private boolean isFirstChapter() {
        return mCurrentChapterIndex == 0;
    }

    @Override
    public void nextChapter() {
        if (mBook == null) {
            Toast.makeText(mContext, R.string.loading_chapter, Toast.LENGTH_SHORT).show();
            return;
        }
        if (isLastChapter()) {
            Toast.makeText(mContext, R.string.lastchapter, Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentChapterIndex++;
        loadChapter();
    }

    @Override
    public void previousChapter() {
        if (mBook == null) {
            Toast.makeText(mContext, R.string.loading_chapter, Toast.LENGTH_SHORT).show();
            return;
        }
        if (isFirstChapter()) {
            Toast.makeText(mContext, R.string.firstchapter, Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentChapterIndex--;
        loadChapter();
    }

    @Override
    public void gotoChapter(int index) {
        if (mBook == null) {
            return;
        }
        if (index >= 0 && index < mBook.getTOC().getSize()) {
            mCurrentChapterIndex = index;
            loadChapter();
        }
    }

    @Override
    public void gotoChapterById(long chapterId) {
        if (mBook == null) {
            return;
        }
        int index = mBook.getTOC().getItemIndexById(chapterId);
        gotoChapter(index);
    }

    @Override
    public void refresh() {
        gotoChapter(mCurrentChapterIndex);
    }

    private void loadChapter() {

        mReaderView.clear();
        long chapterId = mBook.getTOC().getItem(mCurrentChapterIndex).getId();
        if (mReaderClient != null) {
            mReaderClient.onChapterLoadStarted(mCurrentChapterIndex);
            mReaderClient.onPageSelected(mCurrentChapterIndex);
        }
        mChapterLoader.loadChapter(0, chapterId, null);
    }

    @Override
    public void nextPage() {
        if (mReaderView != null) {
            if (mReaderView.isAtBottomPosition()) {
                nextChapter();
            } else {
                mReaderView.smoothScrollBy(0, mReaderView.getHeight() - MARGIN);
            }
        }
    }

    @Override
    public void previousPage() {
        if (mReaderView != null) {
            if (mReaderView.isAtTopPosition()) {
                previousChapter();
            } else {
                mReaderView.smoothScrollBy(0, -(mReaderView.getHeight() - MARGIN));
            }
        }
    }

    @Override
    public void openBook(XBook book) {
        mBook = book;
    }

    @Override
    public void onChapterLoaded(Chapter chapter) {
        int index = mBook.getTOC().getItemIndexById(chapter.getChapterId());
        mReaderView.setText(chapter, isLastChapter());
        if (index == mCurrentChapterIndex && mReaderClient != null) {
            mReaderClient.onChapterLoadFinished(index, chapter);
        }
    }

    @Override
    public int getChapterIndex() {
        return mCurrentChapterIndex;
    }

    @Override
    public View getView() {

        if (mReaderView == null) {
            mReaderView = new ReaderView(mContext);
            mReaderView.setRequestNextChapterListener(this);
            update(null, null);
        }
        if (mReaderView.getParent() != null) {
            return mReaderView;
        }
        mReaderView.setTextSize(getReaderSettings().getFontSize());
        return mReaderView;
    }

    @Override
    public boolean isEmpty() {
        return mReaderView.isEmpty();
    }

    @Override
    public void clear() {
        mReaderView.clear();
    }

    @Override
    public void setChapterLoader(IChapterLoader chapterLoader) {
        mChapterLoader = chapterLoader;
    }

    @Override
    public void setUpdateHistoryHandle(IUpdateHistory updateHistoryHandle) {
    }

    @Override
    public void setReaderClient(IReaderClient readerClient) {
        mReaderClient = readerClient;
    }

    @Override
    public ReaderSettings getReaderSettings() {
        return ReaderSettings.getInstance();
    }

    @Override
    public void destroy() {
        getReaderSettings().deleteObserver(this);
    }

    @Override
    public void onRequestNextChapter() {
        nextChapter();
    }

    @Override
    public void update(Observable observable, Object data) {
        ReaderSettings readerSettings = getReaderSettings();
        if (mReaderView != null) {
            mReaderView.setBackgroundDrawable(readerSettings.getWallpaper());
            mReaderView.setTextColor(readerSettings.getTextColor());
            mReaderView.setTextSize(readerSettings.getFontSize());
        }
    }
}
