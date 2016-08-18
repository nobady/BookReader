package com.sanqiwan.reader.webservice;

import com.sanqiwan.reader.model.PaymentResponseInfo;
import com.sanqiwan.reader.model.PrepaidCardPaymentInfo;
import com.sanqiwan.reader.model.SMSPaymentInfo;
import com.sanqiwan.reader.payment.NullPostDataException;
import com.sanqiwan.reader.payment.NullResponseDataException;
import com.sanqiwan.reader.webservice.parser.ResponseDataParser;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;
import org.geometerplus.zlibrary.core.network.ZLNetworkManager;
import org.geometerplus.zlibrary.core.network.ZLNetworkRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-6
 * Time: 下午5:15
 * To change this template use File | Settings | File Templates.
 */
public class PaymentWebService {

    private static final int ALIPAY_RATIO = 97;
    private static final String ALIPAY_PAYMENT = "aliPayWapPayment";
    private static final String SMS_PAYMENT = "smsPayment";
    private static final String PREPAID_CARD_PAYMENT = "cardPayment";
    private UriBuilder mUriBuilder;
    private PaymentJsonBuilder mPaymentJsonBuilder;
    private String mUri;

    public PaymentWebService() {
        mUriBuilder = new UriBuilder(new WebServiceConfig());
        mPaymentJsonBuilder = new PaymentJsonBuilder();
    }

    public PaymentResponseInfo alipayPayment(int userId, int payMoney) throws ZLNetworkException {
        mUri = mUriBuilder.builderUriForGetAliWapPayOrder();
        //将id和金额封装为json
        String postData = mPaymentJsonBuilder.buildAlipayJson(userId, payMoney);

        if (postData == null) {
            throw new NullPostDataException("post\n" +
                    "        Log.i(\"TAG\", \"data=\" + postData);Data is null");
        }
        PaymentResponseInfo responseInfo = sendData(mUri, ALIPAY_PAYMENT, postData);
        if (responseInfo == null) {
            throw new NullResponseDataException("responseInfo is null");
        }
        responseInfo.setPayMoney(payMoney * ALIPAY_RATIO);
        //todo responseInfo.getOrderInfo是获取订单信息，在发送给支付宝。
        return responseInfo;
    }


    public PaymentResponseInfo smsPayment(SMSPaymentInfo smsPaymentInfo) throws ZLNetworkException, NullPostDataException, NullResponseDataException {
        mUri = mUriBuilder.builderUriForSendSMSPayment();
        String postData = mPaymentJsonBuilder.buildSMSPaymentJson(smsPaymentInfo);
        if (postData == null) {
            throw new NullPostDataException("postData is null");
        }
        PaymentResponseInfo responseInfo = sendData(mUri, SMS_PAYMENT, postData);
        if (responseInfo == null) {
            throw new NullResponseDataException("responseInfo is null");
        }
        responseInfo.setPayMoney(smsPaymentInfo.getPayMoney() * SMSPaymentInfo.SMS_RATIO);
        return responseInfo;
    }

    public PaymentResponseInfo prepaidCardPayment(PrepaidCardPaymentInfo prepaidCardPaymentInfo) throws ZLNetworkException, NullPostDataException, NullResponseDataException {
        mUri = mUriBuilder.builderUriForSendPrePaidCardPayment();
        String postData = mPaymentJsonBuilder.buildPrepaidCardPaymentJson(prepaidCardPaymentInfo);
        if (postData == null) {
            throw new NullPostDataException("postData is null");
        }
        PaymentResponseInfo responseInfo = sendData(mUri, PREPAID_CARD_PAYMENT, postData);
        if (responseInfo == null) {
            throw new NullResponseDataException("responseInfo is null");
        }
        responseInfo.setPayMoney(prepaidCardPaymentInfo.getPayMoney() * PrepaidCardPaymentInfo.PREPAID_CARD_RATIO);
        return responseInfo;
    }


    private PaymentResponseInfo sendData(String url, String postParam, String postData) throws ZLNetworkException {
        final HttpResult<PaymentResponseInfo> httpResult = new HttpResult<PaymentResponseInfo>();
        ZLNetworkRequest request = new ZLNetworkRequest(url, false) {
            @Override
            public void handleStream(InputStream inputStream, int length) throws IOException, ZLNetworkException {
                ResponseDataParser parser = new ResponseDataParser();
                httpResult.setResult(parser.parse(inputStream));
            }

            @Override
            public void doAfter(boolean success) throws ZLNetworkException {
            }
        };
        request.addPostParameter(postParam, postData);
        ZLNetworkManager.Instance().perform(request);
        return httpResult.getResult();
    }
}
