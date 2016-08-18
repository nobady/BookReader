package com.sanqiwan.reader.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.*;
import android.text.method.LinkMovementMethod;
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
 * Date: 11/11/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetPasswordBackFragment extends BaseFragment implements View.OnClickListener {
    private EditText mUserNameEditText, mEmailEditText;
    private Button mSubmitButton;
    private String mUserName, mEmail;
    private TextView mTextView, mSuccessTipView;
    private String mPhoneTipsString = "温馨提示:<br/>手机号码注册的会员如遗忘密码，请使用此手机号码，拨打蔷" +
            "薇书院客服电话咨询。客服服务时间：周一～周五 9：00-18：00" +
            "<br/><h3><a href=\"tel:02085559465\">客服电话：020-85559465</a></h3>";


    private Context mContext;
    private View mFragmentView;
    private LayoutInflater mInflater;

    public static GetPasswordBackFragment newFragment() {
        return new GetPasswordBackFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        mInflater = inflater;
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.retrieve_password, container, false);

            mUserNameEditText = (EditText) mFragmentView.findViewById(R.id.et_username);
            mEmailEditText = (EditText) mFragmentView.findViewById(R.id.et_email);
            mSubmitButton = (Button) mFragmentView.findViewById(R.id.button_retrieve_password);
            mSubmitButton.setOnClickListener(this);
            ImageView returnImageView = (ImageView) mFragmentView.findViewById(R.id.btn_return);
            returnImageView.setOnClickListener(this);
            TextView topTitle = (TextView) mFragmentView.findViewById(R.id.top_title);
            topTitle.setText(R.string.find_password_title);
            mTextView = (TextView) mFragmentView.findViewById(R.id.phone_tips);
            mTextView.setText(Html.fromHtml(mPhoneTipsString));
            mTextView.setMovementMethod(LinkMovementMethod.getInstance());
            mSuccessTipView = (TextView) mFragmentView.findViewById(R.id.success_tips);
        }
        if (mFragmentView.getParent() != null) {
            ((ViewGroup) mFragmentView.getParent()).removeAllViews();
        }
        return mFragmentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_retrieve_password:
                doFindBackPassword();
                break;
            case R.id.btn_return:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }

    }

    private void doFindBackPassword() {
        mUserName = mUserNameEditText.getText().toString();
        mEmail = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(mUserName)) {
            Toast.makeText(mContext, R.string.should_enter_username, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mEmail)) {
            Toast.makeText(mContext, R.string.find_email_hint, Toast.LENGTH_SHORT).show();
            return;
        }
        AsyncTask<Void, Void, AccountResult> task = new AsyncTask<Void, Void, AccountResult>() {
            private LoadingProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                mProgressDialog = new LoadingProgressDialog(getActivity());
                mProgressDialog.show();
            }

            @Override
            protected void onPostExecute(AccountResult accountResult) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                if (accountResult.getCode() == UserManager.SUCCESS_CODE) {
                    mSuccessTipView.setVisibility(View.VISIBLE);
                    mUserNameEditText.setVisibility(View.GONE);
                    mEmailEditText.setVisibility(View.GONE);
                    mSubmitButton.setVisibility(View.GONE);
                    mTextView.setVisibility(View.GONE);
                } else {
                    Toast.makeText(mContext, accountResult.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            protected AccountResult doInBackground(Void... params) {
                return UserManager.getInstance().findPassword(mUserName, mEmail);
            }
        };
        AsyncTaskUtil.execute(task);

    }
}