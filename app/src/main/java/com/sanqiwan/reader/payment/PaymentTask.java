package com.sanqiwan.reader.payment;

import android.os.AsyncTask;
import com.sanqiwan.reader.model.PaymentResponseInfo;
import com.sanqiwan.reader.util.UIUtil;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

/**
 * Created with IntelliJ IDEA.
 * User: IBM   进行支付的类
 * Date: 13-11-8
 * Time: 上午11:07
 * To change this template use File | Settings | File Templates.
 */
public class PaymentTask extends AsyncTask<Void, Void, PaymentResponseInfo> {

    public interface Callback {
        /**
         * 显示支付时的加载对话框
         */
        public void onPaymentLoadingShow();

        /**
         * 隐藏加载对话框
         */
        public void onPaymentLoadingDismiss();

        /**
         * 支付成功
         * @param paymentResponseInfo
         */
        public void onPaySuccess(PaymentResponseInfo paymentResponseInfo);

        /**
         * 支付失败
         * @param paymentResponseInfo
         */
        public void onPayFailed(PaymentResponseInfo paymentResponseInfo);

        /**
         * 网络异常
         */
        public void onNetworkException();

        public void onNullPostDataException();

        public void onNullValueException();

        public void onNullResponseDataException();
    }

    /**
     * 支付方式的接口类型
     */
    private IPayment mIPayment;
    /**
     * 支付结果的回调
     */
    private Callback mCallback;


    public PaymentTask(IPayment iPayment, Callback callback) {
        mIPayment = iPayment;
        mCallback = callback;
    }

    @Override
    protected PaymentResponseInfo doInBackground(Void... params) {
        try {
            if (mIPayment != null) {
                return mIPayment.pay();
            }
        } catch (InputValueException e) {
            if (mCallback != null) {
                UIUtil.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onNullValueException();
                    }
                });
            }
        } catch (NullPostDataException e) {
            if (mCallback != null) {
                UIUtil.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onNullPostDataException();
                    }
                });
            }
        } catch (NullResponseDataException e) {
            if (mCallback != null) {
                UIUtil.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onNullResponseDataException();
                    }
                });
            }
        } catch (ZLNetworkException e) {
            if (mCallback != null) {
                UIUtil.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onNetworkException();
                    }
                });
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(PaymentResponseInfo paymentResponseInfo) {
        if (mCallback == null) {
            return;
        }
        mCallback.onPaymentLoadingDismiss();
        if (paymentResponseInfo != null && paymentResponseInfo.isSuccess()) {
            mCallback.onPaySuccess(paymentResponseInfo);
        } else {
            mCallback.onPayFailed(paymentResponseInfo);
        }
    }

    @Override
    protected void onPreExecute() {
        if (mCallback == null) {
            return;
        }
        mCallback.onPaymentLoadingShow();
    }
}
