package com.sanqiwan.reader.engine;

import android.view.View;
import com.sanqiwan.reader.model.Chapter;
import com.sanqiwan.reader.model.XBook;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/12/13
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IReader {
    public void nextChapter();
    public void previousChapter();
    public void gotoChapter(int index);
    public void gotoChapterById(long chapterId);

    public void refresh();

    public void nextPage();
    public void previousPage();

    public void openBook(XBook book);
    public void onChapterLoaded(Chapter chapter);

    public int getChapterIndex();

    public View getView();

    public boolean isEmpty();

    public void clear();

    public void setChapterLoader(IChapterLoader chapterLoader);

    public void setUpdateHistoryHandle(IUpdateHistory updateHistoryHandle);

    public void setReaderClient(IReaderClient readerClient);

    public ReaderSettings getReaderSettings();

    public void destroy();
}
