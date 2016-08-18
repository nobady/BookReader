package com.sanqiwan.reader.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.sanqiwan.reader.model.BookRecommendItem;
import com.sanqiwan.reader.model.Topic;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Sam
 * Date: 13-9-23
 * Time: 下午4:34
 */
public class BookRecommendManager {
    private final static String LOG_TAG = "BookRecommendManager";
    private final static int MAX_RECOMMEND_COUNT = 6;
    private Context mContext;

    public BookRecommendManager(Context context) {
        mContext = context;
    }

    public void addBookRecommend(BookRecommendItem item) {
        ContentValues values = new ContentValues();
        values.put(BookRecommendTable.BOOK_ID, item.getBookId());
        values.put(BookRecommendTable.COVER_URL, item.getPictureUrl());
        values.put(BookRecommendTable.BOOKRECOMMEND_CREATETIME, item.getRefreshTime());
        values.put(BookRecommendTable.CATEGORY_ID, item.getCategoryId());

        mContext.getContentResolver().insert(ReaderUris.BOOKRECOMMEND_URI, values);
    }

    public void addBookRecommendList(List<BookRecommendItem> bookRecommendList) {
        for (BookRecommendItem item : bookRecommendList) {
            addBookRecommend(item);
        }
    }

    public BookRecommendItem getBookRecommendById(int id) {
        Cursor cursor = mContext.getContentResolver().query(ReaderUris.BOOKRECOMMEND_URI, null,
                BookRecommendTable.BOOK_ID + "=?", new String[]{String.valueOf(id)}, null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    BookRecommendItem item = new BookRecommendItem();
                    item.setBookId(cursor.getInt(cursor.getColumnIndex(BookRecommendTable.BOOK_ID)));
                    item.setPictureUrl(cursor.getString(cursor.getColumnIndex(BookRecommendTable.COVER_URL)));
                    item.setRefreshTime(cursor.getLong(cursor.getColumnIndex(BookRecommendTable.BOOKRECOMMEND_CREATETIME)));
                    item.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(BookRecommendTable.CATEGORY_ID)));
                    return item;
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public Cursor getBookRecommendByCategoryId(int categoryId) {
        return mContext.getContentResolver().query(ReaderUris.BOOKRECOMMEND_URI, null,
                BookRecommendTable.CATEGORY_ID + "=?", new String[]{String.valueOf(categoryId)}, BookRecommendTable.BOOKRECOMMEND_CREATETIME+" desc");
    }

    public Cursor getAllBookRecommends() {
        return mContext.getContentResolver().query(ReaderUris.BOOKRECOMMEND_URI, null, null, null, BookRecommendTable.BOOKRECOMMEND_CREATETIME+" desc");
    }

    public int deleteBookRecommend(int bookId) {
        return mContext.getContentResolver().delete(ReaderUris.BOOKRECOMMEND_URI,
                BookRecommendTable.BOOK_ID + "=?", new String[]{String.valueOf(bookId)});
    }

    public int deleteBookRecommendById(int id) {
        return mContext.getContentResolver().delete(ReaderUris.BOOKRECOMMEND_URI,
                BookRecommendTable._ID + "=?", new String[]{String.valueOf(id)});
    }

    public int deleteAllBookRecommend() {
        return mContext.getContentResolver().delete(ReaderUris.BOOKRECOMMEND_URI, null, null);
    }

    public int updateBookRecommend(BookRecommendItem item) {
        ContentValues values = new ContentValues();
        values.put(BookRecommendTable.BOOK_ID, item.getBookId());
        values.put(BookRecommendTable.COVER_URL, item.getPictureUrl());
        values.put(BookRecommendTable.BOOKRECOMMEND_CREATETIME, item.getRefreshTime());
        values.put(BookRecommendTable.CATEGORY_ID, item.getCategoryId());
        String[] id = new String[]{String.valueOf(item.getBookId())};
        String where = BookRecommendTable.BOOK_ID + " =?";
        return mContext.getContentResolver().update(ReaderUris.BOOKRECOMMEND_URI, values, where, id);
    }

    public void deleteOldBookRecommend() {
        Cursor c = getAllBookRecommends();
        if (c != null) {
            try {
                if (c.getCount() > MAX_RECOMMEND_COUNT) {
                    for (int i = MAX_RECOMMEND_COUNT+1; i <= c.getCount(); i++) {
                        if (c.move(i)) {
                            int id = c.getInt(c.getColumnIndexOrThrow(BookRecommendTable._ID));
                            deleteBookRecommendById(id);
                        }

                    }
                }
            } finally {
                c.close();
            }
        }
    }

    public void deleteOldBookRecommendByCategoryId(int categoryId) {
        Cursor c = getBookRecommendByCategoryId(categoryId);
        if (c != null) {
            try {
                if (c.getCount() > MAX_RECOMMEND_COUNT) {
                    for (int i = MAX_RECOMMEND_COUNT+1; i <= c.getCount(); i++) {
                        if (c.move(i)) {
                            int id = c.getInt(c.getColumnIndexOrThrow(BookRecommendTable._ID));
                            deleteBookRecommendById(id);
                        }
                    }
                }
            } finally {
                c.close();
            }
        }
    }
}
