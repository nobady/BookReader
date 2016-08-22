package com.sanqiwan.reader.webservice;

import android.net.Uri;

import com.sanqiwan.reader.util.SecurityUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/25/13
 * Time: 11:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class UriBuilder {

    private static final String ACTION_GET_SEARCH_BOOKS = "GetSearchBooks";
    private static final String ACTION_GET_CHAPTER_INFO = "GetChapterInfo";
    private static final String ACTION_GET_NEW_TOP_BOOKS = "GetNewTopBooks";
    private static final String ACTION_GET_CONTENT_TABLE = "GetChapterList";
    private static final String ACTION_GET_BOOK_INFO = "GetBookInfo";
    private static final String ACTION_GET_NEW_UPDATE_BOOKS = "GetNewUpdateBooks";
    private static final String ACTION_GET_BOOKS_UPDATE = "GetBooksUpdate";
    private static final String ACTION_GET_VIP_CHAPTER_RESULT = "GetVIPChapterResult";
    private static final String ACTION_GET_LOGIN_RESULT = "GetLoginResult";
    private static final String ACTION_GET_USERINFO_RESULT = "GetUserInfo";
    private static final String ACTION_GET_BACK_PASSWORD = "GetBackPassword";
    private static final String ACTION_USER_ALIPAY_PAYMENT = "UserAliPayPayment";
    private static final String ACTION_USER_ALIPAY_WAP_PAYMENT = "UserAliPayWapPayment";
    private static final String ACTION_USER_WXPAY_PAYMENT = "UserWXPayPayment";
    private static final String ACTION_USER_SMS_PAYMENT = "UserSMSPayment";
    private static final String ACTION_USER_PREPAID_CARD = "UserPrepaidCardPayment";
    private static final String ACTION_GET_VIP_CHAPTER_ORDER = "GetVIPChapterOrder";
    private static final String ACTION_GET_BUY_VIP_CHAPTER_LIST = "GetBuyVIPChapterList";
    private static final String ACTION_MODIFY_PASSWORD = "modifyPassword";
    private static final String ACTION_GET_PAYMENT_RECORD = "GetPaymentRecord";
    private static final String ACTION_GET_CONSUME_RECORD = "GetConsumeRecord";

    private static final String ACTION_GET_REGISTER_SMS_RESULT = "GetRegisterSMSResult";
    private static final String ACTION_GET_REGISTER_RESULT = "GetRegisterResult";
    private static final String ACTION_USER_REGISTER = "userRegister";
    private static final String ACTION_GET_COMMENT_LIST = "GetCommentList";
    private static final String ACTION_GET_LATEST_COMMENT = "GetCommentsInfo";
    private static final String ACTION_SEND_COMMENT = "PublishComment";
    private static final String ACTION_GET_LATEST_CHAPTER = "GetLatestChapterList";
    private static final String ACTION_GET_SEARCH_SUGGESTION = "GetSearchSuggestion";
    private static final String ACTION_GET_HOT_READ_BOOKS = "GetHotReadBooks";
    private static final String ACTION_GET_NEW_FINISH_BOOKS = "GetNewFinishBooks";
    private static final String ACTION_GET_NEW_POTENTIAL_BOOKS = "GetNewPotentialBooks";
    private static final String ACTION_GET_RANK_LIST = "GetRankList";
    private static final String ACTION_GET_RANK_BOOK_LIST = "GetRankBookList";
    private static final String ACTION_GET_HOT_RECOMMEND_BOOKS = "hotfinishpotentiallist";

    private static final String KEY_SPID = "spid";
    private static final String KEY_SIGN = "sign";
    private static final String KEY_QTIME = "qtime";
    private static final String KEY_ACTION = "action";
    private static final String KEY_KEY = "key";
    private static final String KEY_BOOK_ID = "bookid";
    private static final String KEY_CHAPTER_ID = "chapterid";
    private static final String KEY_TYPE = "type";
    private static final String KEY_PAGE = "page";
    private static final String KEY_CATEGORY = "cate_id";
    private static final String KEY_SCENARIO = "scen_id";
    private static final String KEY_INFO_USER_ID = "userID";
    private static final String KEY_CERT = "Cert";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_PASSWORD = "userPwd";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CHAPTER_LIST = "chapterList";
    private static final String KEY_PHONE_NUMBER = "PhoneNumber";
    private static final String KEY_RECORD_USER_ID = "userId";
    private static final String KEY_NEW_PASSWORD = "newPassword";
    private static final String KEY_OLD_PASSWORD = "oldPassword";
    private static final String KEY_ORDER_BOOK_ID = "bookID";
    private static final String KEY_SINCE_ID = "since_id";
    private static final String KEY_MAX_ID = "max_id";
    private static final String KEY_PAGE_SIZE = "page_size";
    private static final String KEY_COMMENTS_TOP = "commentsTop";
    private static final String KEY_RANK_ID = "rankId";

    private WebServiceConfig mConfig;
    private Uri mBaseUri;

    public UriBuilder(WebServiceConfig config) {
        mConfig = config;
    }

    private Uri.Builder getBaseBuilder() {
        if (mBaseUri == null) {
            Uri.Builder builder = Uri.parse(mConfig.getHost()).buildUpon();
            builder.appendQueryParameter(KEY_SPID, mConfig.getSpid());
            mBaseUri = builder.build();
        }
        Uri.Builder builder = mBaseUri.buildUpon();
        String date = getDate();
        String sign = getSign(date);
        builder.appendQueryParameter(KEY_SIGN, sign);
        builder.appendQueryParameter(KEY_QTIME, date);
        return builder;
    }

    private String getSign(String date) {
        String sign = SecurityUtil.md5(mConfig.getSpid() + mConfig.getSecretKey() + date);
        sign = SecurityUtil.md5(sign);
        return sign;
    }

    private String getDate() {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(new Date());
    }

    public String buildUriForGetSearchBooks(String key, int pageIndex) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_SEARCH_BOOKS);
        builder.appendQueryParameter(KEY_KEY, key);
        builder.appendQueryParameter(KEY_PAGE, String.valueOf(pageIndex));
        return builder.toString();
    }

    public String buildUriForGetChapter(long bookId, long chapterId) {

        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_CHAPTER_INFO);
        builder.appendQueryParameter(KEY_BOOK_ID, String.valueOf(bookId));
        builder.appendQueryParameter(KEY_CHAPTER_ID, String.valueOf(chapterId));
        return builder.toString();
    }

    public String builderUriForGetNewTopBooks(String key, String type, int pageIndex) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_NEW_TOP_BOOKS);
        builder.appendQueryParameter(KEY_KEY, key);
        builder.appendQueryParameter(KEY_TYPE, type);
        builder.appendQueryParameter(KEY_PAGE, String.valueOf(pageIndex));
        return builder.toString();
    }

    public String builderUriForGetNewTopBooksByCategoryId(int cateId, String type, int pageIndex) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_NEW_TOP_BOOKS);
        builder.appendQueryParameter(KEY_CATEGORY, String.valueOf(cateId));
        builder.appendQueryParameter(KEY_TYPE, type);
        builder.appendQueryParameter(KEY_PAGE, String.valueOf(pageIndex));
        return builder.toString();
    }

    public String buildUriForGetBooks(int cateId, int scenId, int pageIndex) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_SEARCH_BOOKS);
        builder.appendQueryParameter(KEY_CATEGORY, String.valueOf(cateId));
        builder.appendQueryParameter(KEY_SCENARIO, String.valueOf(scenId));
        builder.appendQueryParameter(KEY_PAGE, String.valueOf(pageIndex));
        return builder.toString();
    }

    public String builderUriForGetTOC(long bookId, int pageIndex) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_CONTENT_TABLE);
        builder.appendQueryParameter(KEY_BOOK_ID, String.valueOf(bookId));
        builder.appendQueryParameter(KEY_PAGE, String.valueOf(pageIndex));
        return builder.toString();
    }

    public String builderUriForGetNewUpdateBooks(String key, int pageIndex) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_NEW_UPDATE_BOOKS);
        builder.appendQueryParameter(KEY_KEY, key);
        builder.appendQueryParameter(KEY_PAGE, String.valueOf(pageIndex));
        return builder.toString();
    }

    public String builderUriForGetNewUpdateBooksByCategoryId(int cateId, int pageIndex) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_NEW_UPDATE_BOOKS);
        builder.appendQueryParameter(KEY_CATEGORY, String.valueOf(cateId));
        builder.appendQueryParameter(KEY_PAGE, String.valueOf(pageIndex));
        return builder.toString();
    }

    public String builderUriForGetBookInfo(long bookId) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_BOOK_INFO);
        builder.appendQueryParameter(KEY_BOOK_ID, String.valueOf(bookId));
        return builder.toString();
    }

    public String builderUriForGetBooksUpdate(long bookId, long chapterId) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_BOOKS_UPDATE);
        builder.appendQueryParameter(KEY_BOOK_ID, String.valueOf(bookId));
        builder.appendQueryParameter(KEY_CHAPTER_ID, String.valueOf(chapterId));
        return builder.toString();
    }

    public String builderUriForGetVipChapterResult(long bookId, long chapterId, long userId, String cert) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_VIP_CHAPTER_RESULT);
        builder.appendQueryParameter(KEY_BOOK_ID, String.valueOf(bookId));
        builder.appendQueryParameter(KEY_CHAPTER_ID, String.valueOf(chapterId));
        builder.appendQueryParameter(KEY_INFO_USER_ID, String.valueOf(userId));
        builder.appendQueryParameter(KEY_CERT, cert);
        return builder.toString();
    }

    public String builderUriForGetUserInfo(int userId, String certString) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_USERINFO_RESULT);
        builder.appendQueryParameter(KEY_INFO_USER_ID, String.valueOf(userId));
        builder.appendQueryParameter(KEY_CERT, certString);
        return builder.toString();
    }

    public String builderUriForGetLoginResult(String userName, String password) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_LOGIN_RESULT);
        builder.appendQueryParameter(KEY_USER_NAME, userName);
        builder.appendQueryParameter(KEY_PASSWORD, password);
        return builder.toString();
    }

    public String builderUriForGetBackPasswordResult(String userName, String email) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_BACK_PASSWORD);
        builder.appendQueryParameter(KEY_USER_NAME, userName);
        builder.appendQueryParameter(KEY_EMAIL, email);
        return builder.toString();
    }

    public String builderUriForGetAliPayOrder() {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_USER_ALIPAY_PAYMENT);
        return builder.toString();
    }
    public String builderUriForGetAliWapPayOrder() {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_USER_ALIPAY_WAP_PAYMENT);
        return builder.toString();
    }
    /*微信订单url*/
    public String builderUriForGetWXPayOrder() {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_USER_WXPAY_PAYMENT);
        return builder.toString();
    }


    public String builderUriForSendSMSPayment() {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_USER_SMS_PAYMENT);
        return builder.toString();
    }

    public String builderUriForSendPrePaidCardPayment() {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_USER_PREPAID_CARD);
        return builder.toString();
    }

    public String builderUriForGetVIPChapterOrder(long bookId, String chapterList, int userId, String cert) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_VIP_CHAPTER_ORDER);
        builder.appendQueryParameter(KEY_ORDER_BOOK_ID, String.valueOf(bookId));
        builder.appendQueryParameter(KEY_CHAPTER_LIST, chapterList);
        builder.appendQueryParameter(KEY_INFO_USER_ID, String.valueOf(userId));
        builder.appendQueryParameter(KEY_CERT, cert);
        return builder.toString();
    }

    public String buliderUriForGetBuyVIPChapterList(long bookId, int userId, String cert) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_BUY_VIP_CHAPTER_LIST);
        builder.appendQueryParameter(KEY_BOOK_ID, String.valueOf(bookId));
        builder.appendQueryParameter(KEY_INFO_USER_ID, String.valueOf(userId));
        builder.appendQueryParameter(KEY_CERT, cert);
        return builder.toString();
    }

    public String builderUriForGetRegisterSMSResult(String phoneNumber) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_REGISTER_SMS_RESULT);
        builder.appendQueryParameter(KEY_PHONE_NUMBER, phoneNumber);
        return builder.toString();
    }

    public String builderUriForGetRegisterResult(String phoneNumber, String password) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_REGISTER_RESULT);
        builder.appendQueryParameter(KEY_PHONE_NUMBER, phoneNumber);
        builder.appendQueryParameter(KEY_PASSWORD, password);
        return builder.toString();
    }

    public String builderUriForModifyPassword(int userId, String oldPassword, String newPassword, String cert) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_MODIFY_PASSWORD);
        builder.appendQueryParameter(KEY_RECORD_USER_ID, String.valueOf(userId));
        builder.appendQueryParameter(KEY_OLD_PASSWORD, oldPassword);
        builder.appendQueryParameter(KEY_NEW_PASSWORD, newPassword);
        builder.appendQueryParameter(KEY_CERT, cert);
        return builder.toString();
    }

    public String builderUriForGetPaymentRecord(int userId, String cert) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_PAYMENT_RECORD);
        builder.appendQueryParameter(KEY_RECORD_USER_ID, String.valueOf(userId));
        builder.appendQueryParameter(KEY_CERT, cert);
        return builder.toString();
    }

    public String builderUriForGetConsumeRecord(int userId, int page, String cert) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_CONSUME_RECORD);
        builder.appendQueryParameter(KEY_RECORD_USER_ID, String.valueOf(userId));
        builder.appendQueryParameter(KEY_PAGE, String.valueOf(page));
        builder.appendQueryParameter(KEY_CERT, cert);
        return builder.toString();
    }

    public String builderUriForUserRegister(String userName, String password, String email) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_USER_REGISTER);
        builder.appendQueryParameter(KEY_USER_NAME, userName);
        builder.appendQueryParameter(KEY_PASSWORD, password);
        builder.appendQueryParameter(KEY_EMAIL, email);
        return builder.toString();
    }

    public String builderUriForGetCommentBySince(long bookId, long sicne, int page) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_COMMENT_LIST);
        builder.appendQueryParameter(KEY_BOOK_ID, String.valueOf(bookId));
        builder.appendQueryParameter(KEY_SINCE_ID, String.valueOf(sicne));
        builder.appendQueryParameter(KEY_PAGE, String.valueOf(page));
        return builder.toString();
    }

    public String builderUriForGetCommentByMax(long bookId, long max, int page) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_COMMENT_LIST);
        builder.appendQueryParameter(KEY_BOOK_ID, String.valueOf(bookId));
        builder.appendQueryParameter(KEY_MAX_ID, String.valueOf(max));
        builder.appendQueryParameter(KEY_PAGE, String.valueOf(page));
        return builder.toString();
    }

    public String builderUriForGetLatestComment(long bookId, int commentsTop) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_LATEST_COMMENT);
        builder.appendQueryParameter(KEY_BOOK_ID, String.valueOf(bookId));
        builder.appendQueryParameter(KEY_COMMENTS_TOP, String.valueOf(commentsTop));
        return builder.toString();
    }

    public String builderUriForSendComment() {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_SEND_COMMENT);
        return builder.toString();
    }

    public String builderUriForGetLatestChapter(long bookId, int pageSize) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_LATEST_CHAPTER);
        builder.appendQueryParameter(KEY_ORDER_BOOK_ID, String.valueOf(bookId));
        builder.appendQueryParameter(KEY_PAGE_SIZE, String.valueOf(pageSize));
        return builder.toString();
    }

    public String builderUriForGetSearchSuggestion(String key) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_SEARCH_SUGGESTION);
        builder.appendQueryParameter(KEY_KEY, key);
        return builder.toString();
    }

    public String builderUriForGetHotReadBooks(int pageNum) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_HOT_READ_BOOKS);
        builder.appendQueryParameter(KEY_PAGE, String.valueOf(pageNum));
        return builder.toString();
    }

    public String builderUriForGetHotRecommenBooks(String type) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_HOT_RECOMMEND_BOOKS);
        builder.appendQueryParameter(KEY_TYPE, type);
        return builder.toString();
    }

    public String builderUriForGetNewFinishBook(int pageNum) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_NEW_FINISH_BOOKS);
        builder.appendQueryParameter(KEY_PAGE, String.valueOf(pageNum));
        return builder.toString();
    }

    public String builderUriForGetNewPotentialBooks(int pageNum) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_NEW_POTENTIAL_BOOKS);
        builder.appendQueryParameter(KEY_PAGE, String.valueOf(pageNum));
        return builder.toString();
    }

    public String builderUriForGetRankList() {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_RANK_LIST);
        return builder.toString();
    }

    public String builderUriForGetRankBookList(int rankId, int nowpage) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendQueryParameter(KEY_ACTION, ACTION_GET_RANK_BOOK_LIST);
        builder.appendQueryParameter(KEY_RANK_ID, String.valueOf(rankId));
        builder.appendQueryParameter(KEY_PAGE, String.valueOf(nowpage));
        return builder.toString();
    }
}
