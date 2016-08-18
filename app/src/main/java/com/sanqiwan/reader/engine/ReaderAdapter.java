package com.sanqiwan.reader.engine;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.sanqiwan.reader.model.Chapter;
import com.sanqiwan.reader.model.XBook;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.zlibrary.ui.android.view.ZLAndroidWidget;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/25/13
 * Time: 7:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderAdapter extends PagerAdapter {

    private XBook mBook;
    private IChapterLoader mChapterLoader;
    private Map<Integer, ZLAndroidWidget> mViews;
    private Context mContext;
    private IReaderClient mReaderClient;

    public ReaderAdapter(Context context) {
        mContext = context;
        mViews = new HashMap<Integer, ZLAndroidWidget>();
    }

    public void setBook(XBook book) {
        mBook = book;
        notifyDataSetChanged();
    }

    public void setChapterLoader(IChapterLoader chapterLoader) {
        mChapterLoader = chapterLoader;
    }

    public void setReaderClient(IReaderClient readerClient) {
        mReaderClient = readerClient;
    }

    @Override
    public int getCount() {
        if (mBook != null) {
            return mBook.getTOC().getSize();
        }
        return 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ZLAndroidWidget widget = mViews.get(position);
        if (widget == null) {
            widget = new ZLAndroidWidget(mContext);
            mViews.put(position, widget);
            if (position == 0) {
                widget.setIndicator(PagingReader.Indicator.FistChapter);
            } else if (position == getCount() - 1) {
                widget.setIndicator(PagingReader.Indicator.LastChapter);
            }
//            if (position == ((SlideListView) container).getCurrentItem()) {
//                notifyChapterLoadStart(position);
//            }
//
//            loadChapter(position);
        }
        container.addView(widget);
        return widget;
    }

    public void notifyChapterLoadStart(int position) {
        if (mReaderClient != null) {
            mReaderClient.onChapterLoadStarted(position);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
        mViews.remove(position);
        System.gc();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    public void onChapterLoaded(int index, Chapter chapter) {
        Book book = new ChapterReader().createBookForChapter(chapter);
        book.setIndex(index);
        ZLAndroidWidget view = mViews.get(index);
        if (view != null) {
            view.openBook(book);
        }
    }

    public void refresh(int index) {
        notifyChapterLoadStart(index);
        loadChapter(index);
    }

    public void loadChapter(int index) {
        if (mChapterLoader != null) {
            long chapterId = mBook.getTOC().getItem(index).getId();
            mChapterLoader.loadChapter(0, chapterId, null);
        }
    }

    public ZLAndroidWidget getView(int index) {
        return mViews.get(index);
    }

    public void repaint() {
        for (ZLAndroidWidget view : mViews.values()) {
            view.clearTextCaches();
            view.repaint();
        }
    }
}
