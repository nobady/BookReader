package com.sanqiwan.reader.model;

import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.util.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-23
 * Time: 下午10:28
 * To change this template use File | Settings | File Templates.
 */
public class CommentInfo {

    private long mId;
    private long mBookId;
    private String mTitle;
    private String mContent;
    private String mUserName;
    private int mUserId;
    private String mTime;
    private String mUserImageUrl;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public long getBookId() {
        return mBookId;
    }

    public void setBookId(long bookId) {
        this.mBookId = bookId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String createTime) {
        this.mTime = createTime;
    }

    public String getUserImageUrl() {
        return mUserImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.mUserImageUrl = userImageUrl;
    }

    public static CommentInfo getCommentInfo(long bookId, String content) {
        CommentInfo info = new CommentInfo();
        info.setUserId(UserManager.getInstance().getUserId());
        info.setBookId(bookId);
        info.setContent(content);
        info.setTime(SimpleDateFormat.formatUnixTimestamp("yyyy-MM-dd hh:mm:ss", System.currentTimeMillis() / 1000).toString());
        return info;
    }
}
