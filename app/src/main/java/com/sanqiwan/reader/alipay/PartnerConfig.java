package com.sanqiwan.reader.alipay;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-21
 * Time: 上午10:40
 * To change this template use File | Settings | File Templates.
 */
public class PartnerConfig {
    // 合作商户ID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
    public static final String PARTNER = "2088502726731201";
    // 商户收款的支付宝账号
    public static final String SELLER = "";
    // 商户（RSA）私钥
    public static final String RSA_PRIVATE = "";
    // 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。
    public static final String RSA_ALIPAY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCUBg15rEwZU/K+m81YEm1aZ2P0kl+NJj6csQtpktHAzJ7TP8U4wB3zfOjN2Nt1fUfVoJAM+aAMcEdEDUl1TeJoy0bRDOiGjTcbmqLsH4ULK+oNfcMnjmWE0Z1wlpAbSVz7RIyoWw62mDeuS3NOCLpwVJve6Ryrlq39QSNOTgpiFwIDAQAB";
    // 支付宝安全支付服务apk的名称，必须与assets目录下的apk名称一致
    public static final String ALIPAY_PLUGIN_NAME = "alipay_plugin_msp.apk";

}
