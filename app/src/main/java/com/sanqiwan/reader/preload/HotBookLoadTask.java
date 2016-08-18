package com.sanqiwan.reader.preload;

import com.sanqiwan.reader.model.BookItem;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.List;

/**
 * User: IBM
 * Date: 13-8-6
 * Time: 下午3:12
 */
public class HotBookLoadTask extends OnlineBookLoadTask {

    private int mCategoryId;
    private String mType;
    private int mPageIndex;

    public HotBookLoadTask(int categoryId, String type, int pageIndex, OnlineBookCallback callback) {
        super(callback);
        mCategoryId = categoryId;
        mType = type;
        mPageIndex = pageIndex;
    }

    @Override
    protected List<BookItem> doInBackground(Void... params) {
        BookWebService bookWebService = new BookWebService();
        try {
            return bookWebService.getNewTopBookItemsByCategoryId(mCategoryId, mType, mPageIndex);
        } catch (ZLNetworkException e) {
        }
        return null;
    }
}
