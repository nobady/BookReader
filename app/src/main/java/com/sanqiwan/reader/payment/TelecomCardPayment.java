package com.sanqiwan.reader.payment;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.PaymentResponseInfo;
import com.sanqiwan.reader.model.PrepaidCardPaymentInfo;
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
class TelecomCardPayment implements IPayment {
    static final int CARD_NUM_LENGTH = 15;
    static final int CARD_PASSWORD_LENGTH = 19;
    private static final int THIRTY = 30;
    private static final int FIFTY = 50;
    private static final int HUNDRED = 100;
    private LayoutInflater mInflater;
    private Context mContext;
    private String mUserName;
    private int mSelectValue;
    String mCardNum;
    String mCardPassword;
    PaymentWebService mPaymentWebService;

    public TelecomCardPayment() {
        mContext = AppContext.getInstance();
        mInflater = LayoutInflater.from(mContext);
        mUserName = UserManager.getInstance().getUserInfo().getUserName();
        mPaymentWebService = new PaymentWebService();
    }

    @Override
    public View getView(Context context, CmccConfirmDialog.CallBack callBack) {
        View view = newView();
        bindView(view);
        return view;
    }

    @Override
    public void bindView(View view) {
        mSelectValue = THIRTY;
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
        telecomCardHolder.mThirdView.setVisibility(View.VISIBLE);
        telecomCardHolder.mThirdView.setTag(telecomCardHolder);
        telecomCardHolder.mThirdView.setText(showText.get(3));
        telecomCardHolder.mThirdView.setOnClickListener(mThirdListener);
        telecomCardHolder.mCardNum.addTextChangedListener(mCardNumTextWatcher);
        telecomCardHolder.mCardPassword.addTextChangedListener(mCardPasswordTextWatcher);
        telecomCardHolder.mRatioView.setText(getRatioTipText(THIRTY));

        telecomCardHolder.mCardPassword.setText("");
        telecomCardHolder.mCardNum.setText("");
        clearButtonSelected(telecomCardHolder);
        telecomCardHolder.mFirstView.setSelected(true);
    }

    @Override
    public View newView() {
        TelecomCardHolder telecomCardHolder = new TelecomCardHolder();
        View view = mInflater.inflate(R.layout.telecom_unicom_card_pay, null);
        telecomCardHolder.mUserName = (TextView) view.findViewById(R.id.user);
        telecomCardHolder.mFirstView = (TextView) view.findViewById(R.id.card_ten);
        telecomCardHolder.mSecondView = (TextView) view.findViewById(R.id.card_twenty);
        telecomCardHolder.mThirdView = (TextView) view.findViewById(R.id.card_thirty);
        telecomCardHolder.mCardNum = (EditText) view.findViewById(R.id.card_num);
        telecomCardHolder.mCardPassword = (EditText) view.findViewById(R.id.card_password);
        telecomCardHolder.mRatioView = (TextView) view.findViewById(R.id.conversion_ratio);

        view.setTag(telecomCardHolder);
        return view;
    }

    @Override
    public PaymentResponseInfo pay() throws ZLNetworkException {
        if (TextUtils.isEmpty(mCardNum) || TextUtils.isEmpty(mCardPassword)) {
            throw new InputValueException("mCardNum, mCardPassword is null");
        }
        if (mCardNum.length() < CARD_NUM_LENGTH || mCardPassword.length() < CARD_PASSWORD_LENGTH) {
            throw new InputValueException("input error CardNum, CardPassword");
        }
        PrepaidCardPaymentInfo prepaidCardPaymentInfo = new PrepaidCardPaymentInfo();
        prepaidCardPaymentInfo.setUserId(UserManager.getInstance().getUserId());
        prepaidCardPaymentInfo.setPayMoney(mSelectValue);
        prepaidCardPaymentInfo.setCardNum(mCardNum);
        prepaidCardPaymentInfo.setCardPassword(mCardPassword);
        prepaidCardPaymentInfo.setPayType(PaymentType.TELECOM_CARD_PAYMENT);
        return mPaymentWebService.prepaidCardPayment(prepaidCardPaymentInfo);
    }

    @Override
    public String getName() {
        return mContext.getResources().getString(R.string.telecom_card_txt);
    }

    @Override
    public List<String> getText() {
        List<String> showText = new ArrayList<String>();
        showText.add(mContext.getResources().getString(R.string.telecom_card_title_txt));
        showText.add(mContext.getResources().getString(R.string.thirty_txt));
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

    class TelecomCardHolder {
        TextView mUserName;
        TextView mFirstView;
        TextView mSecondView;
        TextView mThirdView;
        EditText mCardNum;
        EditText mCardPassword;
        TextView mRatioView;
    }

    private View.OnClickListener mFirstListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TelecomCardHolder telecomCardHolder = (TelecomCardHolder) v.getTag();
            clearButtonSelected(telecomCardHolder);
            telecomCardHolder.mFirstView.setSelected(true);
            telecomCardHolder.mRatioView.setText(getRatioTipText(THIRTY));
            mSelectValue = THIRTY;

        }
    };

    private View.OnClickListener mSecondListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TelecomCardHolder telecomCardHolder = (TelecomCardHolder) v.getTag();
            clearButtonSelected(telecomCardHolder);
            telecomCardHolder.mSecondView.setSelected(true);
            telecomCardHolder.mRatioView.setText(getRatioTipText(FIFTY));
            mSelectValue = FIFTY;
        }
    };

    private View.OnClickListener mThirdListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TelecomCardHolder telecomCardHolder = (TelecomCardHolder) v.getTag();
            clearButtonSelected(telecomCardHolder);
            telecomCardHolder.mThirdView.setSelected(true);
            telecomCardHolder.mRatioView.setText(getRatioTipText(HUNDRED));
            mSelectValue = HUNDRED;
        }
    };

    CardNumTextWatcher mCardNumTextWatcher = new CardNumTextWatcher();
    CardPasswordTextWatcher mCardPasswordTextWatcher = new CardPasswordTextWatcher();

    class CardNumTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mCardNum = s.toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    class CardPasswordTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mCardPassword = s.toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private void clearButtonSelected(TelecomCardHolder telecomCardHolder) {
        telecomCardHolder.mFirstView.setSelected(false);
        telecomCardHolder.mSecondView.setSelected(false);
        telecomCardHolder.mThirdView.setSelected(false);
    }
}
