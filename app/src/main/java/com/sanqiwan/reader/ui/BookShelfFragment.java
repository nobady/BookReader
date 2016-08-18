package com.sanqiwan.reader.ui;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.BucketListAdapter;
import com.sanqiwan.reader.adapter.GridBookAdapter;
import com.sanqiwan.reader.adapter.HistoryGalleryAdapter;
import com.sanqiwan.reader.adapter.ListBookAdapter;
import com.sanqiwan.reader.data.HistoryManager;
import com.sanqiwan.reader.data.XBookManager;
import com.sanqiwan.reader.model.BookItem;
import com.sanqiwan.reader.preference.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/20/13
 * Time: 2:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class BookShelfFragment extends BaseFragment implements View.OnClickListener{

    private ListView mListView;
    private List<BookItem> mBookItems;
    private ImageView mSelectGridView, mSelectListView;
    private BucketListAdapter mListAdapter, mGridAdapter;
    private XBookManager mXBookManager;
    private HistoryManager mHistoryManager;
    private View mHeaderView;
    private Cursor mBookCursor;
    private View mView;
    private Context mContext;

    private ContentObserver mBookObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            resetBookData();
        }
    };

    public static BookShelfFragment newFragment() {
        return new BookShelfFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mContext = getContext();
            mView = inflater.inflate(R.layout.book_shelf, container, false);
            mListView = (ListView) mView.findViewById(R.id.lv_books);
            mHeaderView = inflater.inflate(R.layout.book_shelf_header, null);
            mListView.addHeaderView(mHeaderView);
            mSelectGridView = (ImageView) mHeaderView.findViewById(R.id.grid_view);
            mSelectListView = (ImageView) mHeaderView.findViewById(R.id.list_view);
            mSelectGridView.setOnClickListener(this);
            mSelectListView.setOnClickListener(this);
            mXBookManager = new XBookManager();
            initGallery();
        }
        resetBookData();
        if (mView.getParent() != null) {
            ((ViewGroup) mView.getParent()).removeAllViews();
        }
        return mView;
    }

    private void resetBookData(){
        mBookItems = new ArrayList<BookItem>();
        mBookCursor = mXBookManager.getAllBooks();
        mBookCursor.registerContentObserver(mBookObserver);
        mBookCursor.moveToPosition(-1);
        while(mBookCursor.moveToNext()) {
            BookItem item = new BookItem();
            item.setBookId(mBookCursor.getLong(XBookManager.BOOK_ID_INDEX));
            item.setBookName(mBookCursor.getString(XBookManager.NAME_INDEX));
            item.setAuthor(mBookCursor.getString(XBookManager.AUTHOR_NAME_INDEX));
            item.setDescribe(mBookCursor.getString(XBookManager.DESCRIPTION_INDEX));
            item.setCover(mBookCursor.getString(XBookManager.COVER_INDEX));
            item.setUpdateDate(mBookCursor.getLong(XBookManager.HAS_UPDATE_INDEX));
            mBookItems.add(item);
        }
        mBookItems.add(new BookItem());
        if (Settings.getInstance().getShelfShowMode() == Settings.SHELF_SHOW_MODE_GRID) {
            showGridMode();
        } else {
            showListMode();
        }
    }

    private void initGallery() {
        mHistoryManager = new HistoryManager();
        Cursor mHistoryCursor = mHistoryManager.getAllHistory();
        HistoryGalleryAdapter mGalleryAdapter = new HistoryGalleryAdapter(mContext, mHistoryCursor, true);
        Gallery mGallery = (Gallery) mHeaderView.findViewById(R.id.gallery);
        mGallery.setSpacing(50);
        mGallery.setFadingEdgeLength(0);
        mGallery.setGravity(Gravity.CENTER_VERTICAL);
        mGallery.setAdapter(mGalleryAdapter);
        mGallery.setSelection(0);
        mGallery.setEmptyView(mHeaderView.findViewById(R.id.empty_view));
        //mGallery.setOnItemClickListener(this);
        mGallery.setCallbackDuringFling(false);
        HistoryOperationListener historyOperationListener = new HistoryOperationListener(mContext);
        mGallery.setOnItemClickListener(historyOperationListener);
        mGallery.setOnItemLongClickListener(historyOperationListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.grid_view:
                Settings.getInstance().setShelfShowMode(Settings.SHELF_SHOW_MODE_GRID);
                resetBookData();
                break;
            case R.id.list_view:
                Settings.getInstance().setShelfShowMode(Settings.SHELF_SHOW_MODE_LIST);
                resetBookData();
                break;
        }
    }

    private void showGridMode() {
        if (mGridAdapter == null) {
            mGridAdapter = new GridBookAdapter(mContext, mBookItems);
        } else {
            mGridAdapter.clear();
            mGridAdapter.addAll(mBookItems);
            mGridAdapter.notifyDataSetChanged();
        }
        mListView.setDivider(new BitmapDrawable());
        mListView.setDividerHeight(14);
        mListView.setAdapter(mGridAdapter);
        mSelectListView.setEnabled(true);
        mSelectGridView.setEnabled(false);
    }

    private void showListMode() {
        if (mListAdapter == null) {
            mListAdapter = new ListBookAdapter(mContext, mBookItems);
        } else {
            mListAdapter.clear();
            mListAdapter.addAll(mBookItems);
            mListAdapter.notifyDataSetChanged();
        }
        mListView.setDivider(getResources().getDrawable(R.drawable.diver_line));
        mListView.setAdapter(mListAdapter);
        mSelectListView.setEnabled(false);
        mSelectGridView.setEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBookCursor != null && mBookObserver != null ) {
            mBookCursor.unregisterContentObserver(mBookObserver);
        }

    }
}