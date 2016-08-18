package com.sanqiwan.reader.payment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IBM   支付方式（目前只有支付宝、电信充值卡、联通充值卡）
 * Date: 13-11-5
 * Time: 下午8:22
 * To change this template use File | Settings | File Templates.
 */
public class PaymentManager {

    public static final int ALIPAY_TYPE = 1;
    public static final int CMCC_TYPE = 2;
    public static final int TELECOM_TYPE = 3;
    public static final int UNICOM_TYPE = 4;
    public static final int TELECOM_CARD_TYPE = 5;
    public static final int UNICOM_CARD_TYPE = 6;
    private List<IPayment> mPayments;
    private static PaymentManager sPaymentManager;

    private PaymentManager() {
        mPayments = new ArrayList<IPayment>();

        mPayments.add(new AlipayPayment());
//        mPayments.add(new CmccPayment());
        //mPayments.add(new TelecomPayment());
        //mPayments.add(new UnicomPayment());
        mPayments.add(new WeChatPayment ());
        mPayments.add(new TelecomCardPayment());
        mPayments.add(new UnicomCardPayment());
    }

    public static PaymentManager getInstance() {
        if (sPaymentManager == null) {
            synchronized (PaymentManager.class) {
                if (sPaymentManager == null) {
                    sPaymentManager = new PaymentManager();
                }
            }
        }
        return sPaymentManager;
    }

    public List<IPayment> getPayments() {
        return mPayments;
    }

}
