package com.sanqiwan.reader.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.PaymentResponseInfo;
import com.sanqiwan.reader.view.CmccConfirmDialog;
import com.sanqiwan.reader.webservice.PaymentWebService;

import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.ArrayList;
import java.util.List;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/18 13:07.
 */

public class WeChatPayment implements IPayment {

    private static final int RATIO = 97;
    private static final int TEN = 10;
    private static final int THIRTY = 30;
    private static final int FIFTY = 50;
    private static final int HUNDRED = 100;
    private static final int THREE_HUNDRED = 365;
    private int mSelectValue;

    private Context mContext;
    private String mUserName;
    private PaymentWebService mPaymentWebService;

    public WeChatPayment () {
        mContext = AppContext.getInstance();
        mUserName = UserManager.getInstance().getUserInfo().getUserName();
        mPaymentWebService = new PaymentWebService ();
    }

    @Override
    public String getName () {
        return mContext.getString (R.string.wechat_txt);
    }

    @Override
    public View getView (Context context, CmccConfirmDialog.CallBack callBack) {
        View view = newView ();
        bindView (view);
        return view;
    }

    @Override
    public void bindView (View view) {
        List<String> showText = getText ();

        WechatHolder holder = (WechatHolder) view.getTag ();
        holder.mUserName.setText (mUserName);

        holder.mTenView.setTag (holder);
        holder.mTenView.setText (showText.get (1));
        holder.mTenView.setOnClickListener (mTenListener);

        holder.mThreeView.setTag (holder);
        holder.mThreeView.setText (showText.get (2));
        holder.mThreeView.setOnClickListener (mThreeListener);

        holder.mFiftyView.setTag (holder);
        holder.mFiftyView.setText (showText.get (3));
        holder.mFiftyView.setOnClickListener (mFiftyListener);

        holder.mHunderView.setTag (holder);
        holder.mHunderView.setText (showText.get (4));
        holder.mHunderView.setOnClickListener (mHunderListener);

        holder.mThreeHunderView.setTag (holder);
        holder.mThreeHunderView.setText (showText.get (5));
        holder.mThreeHunderView.setOnClickListener (mThreeHunderListener);

        //默认选中10元
        clearButtonSelected (holder);
        holder.mTenView.setSelected (true);
        mSelectValue = TEN;
        holder.mRatioView.setText (getRatioTipText (mSelectValue));

    }

    @Override
    public View newView () {

        WechatHolder holder = new WechatHolder ();
        View view = LayoutInflater.from (mContext).inflate (R.layout.wechat_pay, null);
        holder.mUserName = (TextView) view.findViewById (R.id.user_wechat);
        holder.mTenView = (TextView) view.findViewById (R.id.ten_wechat);
        holder.mThreeView = (TextView) view.findViewById (R.id.thirty_wechat);
        holder.mFiftyView = (TextView) view.findViewById (R.id.fifty_wechat);
        holder.mHunderView = (TextView) view.findViewById (R.id.hundred_wechat);
        holder.mThreeHunderView = (TextView) view.findViewById (R.id.three_hundred_wechat);
        holder.mRatioView = (TextView) view.findViewById (R.id.conversion_ratio_wechat);
        view.setTag (holder);
        return view;
    }

    @Override
    public PaymentResponseInfo pay () throws ZLNetworkException {
        return null;
    }

    @Override
    public int convertMoney (int value) {
        return value * RATIO;
    }

    @Override
    public String getRatioTipText (int value) {
        String text = mContext.getResources ().getString (R.string.ratio_txt);
        return String.format (text, value, convertMoney (value));
    }

    @Override
    public List<String> getText () {
        List<String> showText = new ArrayList<> ();
        showText.add (mContext.getResources ().getString (R.string.wechat_txt));
        showText.add (mContext.getResources ().getString (R.string.ten_txt));
        showText.add (mContext.getResources ().getString (R.string.thirty_txt));
        showText.add (mContext.getResources ().getString (R.string.fifty_txt));
        showText.add (mContext.getResources ().getString (R.string.hundred_txt));
        showText.add (mContext.getResources ().getString (R.string.three_hundred_txt));
        return showText;
    }

    private class WechatHolder {
        private TextView mUserName;
        private TextView mTenView;
        private TextView mThreeView;
        private TextView mFiftyView;
        private TextView mHunderView;
        private TextView mThreeHunderView;
        private TextView mRatioView;
    }

    private void clearButtonSelected (WechatHolder holder) {
        holder.mTenView.setSelected (false);
        holder.mThreeView.setSelected (false);
        holder.mFiftyView.setSelected (false);
        holder.mHunderView.setSelected (false);
        holder.mThreeHunderView.setSelected (false);
    }

    private View.OnClickListener mTenListener = new View.OnClickListener () {
        @Override
        public void onClick (View v) {
            WechatHolder holder = (WechatHolder) v.getTag ();
            clearButtonSelected (holder);
            holder.mTenView.setSelected (true);
            mSelectValue = TEN;
            holder.mRatioView.setText (getRatioTipText (mSelectValue));
        }
    };
    private View.OnClickListener mThreeListener = new View.OnClickListener () {
        @Override
        public void onClick (View v) {
            WechatHolder holder = (WechatHolder) v.getTag ();
            clearButtonSelected (holder);
            holder.mThreeView.setSelected (true);
            mSelectValue = THIRTY;
            holder.mRatioView.setText (getRatioTipText (mSelectValue));
        }
    };
    private View.OnClickListener mFiftyListener = new View.OnClickListener () {
        @Override
        public void onClick (View v) {
            WechatHolder holder = (WechatHolder) v.getTag ();
            clearButtonSelected (holder);
            holder.mFiftyView.setSelected (true);
            mSelectValue = FIFTY;
            holder.mRatioView.setText (getRatioTipText (mSelectValue));
        }
    };
    private View.OnClickListener mHunderListener = new View.OnClickListener () {
        @Override
        public void onClick (View v) {
            WechatHolder holder = (WechatHolder) v.getTag ();
            clearButtonSelected (holder);
            holder.mHunderView.setSelected (true);
            mSelectValue = HUNDRED;
            holder.mRatioView.setText (getRatioTipText (mSelectValue));
        }
    };
    private View.OnClickListener mThreeHunderListener = new View.OnClickListener () {
        @Override
        public void onClick (View v) {
            WechatHolder holder = (WechatHolder) v.getTag ();
            clearButtonSelected (holder);
            holder.mThreeHunderView.setSelected (true);
            mSelectValue = THREE_HUNDRED;
            holder.mRatioView.setText (getRatioTipText (mSelectValue));
        }
    };
}
