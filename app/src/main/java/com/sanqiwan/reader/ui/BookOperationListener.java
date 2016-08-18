package com.sanqiwan.reader.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.data.XBookManager;
import com.sanqiwan.reader.download.XBookUpdate;
import com.sanqiwan.reader.model.BookItem;
import com.sanqiwan.reader.track.UMengEventId;
import com.sanqiwan.reader.view.DeleteConfirmDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/25/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class BookOperationListener implements View.OnClickListener,View.OnLongClickListener {
    private Context mContext;
    private XBookManager mXBookManager;
    private DeleteConfirmDialog mDialog;
    private XBookUpdate.Callback mBookUpdateCallback;

    public BookOperationListener(Context context) {
        mContext = context;
        mXBookManager = new XBookManager();
    }

    @Override
    public void onClick(View v) {
        BookItem item = (BookItem)v.getTag(R.id.book_id);
        if (item.getUpdateDate() == 1) {
            showUpdateConfirmDialog(item.getBookId(), item.getBookName());
        } else {
            ReaderActivity.openBook(mContext, item.getBookId());
        }
    }



    @Override
    public boolean onLongClick(View v) {
        BookItem item = (BookItem)v.getTag(R.id.book_id);
        showDeleteConfirmDialog(item.getBookId(), item.getBookName());
        return true;
    }

    private void showUpdateConfirmDialog(final long bookId, String bookName) {
        mDialog = new DeleteConfirmDialog(mContext);
        mDialog.setCancelable(true);
        mDialog.setTitle(mContext.getString(R.string.update_title));
        String msg = mContext.getString(R.string.update_msg);
        mDialog.setMessage(String.format(msg, bookName));
        mDialog.setOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XBookUpdate bookUpdate = new XBookUpdate();
                bookUpdate.update(bookId, mBookUpdateCallback);
                mXBookManager.setBookHasUpdate(bookId, false);
                mDialog.dismiss();
            }
        });
        mDialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                ReaderActivity.openBook(mContext, bookId);
            }
        });
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ReaderActivity.openBook(mContext, bookId);
            }
        });
        mDialog.show();
    }

    private void showDeleteConfirmDialog(final long bookId, String bookName) {

        mDialog = new DeleteConfirmDialog(mContext);
        mDialog.setCancelable(true);
        mDialog.setTitle(mContext.getString(R.string.delete_title));
        String msg = mContext.getString(R.string.delete_book_msg);
        mDialog.setMessage(String.format(msg, bookName));
        mDialog.setOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, UMengEventId.UMENG_DELETE_EVENT);
                mXBookManager.deleteBookWithFile(bookId);
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public void setBookUpdateCallback(XBookUpdate.Callback bookUpdateCallback) {
        mBookUpdateCallback = bookUpdateCallback;
    }
}
