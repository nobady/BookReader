package com.sanqiwan.reader.ui;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.data.HistoryManager;
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
public class HistoryOperationListener implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private Context mContext;
    private HistoryManager mHistoryManager;
    private DeleteConfirmDialog mDialog;

    public HistoryOperationListener(Context context) {
        mContext = context;
        mHistoryManager = new HistoryManager();
    }

    private void showDeleteConfirmDialog(final long bookId) {
        mDialog = new DeleteConfirmDialog(mContext);
        mDialog.setCancelable(true);
        mDialog.setTitle(mContext.getString(R.string.delete_title));
        String msg = mContext.getString(R.string.delete_history_msg);
        String bookName = mHistoryManager.getHistoryItem(bookId).getBookName();
        mDialog.setMessage(String.format(msg, bookName));
        mDialog.setOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, UMengEventId.UMENG_DELETE_EVENT);
                mHistoryManager.deleteHistoryItem(bookId);
                mDialog.dismiss();
            }
        });
        mDialog.show();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long bookId = (Long)view.getTag(R.id.book_id);
        ReaderActivity.openBook(mContext, bookId);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        long bookId = (Long)view.getTag(R.id.book_id);
        showDeleteConfirmDialog(bookId);
        return true;
    }
}
