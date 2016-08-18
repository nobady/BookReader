package com.sanqiwan.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.sanqiwan.reader.R;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-8-14
 * Time: 下午8:19
 * To change this template use File | Settings | File Templates.
 */
public class MyRatingImageStar extends LinearLayout {
    private static final int STAR_COUNT = 5;

    public MyRatingImageStar(Context context) {
        this(context, null);
    }

    public MyRatingImageStar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRating(int starCount) {
        removeAllViews();
        int i;
        for (i = 0; i < starCount; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.bg_detail_star);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(0, 0, 5, 0);

            addView(imageView);
        }
        for (; i < STAR_COUNT; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.bg_detail_star_nor);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(0, 0, 5, 0);

            addView(imageView);
        }

    }
}
