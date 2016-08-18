package com.sanqiwan.reader.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.PaymentResponseInfo;
import com.sanqiwan.reader.util.UIUtil;
import com.sanqiwan.reader.view.CmccConfirmDialog;
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
public class CmccPayment implements IPayment {
    private static final int RATIO = 50;
    private static final int TEN = 10;
    private static final int TWENTY = 20;
    private static final int THIRTY = 30;
    private LayoutInflater mInflater;
    private Context mContext;
    private String mUserName;
    int mSelectValue;
    private CmccConfirmDialog.CallBack mCallback;

    CmccPayment() {
        mContext = AppContext.getInstance();
        mInflater = LayoutInflater.from(mContext);
        mUserName = UserManager.getInstance().getUserInfo().getUserName();
    }

    @Override
    public String getName() {
        return mContext.getResources().getString(R.string.cmccpay_txt);
    }

    @Override
    public View getView(Context context, CmccConfirmDialog.CallBack callBack) {
        mContext = context;
        mCallback = callBack;
        View view = newView();
        bindView(view);
        return view;
    }

    @Override
    public List<String> getText() {
        List<String> showText = new ArrayList<String>();
        showText.add(mContext.getResources().getString(R.string.cmccpay_title_txt));
        showText.add(mContext.getResources().getString(R.string.ten_txt));
        showText.add(mContext.getResources().getString(R.string.twenty_txt));
        showText.add(mContext.getResources().getString(R.string.thirty_txt));
        showText.add(mContext.getResources().getString(R.string.ratio_txt));
        return showText;
    }

    @Override
    public void bindView(View view) {
        mSelectValue = TEN;
        CmccHolder cmccHolder = (CmccHolder) view.getTag();
        cmccHolder.mUserName.setText(mUserName);
        cmccHolder.mFirstView.setTag(cmccHolder);
        cmccHolder.mFirstView.setOnClickListener(mFirstListener);
        cmccHolder.mSecondView.setTag(cmccHolder);
        cmccHolder.mSecondView.setOnClickListener(mSecondListener);
        cmccHolder.mThirdView.setTag(cmccHolder);
        cmccHolder.mThirdView.setOnClickListener(mThirdListener);
        cmccHolder.mRatioView.setText(getRatioTipText(TEN));
        clearButtonSelected(cmccHolder);
        cmccHolder.mFirstView.setSelected(true);
    }

    @Override
    public View newView() {
        CmccHolder cmccHolder = new CmccHolder();
        View view = mInflater.inflate(R.layout.cmcc_pay, null);
        cmccHolder.mUserName = (TextView) view.findViewById(R.id.user);
        cmccHolder.mFirstView = (TextView) view.findViewById(R.id.msg_ten);
        cmccHolder.mSecondView = (TextView) view.findViewById(R.id.msg_twenty);
        cmccHolder.mThirdView = (TextView) view.findViewById(R.id.msg_thirty);
        cmccHolder.mRatioView = (TextView) view.findViewById(R.id.conversion_ratio);

        view.setTag(cmccHolder);
        return view;
    }

    @Override
    public PaymentResponseInfo pay() throws ZLNetworkException {
        UIUtil.getHandler().post(new Runnable() {
            @Override
            public void run() {
                new CmccConfirmDialog(mContext, R.layout.cmcc_confirm_dialog,
                        R.style.dialog_style, mSelectValue, mCallback).show();
            }
        });
        return null;
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

    private class CmccHolder {
        private TextView mUserName;
        private TextView mFirstView;
        private TextView mSecondView;
        private TextView mThirdView;
        private TextView mRatioView;
    }

    private View.OnClickListener mFirstListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CmccHolder holder = (CmccHolder) v.getTag();
            clearButtonSelected(holder);
            holder.mFirstView.setSelected(true);
            holder.mRatioView.setText(getRatioTipText(TEN));
            mSelectValue = TEN;
        }
    };

    private View.OnClickListener mSecondListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CmccHolder holder = (CmccHolder) v.getTag();
            clearButtonSelected(holder);
            holder.mSecondView.setSelected(true);
            holder.mRatioView.setText(getRatioTipText(TWENTY));
            mSelectValue = TWENTY;
        }
    };

    private View.OnClickListener mThirdListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CmccHolder holder = (CmccHolder) v.getTag();
            clearButtonSelected(holder);
            holder.mThirdView.setSelected(true);
            holder.mRatioView.setText(getRatioTipText(THIRTY));
            mSelectValue = THIRTY;
        }
    };

    private void clearButtonSelected(CmccHolder telecomHolder) {
        telecomHolder.mFirstView.setSelected(false);
        telecomHolder.mSecondView.setSelected(false);
        telecomHolder.mThirdView.setSelected(false);
    }

}
