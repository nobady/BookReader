package com.sanqiwan.reader.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.data.HistoryManager;
import com.sanqiwan.reader.util.BitmapUtil;
import com.sanqiwan.reader.util.CoverUtil;
import com.sanqiwan.reader.webimageview.IImageHandler;
import com.sanqiwan.reader.webimageview.WebImageView;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/25/13
 * Time: 2:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryGalleryAdapter  extends CursorAdapter {

    private Context mContext;
    public HistoryGalleryAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View convertView;
        convertView = LayoutInflater.from(context).inflate(R.layout.book_shelf_gallery_layout, null);
        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        long bookId = cursor.getLong(HistoryManager.BOOK_ID_INDEX);
        String bookName = cursor.getString(HistoryManager.NAME_INDEX);
        String cover = cursor.getString(HistoryManager.COVER_INDEX);
        WebImageView webImageView = (WebImageView) view.findViewById(R.id.home_gallery_imageview);
        webImageView.setImageHandler(mCoverImageHandler);
        Bitmap bitmap = BitmapUtil.drawBookCover(context, bookName);
        webImageView.setDefaultImageBitmap(bitmap);
        if (!CoverUtil.hasNoCover(cover)) {
            webImageView.setImageUrl(cover);
        }
        view.setTag(R.id.book_id, bookId);
    }

    private IImageHandler mCoverImageHandler = new IImageHandler() {
        @Override
        public void onHandleImage(WebImageView imageView, Bitmap bitmap) {

            if (bitmap.getWidth()> mContext.getResources().getInteger(R.integer.cover_width) ) {
                bitmap = Bitmap.createScaledBitmap(bitmap,
                        mContext.getResources().getInteger(R.integer.cover_width),
                        mContext.getResources().getInteger(R.integer.cover_height),
                        false);
            }
            imageView.setImageBitmap(bitmap);
        }
    };
}
