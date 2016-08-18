package com.sanqiwan.reader.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
public class PhoneRegisterFragment extends BaseFragment implements View.OnClickListener {

    private static final String ACTION_CODE = "action_code";
    private EditText mRegisterPhone, mRegisterPhoneConfirm;
    private EditText mLoginPassword;
    private Button mRegister, mRegisterConfirm;
    private ImageView mReturnImageView;
    private TextView mTopTitle;

    private String mPhoneNumber;

    private View mFragmentView;
    private Context mContext;
    private int mActionCode;

    public static PhoneRegisterFragment newFragment(int actionCode) {

        PhoneRegisterFragment fragment = new PhoneRegisterFragment();
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
            mFragmentView = inflater.inflate(R.layout.phone_register, container, false);

            mRegisterPhone = (EditText) mFragmentView.findViewById(R.id.et_phonenum);
            mRegister = (Button) mFragmentView.findViewById(R.id.button_phone_reg);
            mRegister.setOnClickListener(this);
            mTopTitle = (TextView) mFragmentView.findViewById(R.id.top_title);
            mTopTitle.setText(R.string.phone_register_title);
            mReturnImageView = (ImageView) mFragmentView.findViewById(R.id.btn_return);
            mReturnImageView.setOnClickListener(this);
        }
        if (mFragmentView.getParent() != null) {
            ((ViewGroup) mFragmentView.getParent()).removeAllViews();
        }
        return mFragmentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_phone_reg:
                doPhoneRegister();
                break;
            case R.id.button_phone_reg_confirm:
                doPhoneRegisterConfirm();
                break;
            case R.id.btn_return:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }

    }

    private void doPhoneRegister() {
        mPhoneNumber = mRegisterPhone.getText().toString();
        if (mPhoneNumber.equals("")) {
            Toast.makeText(mContext, R.string.should_enter_phone, Toast.LENGTH_SHORT).show();
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
            protected void onPostExecute(AccountResult errorCode) {
                mProgressDialog.dismiss();
                if (errorCode.getCode() <= 0) {
                    String errorMsg = errorCode.getMessage();
                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                    return;
                }
                startPhoneRegisterConfirm();
            }

            @Override
            protected AccountResult doInBackground(Void... params) {
                return UserManager.getInstance().register(mPhoneNumber);
            }


        };
        AsyncTaskUtil.execute(registerTask);

    }

    private void doPhoneRegisterConfirm() {
        final String password = mLoginPassword.getText().toString();
        if (password.equals("")) {
            Toast.makeText(mContext, R.string.should_enter_password, Toast.LENGTH_SHORT).show();
            return;
        }
        AsyncTask<Void, Void, AccountResult> registerConfirmTask = new AsyncTask<Void, Void, AccountResult>() {
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
                AccountResult result = UserManager.getInstance().registerConfirm(mPhoneNumber, password);
                if (result.getCode() < UserManager.SUCCESS_CODE) {
                    return result;
                }
                return UserManager.getInstance().login(mPhoneNumber, password);
            }


        };
        AsyncTaskUtil.execute(registerConfirmTask);

    }

    private void startPhoneRegisterConfirm() {
        getActivity().setContentView(R.layout.register);
        mRegisterPhoneConfirm = (EditText) mFragmentView.findViewById(R.id.et_phone);
        mLoginPassword = (EditText) mFragmentView.findViewById(R.id.et_password);
        mRegisterConfirm = (Button) mFragmentView.findViewById(R.id.button_phone_reg_confirm);
        mRegisterConfirm.setOnClickListener(this);
        mRegisterPhoneConfirm.setText(mPhoneNumber);

    }


}