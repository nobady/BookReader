package com.sanqiwan.reader.preload;

import com.sanqiwan.reader.model.XBook;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 12/2/13
 * Time: 5:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocalBookLoader extends BookLoader {

    private String mBookPath;

    public LocalBookLoader(String bookPath, Callback callback) {
        super(0, callback);
        mBookPath = bookPath;
    }

    @Override
    protected XBook doInBackground() {
        return XBook.createBookFromFile(mBookPath);
    }
}
