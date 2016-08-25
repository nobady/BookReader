package com.sanqiwan.reader.usercenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanqiwan.reader.R;
import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.UserInfo;
import com.sanqiwan.reader.ui.MainActivity;
import com.sanqiwan.reader.ui.ReaderActivity;
import com.sanqiwan.reader.view.LoadingProgressDialog;
import com.sanqiwan.reader.webimageview.WebImageView;

import mvp.BaseMvpFragment;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/25 15:09.
 */

public class UserCenterFragment extends BaseMvpFragment<UserCenterView,UserCenterPresenter> implements
        UserCenterView,View.OnClickListener{

    public static final int ACTION_FOR_LOGIN = 100;
    public static final int ACTION_IN_USER_CENTER = 0;
    public static final String KEY_FOR_LOGIN = "key_for_login";

    private UserManager mUserManager;
    private EditText mLoginUserName;
    private EditText mLoginPassword;
    private Button mLoginSubmit;
    private TextView mUserNameTextView, mExitButton, mIntegeryTextView, mMoneyTextView, mPhoneTextView, mEmailTextView, mRegisterTextView, mForgotPasswordTextView, mModifyPasswordTextView;
    private WebImageView mAvatarImageView;
    private UserInfo mUserInfo;
    //    private ListView mListView;
    private GridView mGridView;
    private LoadingProgressDialog mLoadingDialog;
    private View mView;
    private Context mContext;
    private LinearLayout mContainer;
    private LayoutInflater mInflater;
    private int mActionCode;
    private ImageView qqLoginIv;
    private ImageView wechatLoginIv;
    private ImageView weiboLoginIv;
    private TextView phoneRegTv;
    private TextView mailRegTv;



    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        if (mContainer == null) {
            mContext = getContext ();
            mContainer = new LinearLayout (mContext);
            mContainer.setLayoutParams (new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mUserManager = UserManager.getInstance ();
//            mUserManager.addObserver (this);

            resetUi ();

        }
        if (mContainer.getParent () != null) {
            ((ViewGroup) mContainer.getParent ()).removeAllViews ();
        }
        return mContainer;
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView ();
//        mUserManager.deleteObserver (this);
    }

    private void resetUi () {
        if (mUserManager.isLogin ()) {   //如果已经登录，就显示个人信息页面
//            startUserCenter ();
        } else {
            startLogin ();  //如果 未登录，显示登录页面
        }
    }

    /**
     * 未登录页面
     */
    private void startLogin () {
        mView = mInflater.inflate (R.layout.login, null);
        mLoginSubmit = (Button) mView.findViewById (R.id.button_login);
        mLoginUserName = (EditText) mView.findViewById (R.id.et_name);
        mLoginPassword = (EditText) mView.findViewById (R.id.et_password);
        mLoginSubmit.setOnClickListener (this);

        //第三方登录相关
        qqLoginIv = (ImageView) mView.findViewById (R.id.qq_login_iv);
        wechatLoginIv = (ImageView) mView.findViewById (R.id.wechat_login_iv);
        weiboLoginIv = (ImageView) mView.findViewById (R.id.weibo_login_iv);
        qqLoginIv.setOnClickListener (this);
        wechatLoginIv.setOnClickListener (this);
        weiboLoginIv.setOnClickListener (this);
        //手机邮箱注册相关
        phoneRegTv = (TextView) mView.findViewById (R.id.phone_register_tv);
        mailRegTv = (TextView) mView.findViewById (R.id.mail_register_tv);
        phoneRegTv.setOnClickListener (this);
        mailRegTv.setOnClickListener (this);

        mRegisterTextView = (TextView) mView.findViewById (R.id.tv_register);
        mRegisterTextView.setOnClickListener (this);
        mForgotPasswordTextView = (TextView) mView.findViewById (R.id.tv_forget);
        mForgotPasswordTextView.setOnClickListener (this);
        mLoginPassword.setOnEditorActionListener (new TextView.OnEditorActionListener () {
            @Override
            public boolean onEditorAction (TextView v, int actionId, KeyEvent event) {
//                doLogin ();
                return true;
            }
        });
        mContainer.removeAllViews ();
        mContainer.addView (mView);
    }

    @NonNull
    @Override
    public UserCenterPresenter createPresenter () {
        return new UserCenterPresenter ();
    }

    @Override
    public void showLoadDialog (boolean isCancelable) {

    }

    @Override
    public void showDialog (String title, String msg, boolean isCancelable) {

    }

    @Override
    public void hideDialog () {

    }

    @Override
    public void showToastMsg (String msg) {

    }

    @Override
    public void onClick (View v) {
        switch (v.getId ()){
            case R.id.button_login:
                //普通登录
                doLogin ();
                break;
            /*第三方登录相关*/
            case R.id.qq_login_iv:
                break;
            case R.id.wechat_login_iv:
                break;
            case R.id.weibo_login_iv:
                break;
            /*手机和邮箱注册相关*/
            case R.id.phone_register_tv:
                break;
            case R.id.mail_register_tv:
                break;
        }
    }

    private void doLogin () {
        final String userName = mLoginUserName.getText ().toString ();
        final String password = mLoginPassword.getText ().toString ();
        if (userName.equals ("")) {
            Toast.makeText (mContext, R.string.should_enter_username, Toast.LENGTH_SHORT).show ();
            return;
        }
        if (password.equals ("")) {
            Toast.makeText (mContext, R.string.should_enter_username, Toast.LENGTH_SHORT).show ();
            return;
        }
        presenter.doLogin(userName,password);
    }

    @Override
    public void showLoginSuccess () {
        if (((MainActivity) getActivity ()).isForLogin ()) {
            getActivity ().setResult (Activity.RESULT_OK);
            ReaderActivity.startActivityFromLogin (mContext);
        }
    }
}
