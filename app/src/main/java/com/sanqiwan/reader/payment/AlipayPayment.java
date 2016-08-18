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
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-4
 * Time: 下午9:17
 * To change this template use File | Settings | File Templates.
 */
class AlipayPayment implements IPayment {

    private static final int RATIO = 97;
    private static final int TEN = 10;
    private static final int THIRTY = 30;
    private static final int FIFTY = 50;
    private static final int HUNDRED = 100;
    private int mSelectValue;
    private LayoutInflater mInflater;
    private Context mContext;
    private String mUserName;
    private PaymentWebService mPaymentWebService;

    AlipayPayment() {
        mContext = AppContext.getInstance();
        mInflater = LayoutInflater.from(mContext);
        mUserName = UserManager.getInstance().getUserInfo().getUserName();
        mPaymentWebService = new PaymentWebService();
    }

    @Override
    public String getName() {
        return mContext.getResources().getString(R.string.alipay_txt);
    }


    @Override
    public List<String> getText() {
        List<String> showText = new ArrayList<String>();
        showText.add(mContext.getResources().getString(R.string.alipay_title_txt));
        showText.add(mContext.getResources().getString(R.string.ten_txt));
        showText.add(mContext.getResources().getString(R.string.thirty_txt));
        showText.add(mContext.getResources().getString(R.string.fifty_txt));
        showText.add(mContext.getResources().getString(R.string.hundred_txt));
        return showText;
    }

    @Override
    public View getView(Context context, CmccConfirmDialog.CallBack callBack) {
        View view = newView();
        bindView(view);
        return view;
    }

    //数据绑定
    @Override
    public void bindView(View view) {
        List<String> showText = getText();

        AliPayHolder aliPayHolder = (AliPayHolder) view.getTag();
        aliPayHolder.mUserName.setText(mUserName);
        aliPayHolder.mFirstView.setTag(aliPayHolder);
        aliPayHolder.mFirstView.setText(showText.get(1));
        aliPayHolder.mFirstView.setOnClickListener(mFirstListener);
        aliPayHolder.mSecondView.setTag(aliPayHolder);
        aliPayHolder.mSecondView.setText(showText.get(2));
        aliPayHolder.mSecondView.setOnClickListener(mSecondListener);
        aliPayHolder.mThirdView.setTag(aliPayHolder);
        aliPayHolder.mThirdView.setText(showText.get(3));
        aliPayHolder.mThirdView.setOnClickListener(mThirdListener);
        aliPayHolder.mFourthView.setTag(aliPayHolder);
        aliPayHolder.mFourthView.setText(showText.get(4));
        aliPayHolder.mFourthView.setOnClickListener(mFourthListener);
        aliPayHolder.mRatioView.setText(getRatioTipText(TEN));
        clearButtonSelected(aliPayHolder);
        aliPayHolder.mFirstView.setSelected(true);
        mSelectValue = TEN;
    }

    //选择金额的view
    @Override
    public View newView() {
        AliPayHolder aliPayHolder = new AliPayHolder();
        View view = mInflater.inflate(R.layout.alipay_pay, null);
        aliPayHolder.mUserName = (TextView) view.findViewById(R.id.user);
        aliPayHolder.mFirstView = (TextView) view.findViewById(R.id.ten);
        aliPayHolder.mSecondView = (TextView) view.findViewById(R.id.thirty);
        aliPayHolder.mThirdView = (TextView) view.findViewById(R.id.fifty);
        aliPayHolder.mFourthView = (TextView) view.findViewById(R.id.hundred);
        aliPayHolder.mRatioView = (TextView) view.findViewById(R.id.conversion_ratio);

        view.setTag(aliPayHolder);
        return view;
    }

    @Override
    public PaymentResponseInfo pay() throws ZLNetworkException {
        return mPaymentWebService.alipayPayment(UserManager.getInstance().getUserId(), mSelectValue);
    }

    @Override
    public int convertMoney(int value) {
        return value * RATIO;
    }

    @Override
    public String getRatioTipText(int value) {
        String text = mContext.getResources().getString(R.string.ratio_txt);
        return String.format(text, value, convertMoney(value));
    }

    private class AliPayHolder {
        private TextView mUserName;
        private TextView mFirstView;
        private TextView mSecondView;
        private TextView mThirdView;
        private TextView mFourthView;
        private TextView mRatioView;
    }

    private View.OnClickListener mFirstListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AliPayHolder holder = (AliPayHolder) v.getTag();
            clearButtonSelected(holder);
            holder.mFirstView.setSelected(true);
            holder.mRatioView.setText(getRatioTipText(TEN));
            mSelectValue = TEN;
        }
    };

    private View.OnClickListener mSecondListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AliPayHolder holder = (AliPayHolder) v.getTag();
            clearButtonSelected(holder);
            holder.mSecondView.setSelected(true);
            holder.mRatioView.setText(getRatioTipText(THIRTY));
            mSelectValue = THIRTY;
        }
    };

    private View.OnClickListener mThirdListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AliPayHolder holder = (AliPayHolder) v.getTag();
            clearButtonSelected(holder);
            holder.mThirdView.setSelected(true);
            holder.mRatioView.setText(getRatioTipText(FIFTY));
            mSelectValue = FIFTY;
        }
    };

    private View.OnClickListener mFourthListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AliPayHolder holder = (AliPayHolder) v.getTag();
            clearButtonSelected(holder);
            holder.mFourthView.setSelected(true);
            holder.mRatioView.setText(getRatioTipText(HUNDRED));
            mSelectValue = HUNDRED;
        }
    };

    private void clearButtonSelected(AliPayHolder aliPayHolder) {
        aliPayHolder.mFirstView.setSelected(false);
        aliPayHolder.mSecondView.setSelected(false);
        aliPayHolder.mThirdView.setSelected(false);
        aliPayHolder.mFourthView.setSelected(false);
    }
}
