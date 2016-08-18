package com.sanqiwan.reader.engine;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.model.Chapter;
import com.sanqiwan.reader.model.XBook;
import org.geometerplus.zlibrary.ui.android.view.ZLAndroidWidget;

import java.util.Observable;
import java.util.Observer;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/12/13
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class PagingReader implements IReader, Observer {

    public enum Indicator {
        FistChapter,
        LastChapter,
    }

    private XBook mBook;
    private SlideListView mSlideListView;
    private Context mContext;
    private ReaderAdapter mReaderAdapter;
    private IReaderClient mReaderClient;
    private IChapterLoader mChapterLoader;

    public PagingReader(Context context) {
        mContext = context;
        getReaderSettings().addObserver(this);
    }

    @Override
    public void nextChapter() {
        if (mBook == null) {
            Toast.makeText(mContext, R.string.loading_chapter, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mSlideListView.canScrollHorizontally(1)) {
            Toast.makeText(mContext, R.string.lastchapter, Toast.LENGTH_SHORT).show();
            return;
        }
        mSlideListView.setCurrentItem(mSlideListView.getCurrentItem() + 1);
    }

    @Override
    public void previousChapter() {
        if (mBook == null) {
            Toast.makeText(mContext, R.string.loading_chapter, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mSlideListView.canScrollHorizontally(-1)) {
            Toast.makeText(mContext, R.string.firstchapter, Toast.LENGTH_SHORT).show();
            return;
        }

        mSlideListView.setCurrentItem(mSlideListView.getCurrentItem() - 1);
    }

    @Override
    public void gotoChapter(int index) {
        if (mBook == null) {
            return;
        }
        if (index >= 0 && index < mBook.getTOC().getSize()) {
            mSlideListView.setCurrentItem(index);
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
        if (mReaderAdapter != null) {
            mReaderAdapter.refresh(mSlideListView.getCurrentItem());
        }
    }

    @Override
    public void nextPage() {
        ZLAndroidWidget widget = mReaderAdapter.getView(mSlideListView.getCurrentItem());
        if (widget.canScrollToRight()) {
            widget.nextPage();
        } else {
            nextChapter();
        }
    }

    @Override
    public void previousPage() {
        ZLAndroidWidget widget = mReaderAdapter.getView(mSlideListView.getCurrentItem());
        if (widget.canScrollToLeft()) {
            widget.previousPage();
        } else {
            previousChapter();
        }
    }

    @Override
    public void openBook(XBook book) {
        mBook = book;
        mReaderAdapter.setBook(book);
    }

    @Override
    public void onChapterLoaded(Chapter chapter) {
        int index = mBook.getTOC().getItemIndexById(chapter.getChapterId());
        mReaderAdapter.onChapterLoaded(index, chapter);
        if (index == mSlideListView.getCurrentItem() && mReaderClient != null) {
            mReaderClient.onChapterLoadFinished(index, chapter);
        }
    }

    @Override
    public int getChapterIndex() {
        return mSlideListView.getCurrentItem();
    }

    @Override
    public View getView() {

        if (mSlideListView == null) {
            mSlideListView = new SlideListView(mContext);
            mReaderAdapter = new ReaderAdapter(mContext);
            mSlideListView.setAdapter(mReaderAdapter);
            mReaderAdapter.setReaderClient(mReaderClient);
            mReaderAdapter.setChapterLoader(mChapterLoader);
            mSlideListView.setOnPageChangeListener(new SlideListView.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (mReaderClient != null && mBook != null) {
                        ZLAndroidWidget view = mReaderAdapter.getView(position);
                        mReaderAdapter.notifyChapterLoadStart(position);
                        if (view != null && !view.isEmpty()) {
                            mReaderClient.onChapterLoadFinished(position, mBook.getChapterByIndex(position));
                        } else {
                            mReaderAdapter.loadChapter(position);
                        }
                        mReaderClient.onPageSelected(position);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }

        return mSlideListView;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public void setChapterLoader(IChapterLoader chapterLoader) {
        mChapterLoader = chapterLoader;
        if (mReaderAdapter != null) {
            mReaderAdapter.setChapterLoader(chapterLoader);
        }
    }

    @Override
    public void setUpdateHistoryHandle(IUpdateHistory updateHistoryHandle) {
    }

    @Override
    public void setReaderClient(IReaderClient readerClient) {
        mReaderClient = readerClient;
        if (mReaderAdapter != null) {
            mReaderAdapter.setReaderClient(readerClient);
        }
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
    public void update(Observable observable, Object data) {
        if (mReaderAdapter != null) {
            mReaderAdapter.repaint();
        }
    }
}
