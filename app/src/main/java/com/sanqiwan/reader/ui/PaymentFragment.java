package com.sanqiwan.reader.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.alipay.AlixId;
import com.sanqiwan.reader.alipay.BaseHelper;
import com.sanqiwan.reader.alipay.MobileSecurePayer;
import com.sanqiwan.reader.alipay.PayResult;
import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.PaymentResponseInfo;
import com.sanqiwan.reader.payment.IPayment;
import com.sanqiwan.reader.payment.PaymentManager;
import com.sanqiwan.reader.payment.PaymentTask;
import com.sanqiwan.reader.payment.ui.PaymentDetailView;
import com.sanqiwan.reader.payment.ui.PaymentListView;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import com.sanqiwan.reader.view.CmccConfirmDialog;
import com.sanqiwan.reader.view.LoadingProgressDialog;

import static org.geometerplus.fbreader.formats.html.HtmlTag.P;

/**
 * Created with IntelliJ IDEA.
 * User: IBM     充值页面
 * Date: 13-11-2
 * Time: 上午10:27
 * To change this template use File | Settings | File Templates.
 */
public class PaymentFragment extends BaseFragment implements View.OnClickListener {

    private static final String ALIPAY_ERROR = "0";
    private LoadingProgressDialog mProgressDialog;
    /**
     * 更加具体的充值方式，显示view
     */
    private PaymentDetailView mPaymentDetailView;
    /**
     * 选择充值方式的view
     */
    private PaymentListView mPaymentListView;
    private ViewGroup mPaymentTop;
    private ImageView mBackView;
    private TextView mTitleView;

    private TextView mNext;
    private Context mContext;
    private View mFragmentView;
    private LayoutInflater mInflater;
    //商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALvMfwpU4n1RUr47+D38ETmJIiAiKYq2GG+nxmxnmmRth5UhyK6gQAhri7kVAKWQrbc2x9pu88JNkra7SyxoK4m8gY4DpaJM6tJ5Zj/l1RHoykkvGzVL77cXl3IRfm/sIGnMFId6jfzShUSrDWCjn3+BxTS/LfivJY/CHO9a5TT/AgMBAAECgYAYMZkwQI1JLb/mKB2sBpxFeEhAMYZ+i0UMpWWns5UtWPr9mHOdI6H8M7a1amJB8iifENygZGjc4FeFWQ2vM+BfzqVEgqfuXnhCg2p7Lt3drydMCGaVNrdpc6qO+XZm9QxK/3iDxBvCFqXM7hdLaK/cgQU+UdYew4JYrW2CEzEtcQJBAOhL5v+QoD9NX7ZTJKpyiSCvLvzslL2IOkBDKNY0IBgqgbb4Z2qV0JBfVEOLXuchP7IAhJqkEB41n3EvxpnQunkCQQDO9jdYmoKxY88CHLMwrhuYYZfLb53hAotFg+5jqpk4reSSiD4fNHmvlHjKlwjNON58HtJu7tvu/VW/IzTM/Q03AkEArRiaKgfYETOTxaXQ5Z3xZsSDi/6ym0TeuzIQRQWUgM1z2Sbi0P6P6nTOsoIxFqOk9fwC08S7KfelNs91+voZaQJBAIROEoOqAvK0A5ouDr+IDkwT155N9Hfr0GzYMoW0gc8iG4UxY8U/HquIFtAsTYwkVtayPYR9Xkwh17chIpgJDXECQDarkHhPjeufHL2y8ZyiOmWmJJlEXqnlrMMY3CCtKlQjtACY+/0AjjUY8YxNLpln4O/IWGxC01PL6JPIIdjqW80=";
    private static final int SDK_PAY_FLAG = 2;

