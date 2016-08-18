package com.sanqiwan.reader.webservice;

import com.sanqiwan.reader.model.AccountResult;
import com.sanqiwan.reader.model.ConsumeRecord;
import com.sanqiwan.reader.model.PaymentRecord;
import com.sanqiwan.reader.model.UserInfo;
import com.sanqiwan.reader.webservice.parser.AccountResultParser;
import com.sanqiwan.reader.webservice.parser.ConsumeRecordParser;
import com.sanqiwan.reader.webservice.parser.PaymentRecordParser;
import com.sanqiwan.reader.webservice.parser.UserInfoParser;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.List;

/**
 * 用户操作的webservice
 */
public class AccountWebService {

    private UriBuilder mUriBuilder;

    public AccountWebService() {
        mUriBuilder = new UriBuilder(new WebServiceConfig());
    }

    public AccountResult getLoginResult(String userName, String userPwd) throws ZLNetworkException {
        HttpRequester<AccountResult> httpRequester = new HttpRequester<AccountResult>();
        return httpRequester.request(
                mUriBuilder.builderUriForGetLoginResult(userName, userPwd), new AccountResultParser());
    }

    public AccountResult getRegisterSMSResult(String phoneNumber) throws ZLNetworkException {
        HttpRequester<AccountResult> httpRequester = new HttpRequester<AccountResult>();
        return httpRequester.request(mUriBuilder.builderUriForGetRegisterSMSResult(phoneNumber), new AccountResultParser());
    }

    public AccountResult getRegisterResult(String phoneNumber, String password) throws ZLNetworkException {
        HttpRequester<AccountResult> httpRequester = new HttpRequester<AccountResult>();
        return httpRequester.request(mUriBuilder.builderUriForGetRegisterResult(phoneNumber, password), new AccountResultParser());
    }

    public AccountResult getUserRegisterResult(String userName, String password, String email) throws ZLNetworkException {
        HttpRequester<AccountResult> httpRequester = new HttpRequester<AccountResult>();
        return httpRequester.request(mUriBuilder.builderUriForUserRegister(userName, password, email), new AccountResultParser());
    }

    public UserInfo getUserInfo(int userId, String certString) throws ZLNetworkException {
        HttpRequester<UserInfo> httpRequester = new HttpRequester<UserInfo>();
        UserInfo userInfo = httpRequester.request(mUriBuilder.builderUriForGetUserInfo(userId, certString), new UserInfoParser());
        return userInfo;
    }

    public AccountResult getPasswordBackResult(String userName, String email) throws ZLNetworkException {
        HttpRequester<AccountResult> httpRequester = new HttpRequester<AccountResult>();
        return httpRequester.request(
                mUriBuilder.builderUriForGetBackPasswordResult(userName, email), new AccountResultParser());
    }

    public AccountResult modifyPassword(int userId, String oldPassword, String newPassword, String certString) throws ZLNetworkException {
        HttpRequester<AccountResult> httpRequester = new HttpRequester<AccountResult>();
        return httpRequester.request(
                mUriBuilder.builderUriForModifyPassword(userId, oldPassword, newPassword, certString), new AccountResultParser());
    }

    public List<PaymentRecord> getPaymentRecord(int userId, String certString) throws ZLNetworkException {
        HttpRequester<PaymentRecord> httpRequester = new HttpRequester<PaymentRecord>();
        return httpRequester.requestList(mUriBuilder.builderUriForGetPaymentRecord(
                userId, certString), new PaymentRecordParser());
    }

    public List<ConsumeRecord> getConsumeRecord(int userId, int page, String certString) throws ZLNetworkException {
        HttpRequester<ConsumeRecord> httpRequester = new HttpRequester<ConsumeRecord>();
        return httpRequester.requestList(mUriBuilder.builderUriForGetConsumeRecord(
                userId, page, certString), new ConsumeRecordParser());
    }
}
