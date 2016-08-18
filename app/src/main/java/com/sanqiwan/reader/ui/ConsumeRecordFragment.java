package com.sanqiwan.reader.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.BucketListAdapter;
import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.ConsumeRecord;
import com.sanqiwan.reader.model.UserInfo;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import com.sanqiwan.reader.util.CoverUtil;
import com.sanqiwan.reader.util.SimpleDateFormat;
import com.sanqiwan.reader.webimageview.WebImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/12/13
 * Time: 12:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConsumeRecordFragment extends BaseFragment implements View.OnClickListener,BucketListAdapter.LoadMoreListener {
    private TextView mUserNameTextView, mRecordTextView;
    private ImageView mIconImageView;
    private ListView mListView;

    private UserManager mUserManager;
    private UserInfo mUserInfo;
    private Context mContext;
    private View mFragmentView;
    private LayoutInflater mInflater;
    private int mCurrentPage = 1;
    private ConsumeRecordAdapter mRecordAdapter;

    public static ConsumeRecordFragment newFragment() {
        return new ConsumeRecordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        mInflater = inflater;
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.record, container, false);

            mUserNameTextView = (TextView) mFragmentView.findViewById(R.id.user_name);
            mRecordTextView = (TextView) mFragmentView.findViewById(R.id.record_title);
            mIconImageView = (ImageView) mFragmentView.findViewById(R.id.avatar);
            mListView = (ListView) mFragmentView.findViewById(R.id.lv_record);
            mRecordTextView.setText(R.string.you_consume_record);
            ImageView returnImageView = (ImageView) mFragmentView.findViewById(R.id.btn_return);
            returnImageView.setOnClickListener(this);
            TextView topTitle = (TextView) mFragmentView.findViewById(R.id.top_title);
            topTitle.setText(R.string.consume_history);
            initData();
        }
        if (mFragmentView.getParent() != null) {
            ((ViewGroup) mFragmentView.getParent()).removeAllViews();
        }
        return mFragmentView;
    }

    private void initData() {
        mUserManager = UserManager.getInstance();
        if (!mUserManager.isLogin()) {
            return;
        }
        mUserInfo = mUserManager.getUserInfo();
        mUserNameTextView.setText(mUserInfo.getUserName());
        mRecordAdapter = new ConsumeRecordAdapter(getContext());
        mRecordAdapter.enableLoadMore();
        mRecordAdapter.setLoadMoreListener(this);
        mListView.setAdapter(mRecordAdapter);


    }

    private void loadData() {

        AsyncTask<Void, Void, List<ConsumeRecord>> task = new AsyncTask<Void, Void, List<ConsumeRecord>>() {

            @Override
            protected void onPostExecute(List<ConsumeRecord> result) {
                TextView emptyView = (TextView) mFragmentView.findViewById(R.id.empty_view);
                mRecordAdapter.disableLoadMore();
                if (mRecordAdapter.isEmpty() && result == null) {
                    emptyView.setText(R.string.no_wifi);
                    emptyView.setVisibility(View.VISIBLE);
                    return;
                }
                if (mRecordAdapter.isEmpty() && result.size() == 0) {
                    emptyView.setText(R.string.no_consume_record);
                    emptyView.setVisibility(View.VISIBLE);
                    return;
                }

                if (result == null || result.size() == 0) {
                    return;
                }
                mRecordAdapter.addAll(result);
                mCurrentPage++;
                mRecordAdapter.enableLoadMore();
            }

            @Override
            protected List<ConsumeRecord> doInBackground(Void... params) {
                List<ConsumeRecord> result = mUserManager.getConsumeRecords(mCurrentPage);
                return result;
            }
        };
        AsyncTaskUtil.execute(task);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    @Override
    public void onLoadMore() {
        loadData();
    }

    private class ConsumeRecordAdapter extends BucketListAdapter<ConsumeRecord> {

        public ConsumeRecordAdapter(Context context) {
            super(context, new ArrayList<ConsumeRecord>());
        }

        @Override
        protected View newView(int position, ConsumeRecord element) {
            View convertView = mInflater.inflate(R.layout.consume_record_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.bookNameTextView = (TextView) convertView.findViewById(R.id.bookName);
            viewHolder.moneyTextView = (TextView) convertView.findViewById(R.id.money);
            viewHolder.timeTextView = (TextView) convertView.findViewById(R.id.time);
            viewHolder.chapterTextView = (TextView) convertView.findViewById(R.id.chapter);
            viewHolder.coverImageView = (WebImageView) convertView.findViewById(R.id.cover);
            convertView.setTag(viewHolder);
            return convertView;
        }

        @Override
        protected void bindView(View view, int position, ConsumeRecord element) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            viewHolder.bookNameTextView.setText(element.getBookName());
            String money = getString(R.string.consume_money);
            money = String.format(money, element.getAmount());
            SpannableString spannableString = new SpannableString(money);
            //使用spannable设置数值位置的文字显示为指定颜色
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.record_money_color)), 2, money.length() - 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.moneyTextView.setText(spannableString);
            viewHolder.timeTextView.setText(SimpleDateFormat.formatUnixTimestamp("MM-d hh:mm:ss", element.getTime()));
            if (!CoverUtil.hasNoCover(element.getCover())) {
                viewHolder.coverImageView.setImageUrl(element.getCover());
            }
            viewHolder.chapterTextView.setText(element.getChapter());
        }

    }

    static class ViewHolder {
        TextView bookNameTextView;
        TextView chapterTextView;
        TextView moneyTextView;
        TextView timeTextView;
        WebImageView coverImageView;
    }
}