package com.sanqiwan.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-30
 * Time: 下午3:24
 * To change this template use File | Settings | File Templates.
 */
public class DisplayAllItemsListView extends ListView {

    public DisplayAllItemsListView(Context context) {
        this(context, null);
    }

    public DisplayAllItemsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //显示所有ListView的所有Item，listView的高度设置为AT_MOST
        super.onMeasure(widthMeasureSpec, 0xbfffffff);
    }
}
