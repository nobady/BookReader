package com.sanqiwan.reader.engine;

import com.sanqiwan.reader.model.AccountResult;
import com.sanqiwan.reader.model.ConsumeRecord;
import com.sanqiwan.reader.model.PaymentRecord;
import com.sanqiwan.reader.model.UserInfo;
import com.sanqiwan.reader.preference.Settings;
import com.sanqiwan.reader.util.SecurityUtil;
import com.sanqiwan.reader.webservice.AccountWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.List;
import java.util.Observable;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/5/13
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserManager extends Observable {

    private static final String CERTFTION_STRING = "37wan&1106@qwsy";
    public static final int PASSWORD_OR_USERNAME_ERROR = -1;
    public static final int FORBID_LOGIN_ERROR = -2;
    public static final int NOT_KNOW_LOGIN_ERROR = 0;
    public static final int SUCCESS_CODE = 1;
    public static final int NOT_KNOW_ERROR = -100;
    public static final String NOT_KNOW_ERROR_STRING = "未知错误";
    public static final int NET_ERROR = -101;
    public static final String NET_ERROR_STRING = "网络错误，无法获取信息";

    private static UserManager sInstance;
    private int mUid;
    private UserInfo mUserInfo;
    private Settings mSettings;
    private AccountWebService mUserWebService;

    private UserManager() {
        mUserWebService = new AccountWebService();
        mSettings = Settings.getInstance();
        initUserInfo();
    }

    public static UserManager getInstance() {
        if (sInstance == null) {
            synchronized (UserManager.class) {
                if (sInstance == null) {
                    sInstance = new UserManager();
                }
            }
        }
        return sInstance;
    }

    private void initUserInfo() {
        mUserInfo = mSettings.getUserInfo();
        mUid = mUserInfo.getUid();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mUid > 0) {
                    UserInfo userInfo = null;
                    try {
                        userInfo = mUserWebService.getUserInfo(mUid, generateCert());
                    } catch (ZLNetworkException e) {
                        e.printStackTrace();
                    }
                    if (userInfo != null) {
                        mUserInfo = userInfo;
                        mUserInfo.setUid(mUid);
                        mSettings.setUserInfo(mUserInfo);        //更新userinfo到配置
                    }
                }
            }
        }).start();
    }

    /**
     * 登录
     *
     * @param userName 用户名
     * @param passWord 密码
     * @return 成功返回uid，失败返回0 -1用户名或密码错误 -2禁止登录会员帐号
     *         耗时
     */
    public AccountResult login(String userName, String passWord) {
        AccountResult result = null;
        try {
            result = mUserWebService.getLoginResult(userName, SecurityUtil.md5(passWord));
        } catch (ZLNetworkException e) {
        }
        if (result == null) {
            result = new AccountResult(NET_ERROR, NET_ERROR_STRING);
        }
        if (result.getCode() > 0) {
            mUid = result.getCode();
            mUserInfo = getUserInfo(true);
            mUserInfo.setUid(mUid);
            mSettings.setUserInfo(mUserInfo);
            setChanged();
            notifyObservers();
        }
        return result;
    }

    public void setAccountResult(AccountResult result){
        if (result.getCode() > 0) {
            mUid = result.getCode();
            mUserInfo = getUserInfo(true);
            mUserInfo.setUid(mUid);
            mSettings.setUserInfo(mUserInfo);
        }
    }

    /**
     * 注销账户
     */
    public void logout() {
        mUid = 0;
        mSettings.removeUserInfo();
        mUserInfo = null;
        setChanged();
        notifyObservers();
    }

    /**
     * 判断是否登录
     *
     * @return
     */
    public boolean isLogin() {
        return mUid > 0 ? true : false;
    }

    /**
     * 注册帐号
     *
     * @param userName 用户名
     * @param password 密码
     * @param email    邮箱
     * @return 返回结果，包括返回码和返回信息，返回码大于0的时候是用户id
     */
    public AccountResult register(String userName, String password, String email) {
        AccountResult result = null;
        try {
            result = mUserWebService.getUserRegisterResult(userName, SecurityUtil.md5(password), email);
        } catch (ZLNetworkException e) {
        }

        if (result == null) {
            result = new AccountResult(NET_ERROR, NET_ERROR_STRING);
        }

        if (result.getCode() > 0) {
            mUid = result.getCode();
            getUserInfo(true);
            setChanged();
            notifyObservers();
        }
        return result;
    }

    /**
     * 注册帐号 第一步骤，获取密码
     *
     * @param phoneNumber
     * @return 耗时
     */
    public AccountResult register(String phoneNumber) {
        AccountResult result = null;
        try {
            result = mUserWebService.getRegisterSMSResult(phoneNumber);
        } catch (ZLNetworkException e) {
        }
        if (result == null) {
            result = new AccountResult(NET_ERROR, NET_ERROR_STRING);
        }
        if (result.getCode() == SUCCESS_CODE ) {
            setChanged();
            notifyObservers();
        }
        return result;
    }

    /**
     * 注册帐号 第二步骤，确认
     *
     * @param phoneNumber
     * @return 耗时
     */
    public AccountResult registerConfirm(String phoneNumber, String password) {
        AccountResult result = null;
        try {
            result = mUserWebService.getRegisterResult(phoneNumber, SecurityUtil.md5(password));
        } catch (ZLNetworkException e) {
        }
        if (result == null) {
            result = new AccountResult(NET_ERROR, NET_ERROR_STRING);
        }
        return result;
    }

    /**
     * 找回密码
     *
     * @return 耗时
     */
    public AccountResult findPassword(String userName, String email) {
        AccountResult result = null;
        try {
            result = mUserWebService.getPasswordBackResult(userName, email);
        } catch (ZLNetworkException e) {
        }
        if (result == null) {
            result = new AccountResult(NET_ERROR, NET_ERROR_STRING);
        }
        return result;
    }

    /**
     * 获取用户信息
     *
     * @return 耗时
     */
    public UserInfo getUserInfo() {
        if (isLogin()) {
            return mUserInfo;
        }
        return null;
    }

    /**
     * @param forcedRefresh 是否刷新本地用户信息
     * @return
     */
    public UserInfo getUserInfo(boolean forcedRefresh) {
        if (isLogin() && (mUserInfo == null || forcedRefresh)) {
            UserInfo userInfo = null;
            try {
                userInfo = mUserWebService.getUserInfo(mUid, generateCert());
            } catch (ZLNetworkException e) {
            }
            if (userInfo != null) {
                mUserInfo = userInfo;
                mUserInfo.setUid(mUid);
                mSettings.setUserInfo(mUserInfo);        //更新userinfo到配置
            }
        }
        return mUserInfo;
    }

    public int getUserId() {
        return mUid;
    }

    public String generateCert() {
        return SecurityUtil.md5(SecurityUtil.md5(mUid + CERTFTION_STRING).
                substring(5, 17).toLowerCase()).substring(3, 18).toLowerCase();
    }

    public AccountResult modifyPassword(String oldPassword, String newPassword) {
        if (!isLogin()) {
            return new AccountResult(NOT_KNOW_ERROR, NOT_KNOW_ERROR_STRING);
        }
        oldPassword = SecurityUtil.md5(oldPassword);
        newPassword = SecurityUtil.md5(newPassword);
        AccountResult result = null;
        try {
            result = mUserWebService.modifyPassword(getUserId(), oldPassword, newPassword, generateCert());
        } catch (ZLNetworkException e) {
        }
        if (result == null) {
            result = new AccountResult(NET_ERROR, NET_ERROR_STRING);
        }
        return result;
    }

    public List<PaymentRecord> getPaymentRecords() {
        if (!isLogin()) {
            return null;
        }
        try {
            return mUserWebService.getPaymentRecord(mUid, generateCert());
        } catch (ZLNetworkException e) {

        }
        return null;
    }

    public List<ConsumeRecord> getConsumeRecords(int page) {
        if (!isLogin()) {
            return null;
        }
        try {
            return mUserWebService.getConsumeRecord(mUid, page, generateCert());
        } catch (ZLNetworkException e) {

        }
        return null;
    }
}
