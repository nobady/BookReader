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
import com.sanqiwan.reader.model.SMSPaymentInfo;
import com.sanqiwan.reader.util.SMSUtil;
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
class UnicomPayment implements IPayment {
    private static final int PHONE_NUM_LENGTH = 11;
    private static final int TWO = 2;
    private LayoutInflater mInflater;
    private Context mContext;
    private String mUserName;
    private int mSelectValue;
    private String mPhoneNum;
    private PaymentWebService mPaymentWebService;

    public UnicomPayment() {
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
        mSelectValue = TWO;
        mPhoneNum = null;
        UnicomHolder unicomHolder = (UnicomHolder) view.getTag();
        unicomHolder.mUserName.setText(mUserName);
        unicomHolder.mPhoneNum.addTextChangedListener(mPhoneNumTextWatcher);
    }

    @Override
    public View newView() {
        UnicomHolder unicomHolder = new UnicomHolder();
        View view = mInflater.inflate(R.layout.unicom_pay, null);
        unicomHolder.mUserName = (TextView) view.findViewById(R.id.user);
        unicomHolder.mFirstView = (TextView) view.findViewById(R.id.msg_two);
        unicomHolder.mPhoneNum = (EditText) view.findViewById(R.id.phone_num);

        view.setTag(unicomHolder);
        return view;
    }

    @Override
    public PaymentResponseInfo pay() throws ZLNetworkException {
        if (TextUtils.isEmpty(mPhoneNum)) {
            throw new InputValueException("mPhoneNum is null");
        }
        if (mPhoneNum.length() < PHONE_NUM_LENGTH) {
            throw new InputValueException("input error PhoneNum");
        }
        SMSPaymentInfo smsPaymentInfo = new SMSPaymentInfo();
        smsPaymentInfo.setUserId(UserManager.getInstance().getUserId());
        smsPaymentInfo.setPayMoney(mSelectValue);
        smsPaymentInfo.setPhoneNum(mPhoneNum);
        smsPaymentInfo.setPayType(PaymentType.UNICOM_SMS_PAYMENT);
        PaymentResponseInfo responseInfo = mPaymentWebService.smsPayment(smsPaymentInfo);
        if (responseInfo.isSuccess() && !responseInfo.getMsg().equals(mContext.getString(R.string.no_service))) {
            String smsUri = responseInfo.getMsg();
            SMSUtil.startSMSEditor(smsUri, responseInfo.getMsg());
        }
        return responseInfo;
    }

    @Override
    public String getName() {
        return mContext.getResources().getString(R.string.unicom_txt);
    }

    @Override
    public List<String> getText() {
        List<String> showText = new ArrayList<String>();
        showText.add(mContext.getResources().getString(R.string.unicom_title_txt));
        showText.add(mContext.getResources().getString(R.string.two_txt));
        return showText;
    }

    @Override
    public int convertMoney(int value) {
        return value * SMSPaymentInfo.SMS_RATIO;
    }

    @Override
    public String getRatioTipText(int value) {
        String text = mContext.getResources().getString(R.string.ratio_txt);
        return String.format(text, value, convertMoney(value));
    }

    private class UnicomHolder {
        private TextView mUserName;
        private TextView mFirstView;
        private EditText mPhoneNum;
    }
    private PhoneNumTextWatcher mPhoneNumTextWatcher = new PhoneNumTextWatcher();
    private class PhoneNumTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mPhoneNum = s.toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private View.OnClickListener mFirstListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSelectValue = TWO;
        }
    };
}
