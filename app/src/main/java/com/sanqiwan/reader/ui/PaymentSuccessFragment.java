package com.sanqiwan.reader.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.engine.UserManager;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-15
 * Time: 上午11:12
 * To change this template use File | Settings | File Templates.
 */
public class PaymentSuccessFragment extends BaseFragment implements View.OnClickListener {

    private static final String MESSAGE_INFO = "message_info";
    private static final String PAY_MONEY = "pay_money";
    private ViewGroup mTopView;
    private TextView mPaymentSuccessTitle;
    private TextView mUserName;
    private TextView mSuccessTip;
    private TextView mCheckAccount;
    private TextView mHelper;
    private ImageView mBack;
    private String mMsgInfo;
    private int mPaymentMoney;

    private Context mContext;
    private View mFragmentView;
    private LayoutInflater mInflater;

    public static PaymentSuccessFragment newFragment(String msgInfo, int payMoney) {
        PaymentSuccessFragment fragment = new PaymentSuccessFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGE_INFO, msgInfo);
        args.putInt(PAY_MONEY, payMoney);
        fragment.setArguments(args);
        return fragment;
    }

    private void takeArguments() {
        Bundle args = getArguments();
        if (args != null) {
            mMsgInfo = args.getString(MESSAGE_INFO);
            mPaymentMoney = args.getInt(PAY_MONEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        takeArguments();
        mContext = getContext();
        mInflater = inflater;
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.payment_success, container, false);
            initTopView();

            mUserName = (TextView) mFragmentView.findViewById(R.id.user_name);
            mUserName.setText(UserManager.getInstance().getUserInfo().getUserName());
            mSuccessTip = (TextView) mFragmentView.findViewById(R.id.success_tip);
            setSuccessTip();
            mCheckAccount = (TextView) mFragmentView.findViewById(R.id.btn_check_account);
            mCheckAccount.setOnClickListener(this);
            mHelper = (TextView) mFragmentView.findViewById(R.id.helper);
            mHelper.setMovementMethod(LinkMovementMethod.getInstance());
        }
        if (mFragmentView.getParent() != null) {
            ((ViewGroup) mFragmentView.getParent()).removeAllViews();
        }
        return mFragmentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return:
            case R.id.btn_check_account:
                MainActivity.openMainActivity(getContext(), MainActivity.MINE_FRAGMENT_ID);
                break;
        }
    }

    private void setSuccessTip() {
        String money;
        if (mPaymentMoney > 0) {
            money = getString(R.string.payment_money);
            money = String.format(money, mPaymentMoney);
            SpannableStringBuilder style = new SpannableStringBuilder(money);
            int start = money.length() - 4 - String.valueOf(mPaymentMoney).length();
            int end = money.length() - 4;
            style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.status_detail)), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            mSuccessTip.setText(style);
        } else {
            money = mMsgInfo;
            mSuccessTip.setText(money);
        }
    }

    private void initTopView() {
        mTopView = (ViewGroup) mFragmentView.findViewById(R.id.payment_success_top_bar);
        mPaymentSuccessTitle = (TextView) mTopView.findViewById(R.id.top_title);
        mPaymentSuccessTitle.setText(R.string.payment_success_title);
        mBack = (ImageView) mTopView.findViewById(R.id.btn_return);
        mBack.setOnClickListener(this);
    }
}