    public static PaymentFragment newFragment() {
        return new PaymentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        mInflater = inflater;
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.payment, container, false);
            initTopView();
            if (!UserManager.getInstance().isLogin()) {
                MainActivity.openMainActivity(getActivity(), MainActivity.MINE_FRAGMENT_ID);
            } else {
                initTextView();
            }
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
                getActivity().getSupportFragmentManager().popBackStack();
                return;
            case R.id.btn_next:  //进行支付
                AsyncTaskUtil.execute(new PaymentTask(mPaymentDetailView.getCurrentPayment(), mPaymentCallback));
                return;
        }
    }

    private void initTextView() {
        mNext = (TextView) mFragmentView.findViewById(R.id.btn_next);
        mNext.setOnClickListener(this);
        mPaymentDetailView = (PaymentDetailView) mFragmentView.findViewById(R.id.payment_ui);
        mPaymentListView = (PaymentListView) mFragmentView.findViewById(R.id.payment_channel);
        mPaymentListView.setPaymentList(PaymentManager.getInstance().getPayments());
        mPaymentListView.setOnClickListener(mPaymentListener);
        mPaymentDetailView.setPayment(PaymentManager.getInstance().getPayments().get(0), mContext, mCallback);
    }

    private View.OnClickListener mPaymentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            IPayment payment = (IPayment) v.getTag();
            mPaymentListView.clearChildrenSelect();
            v.setSelected(true);
            mPaymentDetailView.setPayment(payment, mContext, mCallback);
        }
    };

    private void initTopView() {
        mPaymentTop = (ViewGroup) mFragmentView.findViewById(R.id.payment_top);
        mBackView = (ImageView) mPaymentTop.findViewById(R.id.btn_return);
        mBackView.setOnClickListener(this);
        mTitleView = (TextView) mPaymentTop.findViewById(R.id.top_title);
        mTitleView.setText(getString(R.string.payment_title_txt));
    }

    private CmccConfirmDialog.CallBack mCallback = new CmccConfirmDialog.CallBack() {

        @Override
        public void onOpenPaymentSuccessFragment(PaymentResponseInfo responseInfo) {
            openPaymentSuccessFragment(responseInfo);
        }
    };

    private void openPaymentSuccessFragment(PaymentResponseInfo responseInfo) {
        getActivity().getSupportFragmentManager().popBackStack();
        MainActivity.openSubFragment(PaymentSuccessFragment.newFragment(responseInfo.getMsg(), responseInfo.getPayMoney()));
    }
    //支付结果的回调
    private PaymentTask.Callback mPaymentCallback = new PaymentTask.Callback() {
        @Override
        public void onPaymentLoadingShow() {
            mProgressDialog = new LoadingProgressDialog(mContext);
            mProgressDialog.show();
        }

        @Override
        public void onPaymentLoadingDismiss() {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }

        @Override
        public void onPaySuccess(PaymentResponseInfo responseInfo) {
            if (!TextUtils.isEmpty(responseInfo.getOrderInfo()) && !responseInfo.getOrderInfo().equals(ALIPAY_ERROR)) {
                responseInfo.setIsSuccess(false);
                alipay(responseInfo.getOrderInfo());   //真正的支付
            }
            if (responseInfo.isSuccess()) {
                openPaymentSuccessFragment(responseInfo);   //打开支付成功的页面
            }
        }

        @Override
        public void onPayFailed(PaymentResponseInfo paymentResponseInfo) {
            if (paymentResponseInfo != null && (!paymentResponseInfo.isSuccess())) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.payment_failed_tip), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onNetworkException() {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.payment_network_tip), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNullPostDataException() {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.payment_error_tip), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNullValueException() {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.payment_tip), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNullResponseDataException() {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.payment_response_tip), Toast.LENGTH_LONG).show();
        }
    };


    /**
     * the OnCancelListener for lephone platform. lephone系统使用到的取消dialog监听
     */
    private ProgressDialog mProgress = null;

    private void alipay(String orderInfo) {
        // 检测安全支付服务是否安装
//        MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(mContext);
//        boolean isMobile_spExist = mspHelper.detectMobile_sp();
//        if (!isMobile_spExist) {
//            return;
//        }
        // 根据订单信息开始进行支付
        try {
            // 调用,pay方法进行支付
//            MobileSecurePayer msp = new MobileSecurePayer();
//            Log.i("TAG", "orderinfor=" + orderInfo);
//            boolean bRet = msp.pay(orderInfo, mHandler, AlixId.RQF_PAY, getActivity());
            pay(orderInfo);
            if (true) {
                // 显示“正在支付”进度条
                closeProgress();
                mProgress = BaseHelper.showProgress(getActivity(), null, "正在支付", false, true);
            }
        } catch (Exception ex) {
            Toast.makeText(mContext, R.string.remote_call_failed, Toast.LENGTH_SHORT).show();
        }
    }

    // 这里接收支付结果，支付宝手机端同步通知
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                String strRet = (String) msg.obj;
                switch (msg.what) {
                    case AlixId.RQF_PAY: {
                        closeProgress();
                        // 处理交易结果
                        try {
                            // 获取交易状态码，具体状态代码请参看文档
                            String tradeStatus = "resultStatus={";
                            int imemoStart = strRet.indexOf("resultStatus=");
                            imemoStart += tradeStatus.length();
                            int imemoEnd = strRet.indexOf("};memo=");
                            tradeStatus = strRet.substring(imemoStart, imemoEnd);
                            if (tradeStatus.equals("9000"))//判断交易状态码，只有9000表示交易成功
                                BaseHelper.showDialog(getActivity(), "提示", "支付成功。交易状态码：" + tradeStatus, R.drawable.infoicon);
                            else
                                BaseHelper.showDialog(getActivity(), "提示", "支付失败。交易状态码:"
                                        + tradeStatus, R.drawable.infoicon);
                        } catch (Exception e) {
                            e.printStackTrace();
                            BaseHelper.showDialog(getActivity(), "提示", strRet, R.drawable.infoicon);
                        }
                    }
                    break;
                    case SDK_PAY_FLAG: {
                        mProgress.dismiss();
                        PayResult payResult = new PayResult((String) msg.obj);

                        // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                        String resultInfo = payResult.getResult();
                        Log.i("TAG", "resultInfo" + resultInfo);
                        String resultStatus = payResult.getResultStatus();
                        Log.i("TAG", "resultStatus" + resultStatus);
                        Log.i("TAG", "payResult.getMemo()" + payResult.getMemo());

                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            Toast.makeText(getActivity(), "支付成功",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // 判断resultStatus 为非“9000”则代表可能支付失败
                            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            if (TextUtils.equals(resultStatus, "8000")) {
                                Toast.makeText(getActivity(), "支付结果确认中",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                Toast.makeText(getActivity(), "支付失败",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                        break;
                    }
                }

                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public static class AlixOnCancelListener implements DialogInterface.OnCancelListener {
        Activity mcontext;

        public AlixOnCancelListener(Activity context) {
            mcontext = context;
        }

        public void onCancel(DialogInterface dialog) {
            mcontext.onKeyDown(KeyEvent.KEYCODE_BACK, null);
        }

    }

    // close the progress bar
    // 关闭进度框
    void closeProgress() {
        try {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回键监听事件
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getActivity().getFragmentManager().popBackStack();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mProgress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void pay(final String orderInfo) {

//        // 对订单做RSA 签名
//        String sign = SignUtils.sign(orderInfo, RSA_PRIVATE);

//        try {
        // 仅需对sign 做URL编码
//            final String orderInfoENcoder = URLEncoder.encode(orderInfo, "UTF-8");
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(getActivity());
                // 调用支付接口，获取支付结果
                String result = alipay.pay(orderInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        // 完整的符合支付宝参数规范的订单信息
//        final String payInfo = orderInfo + "\"&"
//                + getSignType();


        // 必须异步调用

    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }
}