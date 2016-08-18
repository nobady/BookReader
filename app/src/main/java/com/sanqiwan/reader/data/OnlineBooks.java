package com.sanqiwan.reader.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.sanqiwan.reader.model.BookItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/22/13
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class OnlineBooks {
    public static final String[] NEW_UPDATE_PROJECTION = new String[]{"book_id", "book_name", "finish", "book_cover",
            "author", "download", "update_date"};
    public static final String[] NEW_TOP_PROJECTION = new String[]{"book_id", "book_name", "finish", "book_cover",
            "author", "download", "commends"};
    public static final String[] ONLINE_BOOK_PROJECTION = new String[]{"book_id", "book_name", "finish", "book_cover",
            "author", "download", "commends", "update_date", "category"};
    public static final String[] BOOK_ID_PROJECTION = new String[]{"book_id"};
    private Context mContext;

    public OnlineBooks(Context context) {
        mContext = context;
    }

    public BookItem queryOnlineBooks(long bookId) {
        BookItem onlineBookItem;
        Uri uri = ContentUris.withAppendedId(ReaderUris.ONLINE_BOOKS_URI, bookId);
        Cursor cursor = mContext.getContentResolver().query(uri, ONLINE_BOOK_PROJECTION,
                null, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                onlineBookItem = new BookItem();
                onlineBookItem.setBookId(bookId);
                onlineBookItem.setBookName(cursor.getString(1));
                onlineBookItem.setIsFinish(cursor.getInt(2) == 0 ? false : true);
                onlineBookItem.setCover(cursor.getString(3));
                onlineBookItem.setAuthor(cursor.getString(4));
                onlineBookItem.setDownload(cursor.getInt(5) == 0 ? false : true);
                onlineBookItem.setCommends(cursor.getInt(6));
                onlineBookItem.setUpdateDate(cursor.getInt(7));
                onlineBookItem.setCategory(cursor.getString(8));
                cursor.close();
                return onlineBookItem;
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    //查询最近更新的书籍数据
    public List<BookItem> queryLatestBooks(boolean download, String category, int pageNum, int pageSize) {
        String where = "download=" + (download == true ? "1" : "0") + " and update_date!=0 and category='" + category
                + "' order by update_date desc limit " + (pageNum - 1) * pageSize + ", " + pageSize;
        List<BookItem> bookTabList = new ArrayList<BookItem>();
        BookItem bookTab;

        Cursor cursor = mContext.getContentResolver().query(ReaderUris.ONLINE_BOOKS_URI, NEW_UPDATE_PROJECTION,
                where, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    bookTab = new BookItem();
                    bookTab.setBookId(cursor.getLong(0));
                    bookTab.setBookName(cursor.getString(1));
                    bookTab.setIsFinish(cursor.getInt(2) == 0 ? false : true);
                    bookTab.setCover(cursor.getString(3));
                    bookTab.setAuthor(cursor.getString(4));
                    bookTab.setDownload(cursor.getInt(5) == 0 ? false : true);
                    bookTab.setUpdateDate(cursor.getInt(6));
                    bookTab.setCategory(category);
                    bookTabList.add(bookTab);
                    cursor.moveToNext();
                }
                return bookTabList;
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    //查询热门书籍数据
    public List<BookItem> queryHotBooks(boolean download, String category, int pageNum, int pageSize) {
        String where = "download=" + (download == true ? "1" : "0") + " and category='" + category
                + "' order by commends desc limit " + (pageNum - 1) * pageSize + ", " + pageSize;
        List<BookItem> bookTabList = new ArrayList<BookItem>();
        BookItem bookTab;
        Cursor cursor = mContext.getContentResolver().query(ReaderUris.ONLINE_BOOKS_URI, NEW_TOP_PROJECTION,
                where, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    bookTab = new BookItem();
                    bookTab.setBookId(cursor.getLong(0));
                    bookTab.setBookName(cursor.getString(1));
                    bookTab.setIsFinish(cursor.getInt(2) == 0 ? false : true);
                    bookTab.setCover(cursor.getString(3));
                    bookTab.setAuthor(cursor.getString(4));
                    bookTab.setDownload(cursor.getInt(5) == 0 ? false : true);
                    bookTab.setCommends(cursor.getInt(6));
                    bookTab.setCategory(category);
                    bookTabList.add(bookTab);
                    cursor.moveToNext();
                }
                return bookTabList;
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    //鏌ヨ宸茬粡淇濆瓨鍦∣nline_books琛ㄤ腑鐨勪功绫岻D
    public Set<Long> queryNewBooks(String category) {
        String where = "category='" + category + "'";
        Set<Long> bookIDSet = new HashSet<Long>();
        Cursor cursor = mContext.getContentResolver().query(ReaderUris.ONLINE_BOOKS_URI, BOOK_ID_PROJECTION,
                where, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    bookIDSet.add(cursor.getLong(0));
                    cursor.moveToNext();
                }
                return bookIDSet;
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public int getSize() {
        int count = 0;
        Cursor cursor = mContext.getContentResolver().query(ReaderUris.ONLINE_BOOKS_URI, BOOK_ID_PROJECTION,
                null, null, null);
        try {
            if (cursor != null) {
                count = cursor.getCount();
            }
        } finally {
            cursor.close();
        }
        return count;
    }

    public Uri insert(BookItem onlineBookItem) {
        Uri uri = null;
        if (onlineBookItem != null) {
            ContentValues values = new ContentValues();
            values.put(OnlineBookTable.COL_BOOK_ID, onlineBookItem.getBookId());
            values.put(OnlineBookTable.COL_BOOK_NAME, onlineBookItem.getBookName());
            values.put(OnlineBookTable.COL_AUTHOR, onlineBookItem.getAuthor());
            values.put(OnlineBookTable.COL_BOOK_COVER, onlineBookItem.getCover());
            values.put(OnlineBookTable.COL_COMMENDS, onlineBookItem.getCommends());
            values.put(OnlineBookTable.COL_DOWNLOAD, onlineBookItem.getDownload());
            values.put(OnlineBookTable.COL_FINISH, onlineBookItem.isFinish());
            values.put(OnlineBookTable.COL_UPDATE_DATE, onlineBookItem.getUpdateDate());
            values.put(OnlineBookTable.COL_CATEGORY, onlineBookItem.getCategory());

            uri = mContext.getContentResolver().insert(ReaderUris.ONLINE_BOOKS_URI, values);
        }
        return uri;
    }

    public int updateNewTopBooks(BookItem onlineBookItem) {
        int count = -1;
        if (onlineBookItem != null) {
            Uri uri = ContentUris.withAppendedId(ReaderUris.ONLINE_BOOKS_URI, onlineBookItem.getBookId());
            ContentValues values = new ContentValues();
            values.put(OnlineBookTable.COL_BOOK_NAME, onlineBookItem.getBookName());
            values.put(OnlineBookTable.COL_BOOK_COVER, onlineBookItem.getCover());
            values.put(OnlineBookTable.COL_AUTHOR, onlineBookItem.getAuthor());
            values.put(OnlineBookTable.COL_COMMENDS, onlineBookItem.getCommends());
            values.put(OnlineBookTable.COL_FINISH, onlineBookItem.isFinish());
            count = mContext.getContentResolver().update(uri, values, null, null);
        }

        return count;
    }

    public int updateNewUpdateBooks(BookItem onlineBookItem) {
        int count = -1;
        if (onlineBookItem != null) {
            Uri uri = ContentUris.withAppendedId(ReaderUris.ONLINE_BOOKS_URI, onlineBookItem.getBookId());
            ContentValues values = new ContentValues();
            values.put(OnlineBookTable.COL_BOOK_NAME, onlineBookItem.getBookName());
            values.put(OnlineBookTable.COL_BOOK_COVER, onlineBookItem.getCover());
            values.put(OnlineBookTable.COL_AUTHOR, onlineBookItem.getAuthor());
            values.put(OnlineBookTable.COL_FINISH, onlineBookItem.isFinish());
            values.put(OnlineBookTable.COL_UPDATE_DATE, onlineBookItem.getUpdateDate());
            count = mContext.getContentResolver().update(uri, values, null, null);
        }

        return count;
    }

    public int updateDownload(BookItem onlineBookItem) {
        int count = -1;
        if (onlineBookItem != null) {
            Uri uri = ContentUris.withAppendedId(ReaderUris.ONLINE_BOOKS_URI, onlineBookItem.getBookId());
            ContentValues values = new ContentValues();
            values.put(OnlineBookTable.COL_DOWNLOAD, onlineBookItem.getDownload());
            count = mContext.getContentResolver().update(uri, values, null, null);
        }
        return count;
    }

    public int delete(long bookID) {
        int count = -1;
        Uri uri = ContentUris.withAppendedId(ReaderUris.ONLINE_BOOKS_URI, bookID);
        count = mContext.getContentResolver().delete(uri, null, null);
        return count;
    }

    public int delete(String bookIDSet) {
        int count = -1;
        String where = OnlineBookTable.COL_BOOK_ID + " in (" + bookIDSet + ")";
        count = mContext.getContentResolver().delete(ReaderUris.ONLINE_BOOKS_URI, where, null);
        return count;
    }
}
