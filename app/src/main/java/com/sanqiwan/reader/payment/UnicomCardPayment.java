package com.sanqiwan.reader.payment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.PaymentResponseInfo;
import com.sanqiwan.reader.model.PrepaidCardPaymentInfo;
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
class UnicomCardPayment extends TelecomCardPayment {
    private static final int FIFTY = 50;
    private static final int HUNDRED = 100;
    private LayoutInflater mInflater;
    private Context mContext;
    private String mUserName;
    private int mSelectValue;

    public UnicomCardPayment() {
        mContext = AppContext.getInstance();
        mInflater = LayoutInflater.from(mContext);
        mUserName = UserManager.getInstance().getUserInfo().getUserName();
    }

    @Override
    public void bindView(View view) {
        mSelectValue = FIFTY;
        mCardNum = null;
        mCardPassword = null;
        List<String> showText = getText();

        TelecomCardHolder telecomCardHolder = (TelecomCardHolder) view.getTag();
        telecomCardHolder.mUserName.setText(mUserName);
        telecomCardHolder.mFirstView.setTag(telecomCardHolder);
        telecomCardHolder.mFirstView.setOnClickListener(mFirstListener);
        telecomCardHolder.mFirstView.setText(showText.get(1));
        telecomCardHolder.mSecondView.setTag(telecomCardHolder);
        telecomCardHolder.mSecondView.setText(showText.get(2));
        telecomCardHolder.mSecondView.setOnClickListener(mSecondListener);
        telecomCardHolder.mThirdView.setVisibility(View.GONE);
        telecomCardHolder.mCardNum.addTextChangedListener(mCardNumTextWatcher);
        telecomCardHolder.mCardPassword.addTextChangedListener(mCardPasswordTextWatcher);
        telecomCardHolder.mRatioView.setText(getRatioTipText(FIFTY));

        telecomCardHolder.mCardPassword.setText("");
        telecomCardHolder.mCardNum.setText("");
        clearButtonSelected(telecomCardHolder);
        telecomCardHolder.mFirstView.setSelected(true);
    }


    @Override
    public PaymentResponseInfo pay() throws ZLNetworkException {
        if (TextUtils.isEmpty(mCardNum) || TextUtils.isEmpty(mCardPassword)) {
            throw new InputValueException("mCardNum or mCardPassword is null");
        }
        if (mCardNum.length() < CARD_NUM_LENGTH || mCardPassword.length() < CARD_PASSWORD_LENGTH) {
            throw new InputValueException("input error CardNum, CardPassword");
        }
        PrepaidCardPaymentInfo prepaidCardPaymentInfo = new PrepaidCardPaymentInfo();
        prepaidCardPaymentInfo.setUserId(UserManager.getInstance().getUserId());
        prepaidCardPaymentInfo.setPayMoney(mSelectValue);
        prepaidCardPaymentInfo.setCardNum(mCardNum);
        prepaidCardPaymentInfo.setCardPassword(mCardPassword);
        prepaidCardPaymentInfo.setPayType(PaymentType.UNICOM_CARD_PAYMENT);
        return mPaymentWebService.prepaidCardPayment(prepaidCardPaymentInfo);
    }

    @Override
    public String getName() {
        return mContext.getResources().getString(R.string.unicom_card_txt);
    }

    @Override
    public List<String> getText() {
        List<String> showText = new ArrayList<String>();
        showText.add(mContext.getResources().getString(R.string.unicom_card_title_txt));
        showText.add(mContext.getResources().getString(R.string.fifty_txt));
        showText.add(mContext.getResources().getString(R.string.hundred_txt));
        return showText;
    }

    @Override
    public int convertMoney(int value) {
        return value * PrepaidCardPaymentInfo.PREPAID_CARD_RATIO;
    }

    @Override
    public String getRatioTipText(int value) {
        String text = mContext.getResources().getString(R.string.ratio_txt);
        return String.format(text, value, convertMoney(value));
    }

    private View.OnClickListener mFirstListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TelecomCardHolder telecomCardHolder = (TelecomCardHolder) v.getTag();
            clearButtonSelected(telecomCardHolder);
            telecomCardHolder.mFirstView.setSelected(true);
            telecomCardHolder.mRatioView.setText(getRatioTipText(FIFTY));
            mSelectValue = FIFTY;

        }
    };

    private View.OnClickListener mSecondListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TelecomCardHolder telecomCardHolder = (TelecomCardHolder) v.getTag();
            clearButtonSelected(telecomCardHolder);
            telecomCardHolder.mSecondView.setSelected(true);
            telecomCardHolder.mRatioView.setText(getRatioTipText(HUNDRED));
            mSelectValue = HUNDRED;
        }
    };

    private void clearButtonSelected(TelecomCardHolder telecomCardHolder) {
        telecomCardHolder.mFirstView.setSelected(false);
        telecomCardHolder.mSecondView.setSelected(false);
    }
}
