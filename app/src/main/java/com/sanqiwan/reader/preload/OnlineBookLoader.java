package com.sanqiwan.reader.preload;

import com.sanqiwan.reader.cache.TOCCache;
import com.sanqiwan.reader.model.BookContent;
import com.sanqiwan.reader.model.BookDetail;
import com.sanqiwan.reader.model.TOC;
import com.sanqiwan.reader.model.XBook;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/15/13
 * Time: 10:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class OnlineBookLoader extends BookLoader {

    public OnlineBookLoader(long bookId, Callback callback) {
        super(bookId, callback);
    }

    @Override
    protected XBook doInBackground() {

        long bookId = getBookId();
        XBook book = new XBook();
        BookWebService bookWebService = new BookWebService();
        // book detail
        BookDetail bookDetail = null;
        try {
            bookDetail = bookWebService.getBookDetail(bookId);
        } catch (ZLNetworkException e) {
        }
        if (bookDetail == null) {
            return null;
        }
        book.setBookDetail(bookDetail);
        book.setBookId(bookDetail.getBookId());
        // TOC
        // TODO: 处理目录分页的情形
        TOC toc = TOCCache.getInstance().getTOC(bookId, -1);
        if (toc == null) {
            try {
                toc = bookWebService.getTOC(bookId);
            } catch (ZLNetworkException e) {
            }
            TOCCache.getInstance().addTOC(toc, -1);
        }
        if (toc == null) {
            return null;
        }
        book.setTOC(toc);
        book.setBookContent(new BookContent());
        return book;
    }
}
