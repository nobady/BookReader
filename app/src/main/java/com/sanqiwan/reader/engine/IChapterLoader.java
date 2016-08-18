package com.sanqiwan.reader.engine;

import com.sanqiwan.reader.preload.ChapterLoader;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/13/13
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IChapterLoader {

    public void loadChapter(long bookId, long chapterId, ChapterLoader.Callback callback);
}
