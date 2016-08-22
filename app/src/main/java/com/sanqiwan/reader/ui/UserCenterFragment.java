package com.sanqiwan.reader.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.SettingListAdapter;
import com.sanqiwan.reader.apps.AppListActivity;
import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.AccountResult;
import com.sanqiwan.reader.model.UserInfo;
import com.sanqiwan.reader.threelogin.ThreeLogin;
import com.sanqiwan.reader.threelogin.WXLogin;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import com.sanqiwan.reader.util.UIUtil;
import com.sanqiwan.reader.view.LoadingProgressDialog;
import com.sanqiwan.reader.webimageview.IImageHandler;
import com.sanqiwan.reader.webimageview.WebImageView;

import java.util.Observable;
import java.util.Observer;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/8/13
 * Time: 9:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserCenterFragment extends BaseFragment implements View.OnClickListener, Observer {

    public static final int ACTION_FOR_LOGIN = 100;
    public static final int ACTION_IN_USER_CENTER = 0;
    public static final String KEY_FOR_LOGIN = "key_for_login";

    private UserManager mUserManager;
    private EditText mLoginUserName;
    private EditText mLoginPassword;
    private Button mLoginSubmit;
    private TextView mUserNameTextView, mExitButton, mIntegeryTextView, mMoneyTextView,
            mPhoneTextView, mEmailTextView, mRegisterTextView, mForgotPasswordTextView,
            mModifyPasswordTextView;
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

    public static UserCenterFragment newFragment() {
        return new UserCenterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        if (mContainer == null) {
            mContext = getContext();
            mContainer = new LinearLayout(mContext);
            mContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mUserManager = UserManager.getInstance();
            mUserManager.addObserver(this);

            resetUi();

        }
        if (mContainer.getParent() != null) {
            ((ViewGroup) mContainer.getParent()).removeAllViews();
        }
        return mContainer;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUserManager.deleteObserver(this);
    }

    private void resetUi() {
        if (mUserManager.isLogin()) {   //如果已经登录，就显示个人信息页面
            startUserCenter();
        } else {
            startLogin();  //如果 未登录，显示登录页面
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                doLogin();
                break;
            case R.id.tv_exit:
                doLogout();
                break;
            case R.id.tv_register:
                startUserRegister();
                break;
            case R.id.tv_forget:
                startFindBackPassword();
                break;
            case R.id.tv_modify_password:
                startModifyPassword();
                break;
            /*第三方登录相关*/
            case R.id.qq_login_iv:
                break;
            case R.id.wechat_login_iv:
                ThreeLogin threeLogin = new WXLogin();
                threeLogin.startLogin(mContext);
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


    private void startModifyPassword() {
        MainActivity.openSubFragment(ModifyPasswordFragment.newFragment());
    }

    private void startFindBackPassword() {
        MainActivity.openSubFragment(GetPasswordBackFragment.newFragment());
    }

    private void startLogin() {
        mView = mInflater.inflate(R.layout.login, null);
        mLoginSubmit = (Button) mView.findViewById(R.id.button_login);
        mLoginUserName = (EditText) mView.findViewById(R.id.et_name);
        mLoginPassword = (EditText) mView.findViewById(R.id.et_password);
        mLoginSubmit.setOnClickListener(this);

        //第三方登录相关
        qqLoginIv = (ImageView) mView.findViewById(R.id.qq_login_iv);
        wechatLoginIv = (ImageView) mView.findViewById(R.id.wechat_login_iv);
        weiboLoginIv = (ImageView) mView.findViewById(R.id.weibo_login_iv);
        qqLoginIv.setOnClickListener(this);
        wechatLoginIv.setOnClickListener(this);
        weiboLoginIv.setOnClickListener(this);
        //手机邮箱注册相关
        phoneRegTv = (TextView) mView.findViewById(R.id.phone_register_tv);
        mailRegTv = (TextView) mView.findViewById(R.id.mail_register_tv);
        phoneRegTv.setOnClickListener(this);
        mailRegTv.setOnClickListener(this);

        mRegisterTextView = (TextView) mView.findViewById(R.id.tv_register);
        mRegisterTextView.setOnClickListener(this);
        mForgotPasswordTextView = (TextView) mView.findViewById(R.id.tv_forget);
        mForgotPasswordTextView.setOnClickListener(this);
        mLoginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                doLogin();
                return true;
            }
        });
        mContainer.removeAllViews();
        mContainer.addView(mView);
    }

    private void startPhoneRegister() {
        MainActivity.openSubFragment(UserRegisterFragment.newFragment(mActionCode));
    }

    private void startUserRegister() {
        MainActivity.openSubFragment(UserRegisterFragment.newFragment(mActionCode));
    }

    private void startUserCenter() {
        mView = mInflater.inflate(R.layout.user_center, null);
        mExitButton = (TextView) mView.findViewById(R.id.tv_exit);
        mExitButton.setOnClickListener(this);
        mUserNameTextView = (TextView) mView.findViewById(R.id.tv_user_name);
        mMoneyTextView = (TextView) mView.findViewById(R.id.tv_money);
        mIntegeryTextView = (TextView) mView.findViewById(R.id.tv_integry);
        //mPhoneTextView = (TextView) mView.findViewById(R.id.tv_phone);
        mEmailTextView = (TextView) mView.findViewById(R.id.tv_email);
        mAvatarImageView = (WebImageView) mView.findViewById(R.id.iv_avatar);
        mAvatarImageView.setImageHandler(mAvatarImageHandler);
        mGridView = (GridView) mView.findViewById(R.id.gv_user);
        mGridView.setAdapter(new GridAdapter());
        mModifyPasswordTextView = (TextView) mView.findViewById(R.id.tv_modify_password);
        mModifyPasswordTextView.setOnClickListener(this);
        RelativeLayout userCenterContent = (RelativeLayout) mView.findViewById(R.id.user_center_content);
        userCenterContent.bringToFront();
        refreshData();
        mContainer.removeAllViews();
        mContainer.addView(mView);
    }

    private void refreshData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mUserInfo = mUserManager.getUserInfo(true);
                if (mUserInfo == null) {
                    return;
                }
                mView.post(new Runnable() {
                    @Override
                    public void run() {
                        mUserNameTextView.setText(mUserInfo.getUserName());
                        mMoneyTextView.setText(String.valueOf(mUserInfo.getUserMoney()));
                        mIntegeryTextView.setText(String.valueOf(mUserInfo.getUserPoint()));
                        mEmailTextView.setText(mUserInfo.getEmail());
//                        if (!mUserInfo.getPhoneNumber().equals("0")) {
//                            mPhoneTextView.setText(mUserInfo.getPhoneNumber());
//                        } else {
//                            mPhoneTextView.setText(R.string.not_binder_phone);
//                        }
                        if (TextUtils.isEmpty(mUserInfo.getAvatar())) {
                            return;
                        }
                        mAvatarImageView.setImageUrl(mUserInfo.getAvatar());
                    }
                });
            }
        }).start();
    }

    private IImageHandler mAvatarImageHandler = new IImageHandler() {
        @Override
        public void onHandleImage(WebImageView imageView, Bitmap bitmap) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar, options);
            int width = options.outWidth;
            mAvatarImageView.setImageBitmap(drawCircleImage(bitmap, width));
        }
    };

    private void doLogout() {
        mUserManager.logout();
    }

    private void doLogin() {
        final String userName = mLoginUserName.getText().toString();
        final String password = mLoginPassword.getText().toString();
        if (userName.equals("")) {
            Toast.makeText(mContext, R.string.should_enter_username, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.equals("")) {
            Toast.makeText(mContext, R.string.should_enter_username, Toast.LENGTH_SHORT).show();
            return;
        }
        AsyncTask<Void, Void, AccountResult> loginTask = new AsyncTask<Void, Void, AccountResult>() {

            @Override
            protected void onPreExecute() {
                mLoadingDialog = new LoadingProgressDialog(mContext);
                mLoadingDialog.show();
            }

            @Override
            protected void onPostExecute(AccountResult errorCode) {
                mLoadingDialog.dismiss();
                if (errorCode.getCode() <= 0) {
                    String errorMsg = errorCode.getMessage();
                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (((MainActivity) getActivity()).isForLogin()) {
                    getActivity().setResult(Activity.RESULT_OK);
                    ReaderActivity.startActivityFromLogin(mContext);
                }
            }

            @Override
            protected AccountResult doInBackground(Void... params) {
                return mUserManager.login(userName, password);
            }


        };
        AsyncTaskUtil.execute(loginTask);

    }

    private Bitmap drawCircleImage(Bitmap origin, int width) {
        Bitmap myAvatar = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(myAvatar);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        int radwidth = width / 2 - 3;
        canvas.drawCircle(width / 2, width / 2, radwidth, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Matrix matrix = new Matrix();
        matrix.setScale((float) width / (float) origin.getWidth(), (float) width / (float) origin.getHeight());
        canvas.drawBitmap(origin, matrix, paint);
        canvas.save();
        return myAvatar;
    }

    @Override
    public void update(Observable observable, Object data) {
        UIUtil.getHandler().post(new Runnable() {
            @Override
            public void run() {
                resetUi();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (toPay) {
            refreshData();
            toPay = false;
        }
    }

    private boolean toPay;

    public class GridAdapter extends BaseAdapter implements View.OnClickListener {

        private final static int PAYMENT_RECORD = 2;
        private final static int CONSUME_RECORD = 1;
        private final static int CONSUME_PAYMENT = 0;
        private final static int FEED_BACK = 5;
        private final static int PROMTION = 3;
        private final static int SETTINGS = 4;

        private String[] mItemStrings;
        private int[] mIcons = {R.drawable.gv_chongzhi, R.drawable.gv_xiaofei, R.drawable.gv_chongzhijilu,
                R.drawable.gv_shuoming, R.drawable.gv_shezhi, R.drawable.gv_fankui};

        public GridAdapter() {
            mItemStrings = getResources().getStringArray(R.array.user_center_string);
        }

        @Override
        public int getCount() {
            return mItemStrings.length;
        }

        @Override
        public Object getItem(int position) {
            return mItemStrings[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.gv_item, null);
            }
            TextView textView = (TextView) convertView;
            textView.setText(mItemStrings[position]);
            textView.setCompoundDrawablesWithIntrinsicBounds(mIcons[position], 0, 0, 0);
            textView.setTag(position);
            textView.setOnClickListener(this);
            return textView;
        }

        @Override
        public void onClick(View v) {
            switch ((Integer) v.getTag()) {
                case PAYMENT_RECORD:   //充值记录
                    MainActivity.openSubFragment(PaymentRecordFragment.newFragment());
                    break;
                case CONSUME_RECORD:  //消费记录
                    MainActivity.openSubFragment(ConsumeRecordFragment.newFragment());
                    break;
                case CONSUME_PAYMENT:
                    toPay = true;    //蔷薇充值
                    MainActivity.openSubFragment(PaymentFragment.newFragment());
                    break;
                case FEED_BACK:
                    openFeedbackActivity(mContext);
                    break;
                case PROMTION:
                    AppListActivity.start(mContext);
                    break;
                case SETTINGS:
                    MainActivity.openSubFragment(SettingsFragment.newFragment());
                    break;
            }

        }
    }

    public void setActionCode(int actionCode) {
        mActionCode = actionCode;
    }

}
