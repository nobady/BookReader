package com.sanqiwan.reader.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.alipay.sdk.app.PayTask;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.alipay.PayResult;
import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.PaymentRecord;
import com.sanqiwan.reader.model.UserInfo;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import com.sanqiwan.reader.util.SimpleDateFormat;
import com.sanqiwan.reader.view.LoadingProgressDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/12/13
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaymentRecordFragment extends BaseFragment implements View.OnClickListener {
    private TextView mUserNameTextView, mRecordTextView;
    private ImageView mIconImageView;
    private ListView mListView;

    private UserManager mUserManager;
    private UserInfo mUserInfo;
    private List<PaymentRecord> mRecords;
    private Context mContext;
    private View mFragmentView;
    private LayoutInflater mInflater;


    public static PaymentRecordFragment newFragment() {
        return new PaymentRecordFragment();
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
            mRecordTextView.setText(R.string.you_pay_record);
            ImageView returnImageView = (ImageView) mFragmentView.findViewById(R.id.btn_return);
            returnImageView.setOnClickListener(this);
            TextView topTitle = (TextView) mFragmentView.findViewById(R.id.top_title);
            topTitle.setText(R.string.pay_history);

            loadData();
        }
        if (mFragmentView.getParent() != null) {
            ((ViewGroup) mFragmentView.getParent()).removeAllViews();
        }
        return mFragmentView;
    }

    private void loadData() {
        mUserManager = UserManager.getInstance();
        if (!mUserManager.isLogin()) {
            return;
        }
        mUserInfo = mUserManager.getUserInfo();
        mUserNameTextView.setText(mUserInfo.getUserName());
        AsyncTask<Void, Void, List<PaymentRecord>> task = new AsyncTask<Void, Void, List<PaymentRecord>>() {
            private LoadingProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                mProgressDialog = new LoadingProgressDialog(mContext);
                mProgressDialog.show();
            }

            @Override
            protected void onPostExecute(List<PaymentRecord> result) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                TextView emptyView = (TextView) mFragmentView.findViewById(R.id.empty_view);
                if (result == null) {
                    emptyView.setText(R.string.no_wifi);
                    emptyView.setVisibility(View.VISIBLE);
                    return;
                }
                if (result.size() == 0) {
                    emptyView.setText(R.string.no_payment_record);
                    emptyView.setVisibility(View.VISIBLE);
                    return;
                }
                mRecords = result;
                PaymentRecordAdapter paymentRecordAdapter = new PaymentRecordAdapter();
                mListView.setAdapter(paymentRecordAdapter);
            }

            @Override
            protected List<PaymentRecord> doInBackground(Void... params) {
                List<PaymentRecord> result = mUserManager.getPaymentRecords();
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

    private class PaymentRecordAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mRecords.size();
        }

        @Override
        public Object getItem(int position) {
            return mRecords.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.payment_record_item, null);
                viewHolder = new ViewHolder();
                viewHolder.channelTextView = (TextView) convertView.findViewById(R.id.tv_channel);
                viewHolder.moneyTextView = (TextView) convertView.findViewById(R.id.tv_money);
                viewHolder.timeTextView = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String channel = getString(R.string.pay_channel);
            channel = String.format(channel, mRecords.get(position).getChannel());
            viewHolder.channelTextView.setText(channel);
            String money = getString(R.string.pay_money);
            money = String.format(money, mRecords.get(position).getAmount());
            viewHolder.moneyTextView.setText(money);
            viewHolder.timeTextView.setText(SimpleDateFormat.formatUnixTimestamp("MM-d hh:mm:ss", mRecords.get(position).getTime()));
            return convertView;
        }

    }



    static class ViewHolder {
        TextView channelTextView;
        TextView moneyTextView;
        TextView timeTextView;
    }
}