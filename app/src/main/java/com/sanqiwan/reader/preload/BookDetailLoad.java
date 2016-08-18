package com.sanqiwan.reader.preload;

import com.sanqiwan.reader.util.AsyncTaskUtil;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/26/13
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class BookDetailLoad {

    public BookDetailLoad() {
    }

    public void loadBookDetail(long bookId, BookDetailTask.BookDetailCallback callback) {

        AsyncTaskUtil.execute(new BookDetailTask(bookId, callback));
    }
}
