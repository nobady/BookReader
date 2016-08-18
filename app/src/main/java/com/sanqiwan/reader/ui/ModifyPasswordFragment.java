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
 * Date: 11/12/13
 * Time: 11:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class ModifyPasswordFragment extends BaseFragment implements View.OnClickListener {
    private UserManager mUserManager;
    private EditText mOldPasswordEditText, mNewPasswordEditText;
    private Button mSubmitButton;
    private TextView mSuccessView;
    private View mFragmentView;
    private Context mContext;

    public static ModifyPasswordFragment newFragment() {
        return new ModifyPasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        mUserManager = UserManager.getInstance();
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.modify_password, container, false);

            mOldPasswordEditText = (EditText) mFragmentView.findViewById(R.id.et_oldpassword);
            mNewPasswordEditText = (EditText) mFragmentView.findViewById(R.id.et_newpassword);
            mSubmitButton = (Button) mFragmentView.findViewById(R.id.button_modify_password);
            mSubmitButton.setOnClickListener(this);
            ImageView returnImageView = (ImageView) mFragmentView.findViewById(R.id.btn_return);
            returnImageView.setOnClickListener(this);
            TextView topTitle = (TextView) mFragmentView.findViewById(R.id.top_title);
            topTitle.setText(R.string.modify_password_title);
            mSuccessView = (TextView) mFragmentView.findViewById(R.id.success_tips);
            mNewPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    doModifyPassword();
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
            case R.id.button_modify_password:
                doModifyPassword();
                break;
            case R.id.btn_return:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    private void doModifyPassword() {
        final String oldPassword = mOldPasswordEditText.getText().toString();
        final String newPassword = mNewPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(oldPassword)) {
            Toast.makeText(mContext, R.string.need_old_password, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(mContext, R.string.should_not_null, Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncTask<Void, Void, AccountResult> task = new AsyncTask<Void, Void, AccountResult>() {
            private LoadingProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                mProgressDialog = new LoadingProgressDialog(mContext);
                mProgressDialog.show();
            }

            @Override
            protected void onPostExecute(AccountResult accountResult) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                if (accountResult.getCode() == UserManager.SUCCESS_CODE) {
                    mSubmitButton.setVisibility(View.GONE);
                    mOldPasswordEditText.setVisibility(View.GONE);
                    mNewPasswordEditText.setVisibility(View.GONE);
                    mSuccessView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(mContext, accountResult.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            protected AccountResult doInBackground(Void... params) {
                return mUserManager.modifyPassword(oldPassword, newPassword);
            }
        };
        AsyncTaskUtil.execute(task);
    }
}