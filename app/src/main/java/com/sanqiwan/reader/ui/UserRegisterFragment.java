package com.sanqiwan.reader.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.AccountResult;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import com.sanqiwan.reader.view.LoadingProgressDialog;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/14/13
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserRegisterFragment extends BaseFragment implements View.OnClickListener {

    private static final String ACTION_CODE = "action_code";
    private EditText mUserNameView, mUserPasswordView, mEmailView;
    private Button mRegister;
    private ImageView mReturnImageView;
    private TextView mTopTitle;
    private String mUserName, mPassword, mEmail;
    private final static int MAX_PASSWORD_LENGTH = 16;
    private final static int MIN_PASSWORD_LENGTH = 6;

    private View mFragmentView;
    private Context mContext;
    private int mActionCode;

    public static UserRegisterFragment newFragment(int actionCode) {
        UserRegisterFragment fragment = new UserRegisterFragment();
        Bundle args = new Bundle();
        args.putInt(ACTION_CODE, actionCode);
        fragment.setArguments(args);
        return fragment;
    }

    private void takeArguments() {
        Bundle args = getArguments();
        if (args != null) {
            mActionCode = args.getInt(ACTION_CODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        takeArguments();
        mContext = getContext();
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.user_register, container, false);

            mUserNameView = (EditText) mFragmentView.findViewById(R.id.et_username);
            mUserPasswordView = (EditText) mFragmentView.findViewById(R.id.et_password);
            mEmailView = (EditText) mFragmentView.findViewById(R.id.et_email);
            mRegister = (Button) mFragmentView.findViewById(R.id.register);
            mRegister.setOnClickListener(this);
            mTopTitle = (TextView) mFragmentView.findViewById(R.id.top_title);
            mTopTitle.setText(R.string.register_text);
            mReturnImageView = (ImageView) mFragmentView.findViewById(R.id.btn_return);
            mReturnImageView.setOnClickListener(this);
            mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    doRegister();
                    return true;
                }
            });
        }
        if (mFragmentView.getParent() != null) {
            ((ViewGroup) mFragmentView.getParent()).removeAllViews();
        }
        return mFragmentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                doRegister();
                break;
            case R.id.btn_return:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }

    }


    private void doRegister() {
        mUserName = mUserNameView.getText().toString();
        mPassword = mUserPasswordView.getText().toString();
        mEmail = mEmailView.getText().toString();
        if (!checkUserInput()) {
            return;
        }

        AsyncTask<Void, Void, AccountResult> registerTask = new AsyncTask<Void, Void, AccountResult>() {
            private LoadingProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                mProgressDialog = new LoadingProgressDialog(mContext);
                mProgressDialog.show();
            }

            @Override
            protected void onPostExecute(AccountResult result) {
                mProgressDialog.dismiss();
                if (result.getCode() < UserManager.SUCCESS_CODE) {
                    String errorMsg = result.getMessage();
                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mActionCode == UserCenterFragment.ACTION_FOR_LOGIN) {
                    ReaderActivity.startActivityFromLogin(mContext);
                } else {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            protected AccountResult doInBackground(Void... params) {
                return UserManager.getInstance().register(mUserName, mPassword, mEmail);
            }


        };
        AsyncTaskUtil.execute(registerTask);

    }

    private boolean checkUserInput() {
        if (TextUtils.isEmpty(mUserName)) {
            showToast(R.string.should_enter_username);
            return false;
        }
        if (TextUtils.isEmpty(mPassword)) {
            showToast(R.string.should_enter_password);
            return false;
        }
        if (TextUtils.isEmpty(mEmail)) {
            showToast(R.string.should_enter_email);
            return false;
        }
        if (mPassword.length() > MAX_PASSWORD_LENGTH || mPassword.length() < MIN_PASSWORD_LENGTH) {
            showToast(R.string.register_password_length_tip);
            return false;
        }
        return true;
    }

    private void showToast(int stringId) {
        Toast.makeText(mContext, stringId, Toast.LENGTH_SHORT).show();
    }

}