package com.sanqiwan.reader.preload;

import com.sanqiwan.reader.model.BookItem;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-8-6
 * Time: 下午3:12
 * To change this template use File | Settings | File Templates.
 */
public class LatestBookLoadTask extends OnlineBookLoadTask {

    private int mCategoryId;
    private int mPageIndex;

    public LatestBookLoadTask(int categoryId, int pageIndex, OnlineBookCallback callback) {
        super(callback);
        mCategoryId = categoryId;
        mPageIndex = pageIndex;
    }

    @Override
    protected List<BookItem> doInBackground(Void... params) {
        BookWebService bookWebService = new BookWebService();
        try {
            return bookWebService.getNewUpdateBooksByCategoryId(mCategoryId, mPageIndex);
        } catch (ZLNetworkException e) {
        }
        return null;
    }
}
