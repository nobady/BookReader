package com.sanqiwan.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.model.BookItem;
import com.sanqiwan.reader.ui.Constants;
import com.sanqiwan.reader.util.CoverUtil;
import com.sanqiwan.reader.util.StarSumUtil;
import com.sanqiwan.reader.view.MyRatingImageStar;
import com.sanqiwan.reader.webimageview.WebImageView;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-7-22
 * Time: 下午3:02
 * To change this template use File | Settings | File Templates.
 */
public class RankBooksAdapter extends BucketListAdapter<BookItem> {
    private LayoutInflater mInflater;
    private Context mContext;

    public RankBooksAdapter(Context context) {
        super(context, new ArrayList<BookItem>(), Constants.GRID_COLUMN_SIZE);
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    protected View newView(int position, BookItem element) {
        ViewHolder viewHolder = new ViewHolder();
        View view = mInflater.inflate(R.layout.rank_book_item, null);
        viewHolder.mBookTitleView = (TextView) view.findViewById(R.id.tv_name);
        viewHolder.mDescribeTextView = (TextView) view.findViewById(R.id.tv_describe);
        viewHolder.mAuthorTextView = (TextView) view.findViewById(R.id.tv_author);
        viewHolder.mBookCoverView = (WebImageView) view.findViewById(R.id.cover);
        viewHolder.mTopNumberImageView = (ImageView) view.findViewById(R.id.top_number);
        viewHolder.mRatingImageStar = (MyRatingImageStar) view.findViewById(R.id.degree_detail);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    protected void bindView(View view, int position, BookItem element) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String coverUrl = element.getCover();
        viewHolder.mBookCoverView.setDefaultImageResource(R.drawable.no_book_pic);
        if (!CoverUtil.hasNoCover(coverUrl)) {
            viewHolder.mBookCoverView.setImageUrl(coverUrl);
        }
        viewHolder.mDescribeTextView.setText(mContext.getString(R.string.bookitem_describe) + element.getDescribe());
        viewHolder.mAuthorTextView.setText(mContext.getString(R.string.author) + element.getAuthor());
        viewHolder.mBookTitleView.setText(element.getBookName());
        viewHolder.mRatingImageStar.setRating(StarSumUtil.getStarSum(element.getCommendNumber()));
        if (position == 0) {
            viewHolder.mTopNumberImageView.setImageResource(R.drawable.top_one);
            viewHolder.mTopNumberImageView.setVisibility(View.VISIBLE);
        }

        if (position == 1) {
            viewHolder.mTopNumberImageView.setImageResource(R.drawable.top_two);
            viewHolder.mTopNumberImageView.setVisibility(View.VISIBLE);
        }

        if (position == 2) {
            viewHolder.mTopNumberImageView.setImageResource(R.drawable.top_three);
            viewHolder.mTopNumberImageView.setVisibility(View.VISIBLE);
        }

        if (position > 2) {
            viewHolder.mTopNumberImageView.setVisibility(View.GONE);
        }
    }

    static class ViewHolder {
        TextView mBookTitleView;
        TextView mDescribeTextView;
        TextView mAuthorTextView;
        WebImageView mBookCoverView;
        ImageView mTopNumberImageView;
        MyRatingImageStar mRatingImageStar;
    }
}
