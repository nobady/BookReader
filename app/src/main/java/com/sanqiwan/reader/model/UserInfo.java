package com.sanqiwan.reader.model;

/**
 * User: sam
 * Date: 11/5/13
 * Time: 2:30 PM
 * User Object：save user info
 */
public class UserInfo {
    private int mUid;
    private String mUserName;
    private String mUserPassWord;
    private String mAvatar;
    private String mLevel;
    private float mUserPoint;
    private int mUserMoney;
    private String mPhoneNumber;
    private String mEmail;

    public UserInfo() {

    }

    public UserInfo(int uid) {
        mUid = uid;
    }

    public int getUid() {
        return mUid;
    }

    public void setUid(int uid) {
        mUid = uid;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getUserPassWord() {
        return mUserPassWord;
    }

    public void setUserPassWord(String userPassWord) {
        mUserPassWord = userPassWord;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public float getUserPoint() {
        return mUserPoint;
    }

    public String getLevel() {
        return mLevel;
    }

    public void setLevel(String level) {
        mLevel = level;
    }

    public void setUserPoint(float userPoint) {
        mUserPoint = userPoint;
    }

    public int getUserMoney() {
        return mUserMoney;
    }

    public void setUserMoney(int userMoney) {
        mUserMoney = userMoney;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }
}
