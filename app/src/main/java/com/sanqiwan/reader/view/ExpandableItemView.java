package com.sanqiwan.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.data.CategoryItem;
import com.sanqiwan.reader.ui.OnlineBookListFragment;
import com.sanqiwan.reader.util.UIUtil;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-11-25
 * Time: 下午5:25
 * To change this template use File | Settings | File Templates.
 */
public class ExpandableItemView extends LinearLayout implements View.OnClickListener{

    private Context mContext;
    private List<CategoryItem> mCategoryItemList;
    private int mCategoryId;
    private static final int COLUMNS = 3;
    private static final int LEFT = UIUtil.dipToPixel(4);
    private static final int BOTTOM = UIUtil.dipToPixel(10);

    public ExpandableItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setCategorys(List<CategoryItem> categoryItemList, int categoryId) {
        mCategoryItemList = categoryItemList;
        mCategoryId = categoryId;
        removeAllViews();
        addRows();
    }

    private void addRows() {
        int rows = mCategoryItemList.size() / COLUMNS;
        for (int i = 0; i < rows; i++) {
            LinearLayout linearLayout = newLinearLayout();
            for (int j = 0; j < COLUMNS; j++) {
                linearLayout.addView(creatTextView(i, j));
            }
            addView(linearLayout);
        }
        int countOfLastRows = mCategoryItemList.size() % COLUMNS;
        if (countOfLastRows != 0) {
            LinearLayout linearLayout = newLinearLayout();
            for (int i = 0; i < countOfLastRows; i++) {
                linearLayout.addView(creatTextView(rows, i));
            }
            for (int i = 0; i < COLUMNS - countOfLastRows; i++) {
                TextView textView = new TextView(mContext);
                textView.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
                linearLayout.addView(textView);
            }
            addView(linearLayout);
        }
    }

    private FrameLayout creatTextView(int row, int position) {
        FrameLayout frameLayout = new FrameLayout(mContext);
        LayoutParams layoutParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
        layoutParams.setMargins(LEFT, 0, LEFT, 0);
        frameLayout.setLayoutParams(layoutParams);
        TextView textView = new TextView(mContext);
        textView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(16);
        textView.setTextColor(mContext.getResources().getColorStateList(R.color.gv_item_color));
        textView.setBackgroundResource(R.drawable.gv_btn_bg);
        textView.setText(mCategoryItemList.get(row * COLUMNS + position).getName());
        textView.setTag(mCategoryItemList.get(row * COLUMNS + position));
        textView.setOnClickListener(this);
        frameLayout.addView(textView);
        return frameLayout;
    }

    private LinearLayout newLinearLayout() {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(HORIZONTAL);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(LEFT, 0, LEFT, BOTTOM);
        linearLayout.setLayoutParams(layoutParams);
        return linearLayout;
    }

    @Override
    public void onClick(View v) {
        mOpenBookListFragmentListener.onClick(v);
    }

    private View.OnClickListener mOpenBookListFragmentListener;
    public void setOpenBookListFragmentListener(View.OnClickListener listFragmentListener) {
        mOpenBookListFragmentListener = listFragmentListener;
    }
}
