package com.sanqiwan.reader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.download.XBookUpdate;
import com.sanqiwan.reader.format.xbook.XBookReader;
import com.sanqiwan.reader.model.BookItem;
import com.sanqiwan.reader.model.BookResource;
import com.sanqiwan.reader.preference.Settings;
import com.sanqiwan.reader.ui.BookOperationListener;
import com.sanqiwan.reader.ui.MainActivity;
import com.sanqiwan.reader.util.BitmapUtil;
import com.sanqiwan.reader.util.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/25/13
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListBookAdapter extends BucketListAdapter<BookItem> {
    private BookOperationListener mBookoperationListener;
    private Context mContext;
    private Map<Long, Integer> mProgresses = new HashMap<Long, Integer>();
    private final static String NO_COVER_NAME = "noBookPic.jpg";

    public ListBookAdapter(Context context, List<BookItem> list) {
        super(context, list, 1);
        mContext = context;
        mBookoperationListener = new BookOperationListener(mContext);
        mBookoperationListener.setBookUpdateCallback(mBookUpdateCallback);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == getElementCount() -1) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_add_more_item, null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.openMainActivity(mContext, MainActivity.RECOMMEND_FRAGMENT_ID);
                }
            });
            return convertView;
        }
        if (convertView instanceof FrameLayout) {
            convertView = null;
        }
        return super.getView(position, convertView, parent);
    }

    @Override
    protected View newView(int position, BookItem element) {
        View convertView;
        Holder holder = new Holder();
        convertView = LayoutInflater.from(mContext).inflate(R.layout.book_shelf_list_item, null);
        holder.mImageView = (ImageView) convertView.findViewById(R.id.home_gallery_imageview);
        holder.mBookName = (TextView) convertView.findViewById(R.id.home_gallery_item_bookname);
        holder.mAuthor = (TextView) convertView.findViewById(R.id.home_author);
        holder.mDescribe = (TextView) convertView.findViewById(R.id.home_describe);
        holder.mUpdateTipView = (ImageView) convertView.findViewById(R.id.book_update_tip);
        holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.update_progress);
        holder.mProgressBackView = convertView.findViewById(R.id.progress_back);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    protected void bindView(View view, int position, BookItem element) {
        Holder holder = (Holder) view.getTag();
        long bookId = element.getBookId();
        String bookName = element.getBookName();
        String cover = element.getCover();
        if (cover == null || cover.endsWith(NO_COVER_NAME)) {
            Bitmap bitmap = BitmapUtil.drawBookCover(mContext, bookName);
            holder.mImageView.setImageBitmap(bitmap);
        } else {
            XBookReader reader = new XBookReader(Settings.getInstance().getBookDownloadFile(bookId));
            BookResource bookResource = reader.readBookResource();
            Bitmap bitmap = bookResource.getCover();
            if (bitmap == null || bitmap.getHeight() <= 0 || bitmap.getHeight() <= 0) {
                bitmap = BitmapUtil.drawBookCover(mContext, bookName);
            }
            holder.mImageView.setImageBitmap(bitmap);
        }
        if (element.getUpdateDate() == 1) {
            holder.mUpdateTipView.setVisibility(View.VISIBLE);
        } else {
            holder.mUpdateTipView.setVisibility(View.GONE);
        }
        if (mProgresses.containsKey(bookId) && mProgresses.get(bookId) < 100) {
            holder.mProgressBar.setVisibility(View.VISIBLE);
            holder.mProgressBar.setMax(100);
            holder.mProgressBar.setProgress(mProgresses.get(bookId));
            holder.mProgressBackView.setVisibility(View.VISIBLE);
        } else {
            holder.mProgressBar.setVisibility(View.GONE);
            holder.mProgressBackView.setVisibility(View.GONE);
        }
        holder.mBookName.setText(bookName);
        String author = mContext.getString(R.string.author_format);
        author = String.format(author, element.getAuthor());
        holder.mAuthor.setText(author);
        String describe = String.format(mContext.getString(R.string.describe_format, element.getDescribe()));
        holder.mDescribe.setText(describe);
        view.setOnClickListener(mBookoperationListener);
        view.setOnLongClickListener(mBookoperationListener);
        view.setTag(R.id.book_id, element);
    }
    public class Holder {
        public ImageView mImageView;
        public TextView mBookName;
        public TextView mAuthor;
        public TextView mDescribe;
        public ImageView mUpdateTipView;
        public ProgressBar mProgressBar;
        public View mProgressBackView;
    }

    private XBookUpdate.Callback mBookUpdateCallback = new XBookUpdate.Callback() {
        @Override
        public void onBookUpdated(final long bookId) {
            UIUtil.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    deleteProgress(bookId);
                }
            });
        }

        @Override
        public void onBookUpdateFailed(long bookId) {
        }

        @Override
        public void onProgressUpdate(final long bookId, final int progress) {
            UIUtil.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    updateProgress(bookId, progress);
                }
            });

        }
    };

    public void updateProgress(long bookId, int progress) {
        mProgresses.put(bookId, progress);
        notifyDataSetChanged();
    }

    public void deleteProgress(long bookId) {
        mProgresses.remove(bookId);
        notifyDataSetChanged();
    }
}
