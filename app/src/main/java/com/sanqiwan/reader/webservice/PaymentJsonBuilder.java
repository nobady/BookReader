package com.sanqiwan.reader.webservice;

import com.sanqiwan.reader.model.PrepaidCardPaymentInfo;
import com.sanqiwan.reader.model.SMSPaymentInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-10-16
 * Time: 下午2:24
 * To change this template use File | Settings | File Templates.
 */
public class PaymentJsonBuilder {
    private static final String USER_ID =  "UserID";
    private static final String PHONE_NUM = "PhoneNum";
    private static final String PAY_MONEY = "PayMoney";
    private static final String PAY_TYPE = "PayType";
    private static final String CARD_NUM =  "CardNum";
    private static final String CARD_PASSWORD = "CardPassword";


    public String buildAlipayJson(int userId, int payMoney) {
        if (userId == 0 || payMoney == 0) {
            return null;
        }
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_ID, userId);
            jsonObject.put(PAY_MONEY, payMoney);

            jsonArray.put(jsonObject);
            return jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String buildSMSPaymentJson(SMSPaymentInfo smsPaymentInfo) {
        if (smsPaymentInfo == null) {
            return null;
        }
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_ID, smsPaymentInfo.getUserId());
            jsonObject.put(PAY_MONEY, smsPaymentInfo.getPayMoney());
            jsonObject.put(PHONE_NUM, smsPaymentInfo.getPhoneNum());
            jsonObject.put(PAY_TYPE, smsPaymentInfo.getPayType());
            jsonArray.put(jsonObject);
            return jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String buildPrepaidCardPaymentJson(PrepaidCardPaymentInfo cardInfo) {
        if (cardInfo == null) {
            return null;
        }
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_ID, cardInfo.getUserId());
            jsonObject.put(PAY_MONEY, cardInfo.getPayMoney());
            jsonObject.put(CARD_NUM, cardInfo.getCardNum());
            jsonObject.put(CARD_PASSWORD, cardInfo.getCardPassword());
            jsonObject.put(PAY_TYPE, cardInfo.getPayType());

            jsonArray.put(jsonObject);
            return jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
