package com.sanqiwan.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.model.BookItem;
import com.sanqiwan.reader.util.CoverUtil;
import com.sanqiwan.reader.util.StarSumUtil;
import com.sanqiwan.reader.webimageview.WebImageView;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-11-21
 * Time: 上午11:23
 * To change this template use File | Settings | File Templates.
 */
public class TodayRecommendView extends LinearLayout implements View.OnClickListener{

    private Context mContext;
    List<BookItem> mRecommendList;

    public TodayRecommendView(Context context) {
        super(context);
        mContext = context;
        setOrientation(VERTICAL);
    }

    public TodayRecommendView(Context context, AttributeSet attrs) {
        super(context,attrs);
        mContext = context;
        setOrientation(VERTICAL);
    }

    public void setRecommends(List<BookItem> recommendList) {
        mRecommendList = recommendList;
        removeAllViewsInLayout();   //删除已有的子控件
        addRows();
    }

    private void addRows() {
        int rows = mRecommendList.size();
        if (rows > 2) {
            rows = 2;
        }
        for (int i = 0; i < rows; i++) {
            RelativeLayout relativeLayout = newRelativeLayout(mRecommendList.get(i));
            addView(relativeLayout);
        }
    }

    private RelativeLayout newRelativeLayout(BookItem bookItem) {
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.today_recommend_view_layout, null);
        WebImageView webImageViewView = (WebImageView) relativeLayout.findViewById(R.id.cover);
        webImageViewView.setDefaultImageResource(R.drawable.no_book_pic);
        if (!CoverUtil.hasNoCover(bookItem.getCover())) {
            webImageViewView.setImageUrl(bookItem.getCover());
        }
        TextView titleTextView = (TextView) relativeLayout.findViewById(R.id.tv_name);
        titleTextView.setText(bookItem.getBookName());
        TextView authorTextView = (TextView) relativeLayout.findViewById(R.id.tv_author);
        authorTextView.setText(mContext.getString(R.string.author) + bookItem.getAuthor());
        TextView describeTextView = (TextView) relativeLayout.findViewById(R.id.tv_describe);
        describeTextView.setText(mContext.getString(R.string.bookitem_describe) + bookItem.getDescribe());
        MyRatingImageStar ratingImageStar = (MyRatingImageStar) relativeLayout.findViewById(R.id.degree_detail);
        ratingImageStar.setRating(StarSumUtil.getStarSum(bookItem.getCommendNumber()));
        relativeLayout.setTag(bookItem);
        relativeLayout.setOnClickListener(this);
        return relativeLayout;
    }

    @Override
    public void onClick(View v) {
        mOpenBookDetailListener.onClick(v);

    }

    private View.OnClickListener mOpenBookDetailListener;
    public void setOpenBookDetailListener(View.OnClickListener listener) {
        mOpenBookDetailListener = listener;
    }
}
