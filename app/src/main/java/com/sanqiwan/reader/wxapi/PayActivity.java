package com.sanqiwan.reader.wxapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.widget.TextView;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PayActivity extends Activity {

    private static final String TAG = "PayActivity";

    PayReq req;
    final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
    TextView show;
    Map<String, String> resultunifiedorder;
    StringBuffer sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay);
//        show = (TextView) findViewById(R.id.editText_prepay_id);
        req = new PayReq();
        sb = new StringBuffer();
        //注册APPID
        msgApi.registerApp(Constants.APP_ID);
        //生成prepay_id
//        Button payBtn = (Button) findViewById(R.id.unifiedorder_btn);
//        payBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
//                getPrepayId.execute();
//            }
//        });
        //支付按钮
//        Button appayBtn = (Button) findViewById(R.id.appay_btn);
//        appayBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendPayReq();
//            }
//        });

        //生成签名参数
//        Button appay_pre_btn = (Button) findViewById(R.id.appay_pre_btn);
//        appay_pre_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                genPayReq();
//            }
//        });
    }


    /**
     * 生成签名
     */

    private String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.API_KEY);
        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        Log.e("orion", packageSign);
        return packageSign;
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.API_KEY);
        this.sb.append("sign str\n" + sb.toString() + "\n\n");
        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
        Log.e("orion", appSign);
        return appSign;
    }

    private String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");
            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");

        Log.e("orion", sb.toString());
        return sb.toString();
    }

    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

        private ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(PayActivity.this, "提示", "正在获取预支付订单...");
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            if (dialog != null) {
                dialog.dismiss();
            }
            sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
            show.setText(sb.toString());
            resultunifiedorder = result;

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {

            String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
            String entity = genProductArgs();

            Log.e("orion", entity);

            byte[] buf = Util.httpPost(url, entity);

            String content = new String(buf);
            Log.e("orion", content);
            Map<String, String> xml = decodeXml(content);
            return xml;
        }
    }


    public Map<String, String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if ("xml".equals(nodeName) == false) {
                            //实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion", e.toString());
        }
        return null;

    }


    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }


    private String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private String genProductArgs() {
        StringBuffer xml = new StringBuffer();
        try {
            String nonceStr = genNonceStr();
            xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<>();
            packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));//微信分配的公众账号ID
            packageParams.add(new BasicNameValuePair("body", "APP pay test"));//商品或支付单简要描述
            packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));//微信支付分配的商户号
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));//随机字符串，不长于32位。推荐随机数生成算法
            packageParams.add(new BasicNameValuePair("notify_url", "http://121.40.35.3/test"));//接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
            packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));//商户系统内部的订单号
            packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));//APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP
            packageParams.add(new BasicNameValuePair("total_fee", "1"));//订单总金额，单位为分
            packageParams.add(new BasicNameValuePair("trade_type", "APP"));//交易类型
            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));//签名
            String xmlstring = toXml(packageParams);
            return xmlstring;
        } catch (Exception e) {
            Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }
    }

    private void genPayReq() {

        req.appId = Constants.APP_ID;//公众账号ID
        req.partnerId = Constants.MCH_ID;//商户号
        req.prepayId = resultunifiedorder.get("prepay_id");//预支付交易会话ID
        req.packageValue = "prepay_id=" + resultunifiedorder.get("prepay_id");//暂填写固定值Sign=WXPay
        req.nonceStr = genNonceStr();//随机字符串
        req.timeStamp = String.valueOf(genTimeStamp());//时间戳


        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        req.sign = genAppSign(signParams);//签名

        sb.append("sign\n" + req.sign + "\n\n");

        show.setText(sb.toString());

        Log.e("orion", signParams.toString());

    }

    private void sendPayReq() {
        msgApi.registerApp(Constants.APP_ID);
        msgApi.sendReq(req);
    }
}

