package com.sanqiwan.reader.engine;

import com.sanqiwan.reader.model.Chapter;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/26/13
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IReaderClient {
    public void onChapterLoadStarted(int index);
    public void onChapterLoadFinished(int index, Chapter chapter);
    public void onPageSelected(int index);
}
