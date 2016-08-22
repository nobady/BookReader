package com.sanqiwan.reader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.sanqiwan.reader.data.HistoryManager;
import com.sanqiwan.reader.data.XBookManager;
import com.sanqiwan.reader.receiver.WakeUpHelper;
import com.sanqiwan.reader.service.BookService;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-21
 * Time: 下午6:40
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {

    public static final int PAYMENT_FRAGMENT = -1;

    public static final int RECOMMEND_FRAGMENT_ID = 1;
    public static final int CLASSIFICATION_FRAGMENT_ID = 2;
    public static final int SEARCH_FRAGMENT_ID = 3;
    public static final int TOPIC_FRAGMENT_ID = 4;
    public static final int MINE_FRAGMENT_ID = 5;
    private static final String RECOMMEND_FRAGMENT_TAG = "recommend_fragment";
    private static final String CLASSIFICATION_FRAGMENT_TAG = "classification_fragment";
    private static final String SEARCH_FRAGMENT_TAG = "search_fragment";
    private static final String TOPIC_FRAGMENT_TAG = "topic_fragment";
    private static final String MINE_FRAGMENT_TAG = "mine_fragment";
    public static final String CURRENT_FRAGMENT = "currentFragment";

    public int mCurrentFragmentId;
    private Fragment mCurrentFragment;
    private Fragment mRecommendFragment;
    private Fragment mClassificationFragment;
    private SearchFragment mSearchFragment;
    private Fragment mTopicFragment;
    private MineFragment mMineFragment;
    private long mExitTime;

    private View mBtnRecommend;
    private View mBtnClassification;
    private View mBtnSearch;
    private View mBtnTopic;
    private View mBtnMine;
    private ViewGroup mBottomNavigate;
    private boolean mIsForLogin;
    private static MainActivity sInstance;

    public void onCreate(Bundle savedInstanceState) {
        sInstance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        startService(new Intent(this, BookService.class));
        //更新提示
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.setUpdateListener(null);
        UmengUpdateAgent.setUpdateAutoPopup(true);
        //返回窗口
        FeedbackAgent agent = new FeedbackAgent(this);
        //用户反馈后，系统回复通知
        agent.sync();
        //更新本地唤醒应用的通知时间
        WakeUpHelper.updateAlarm(this);

        initNavigate();

        Intent intent = getIntent();
        int fragmentFlag = hasBooks() ? MINE_FRAGMENT_ID : RECOMMEND_FRAGMENT_ID;
        if (intent.hasExtra(UserCenterFragment.KEY_FOR_LOGIN) &&
                intent.getIntExtra(UserCenterFragment.KEY_FOR_LOGIN, 0) == UserCenterFragment.ACTION_FOR_LOGIN) {
            mIsForLogin = true;
            showFragment(MINE_FRAGMENT_ID);
            return;
        }
        openPaymentFragment(intent);
        if (intent.hasExtra(CURRENT_FRAGMENT)) {
            fragmentFlag = intent.getIntExtra(CURRENT_FRAGMENT, fragmentFlag);
        } else if (savedInstanceState != null) {
            fragmentFlag = savedInstanceState.getInt(CURRENT_FRAGMENT, fragmentFlag);
            hideAllFragments();
        }
        showFragment(fragmentFlag);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(UserCenterFragment.KEY_FOR_LOGIN) &&
                intent.getIntExtra(UserCenterFragment.KEY_FOR_LOGIN, 0) == UserCenterFragment.ACTION_FOR_LOGIN) {
            mIsForLogin = true;
            if (mMineFragment == null) {
                getFragment(MINE_FRAGMENT_ID);
            }
            showFragment(MINE_FRAGMENT_ID);
            mMineFragment.setActionCode(UserCenterFragment.ACTION_FOR_LOGIN);
            return;
        }
        if (intent.hasExtra(CURRENT_FRAGMENT) && intent.getIntExtra(CURRENT_FRAGMENT, 0) > 0) {
            int currentFragment = intent.getIntExtra(CURRENT_FRAGMENT, RECOMMEND_FRAGMENT_ID);
            showFragment(currentFragment);
        } else {
            openPaymentFragment(intent);
        }
    }

    private void openPaymentFragment(Intent intent) {
        if (intent.hasExtra(CURRENT_FRAGMENT) && intent.getIntExtra(CURRENT_FRAGMENT, 0) == PAYMENT_FRAGMENT) {
            clearPopBackStack();   //当从详情最新目录，目录列表Fragment进入阅读界面，阅读VIP需要进行充值时，跳转显示充值的Fragment,当前显示的Fragment
            openSubFragmentInternal(PaymentFragment.newFragment());
        }
    }

    public boolean isForLogin() {
        return mIsForLogin;
    }

    public boolean hasBooks() {
        HistoryManager historyManager = new HistoryManager();
        XBookManager bookManager = new XBookManager();
        if (historyManager.getHistoryCount() > 0 || bookManager.getBookCount() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home_recommend:
                showFragment(RECOMMEND_FRAGMENT_ID);
                break;
            case R.id.btn_home_classification:
                showFragment(CLASSIFICATION_FRAGMENT_ID);
                break;
            case R.id.btn_home_search:
                showFragment(SEARCH_FRAGMENT_ID);
                break;
            case R.id.btn_home_topic:
                showFragment(TOPIC_FRAGMENT_ID);
                break;
            case R.id.btn_home_mine:
                showFragment(MINE_FRAGMENT_ID);
                break;
        }
    }

    private void initNavigate() {
        mBottomNavigate = (ViewGroup) findViewById(R.id.bottom_navigate);
        mBtnRecommend = mBottomNavigate.findViewById(R.id.btn_home_recommend);
        mBtnRecommend.setOnClickListener(this);
        mBtnClassification = mBottomNavigate.findViewById(R.id.btn_home_classification);
        mBtnClassification.setOnClickListener(this);
        mBtnSearch = mBottomNavigate.findViewById(R.id.btn_home_search);
        mBtnSearch.setOnClickListener(this);
        mBtnTopic = mBottomNavigate.findViewById(R.id.btn_home_topic);
        mBtnTopic.setOnClickListener(this);
        mBtnMine = mBottomNavigate.findViewById(R.id.btn_home_mine);
        mBtnMine.setOnClickListener(this);
    }

    private void showFragment(int fragmentId) {
        navigateSelected(fragmentId);
        Fragment fragment = getFragment(fragmentId);
        clearPopBackStack();   //点击底部栏时，清空FragmentManager的mBackStack
        if (fragment != null && fragment != mCurrentFragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (mCurrentFragment != null) {
                transaction.hide(mCurrentFragment);
            }
            String tag = getFragmentTag(fragmentId);
            Fragment f = fragmentManager.findFragmentByTag(tag);
            if (f == null) {
                transaction.add(R.id.main_content_view, fragment, tag);
            }
            transaction.show(fragment);
            transaction.commitAllowingStateLoss();
            mCurrentFragment = fragment;
            mCurrentFragmentId = fragmentId;
        }
    }

    private void clearPopBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private String getFragmentTag(int fragmentId) {
        switch (fragmentId) {
            case SEARCH_FRAGMENT_ID:
                return SEARCH_FRAGMENT_TAG;
            case CLASSIFICATION_FRAGMENT_ID:
                return CLASSIFICATION_FRAGMENT_TAG;
            case TOPIC_FRAGMENT_ID:
                return TOPIC_FRAGMENT_TAG;
            case MINE_FRAGMENT_ID:
                return MINE_FRAGMENT_TAG;
            case RECOMMEND_FRAGMENT_ID:
            default:
                return RECOMMEND_FRAGMENT_TAG;
        }
    }

    private void hideAllFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(SEARCH_FRAGMENT_TAG);
        if (fragment != null) {
            ft.hide(fragment);
        }

        fragment = fragmentManager.findFragmentByTag(CLASSIFICATION_FRAGMENT_TAG);
        if (fragment != null) {
            ft.hide(fragment);
        }

        fragment = fragmentManager.findFragmentByTag(TOPIC_FRAGMENT_TAG);
        if (fragment != null) {
            ft.hide(fragment);
        }

        fragment = fragmentManager.findFragmentByTag(MINE_FRAGMENT_TAG);
        if (fragment != null) {
            ft.hide(fragment);
        }

        fragment = fragmentManager.findFragmentByTag(RECOMMEND_FRAGMENT_TAG);
        if (fragment != null) {
            ft.hide(fragment);
        }
        ft.commitAllowingStateLoss();
    }

    private Fragment getFragmentByTag(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        ft.commitAllowingStateLoss();
        return fragment;
    }

    private Fragment getFragment(int fragmentId) {

        switch (fragmentId) {
            case CLASSIFICATION_FRAGMENT_ID:
                if (mClassificationFragment == null) {
                    mClassificationFragment = createFragment(fragmentId);
                }
                return mClassificationFragment;
            case TOPIC_FRAGMENT_ID:
                if (mTopicFragment == null) {
                    mTopicFragment = createFragment(fragmentId);
                }
                return mTopicFragment;
            case SEARCH_FRAGMENT_ID:
                if (mSearchFragment == null) {
                    mSearchFragment = (SearchFragment) createFragment(fragmentId);
                }
                return mSearchFragment;
            case MINE_FRAGMENT_ID:
                if (mMineFragment == null) {
                    mMineFragment = (MineFragment) createFragment(fragmentId);
                }
                return mMineFragment;
            case RECOMMEND_FRAGMENT_ID:
            default:
                if (mRecommendFragment == null) {
                    mRecommendFragment = createFragment(fragmentId);
                }
                return mRecommendFragment;
        }
    }

    private Fragment createFragment(int fragmentId) {
        Fragment fragment = getFragmentByTag(getFragmentTag(fragmentId));
        if (fragment != null) {
            return fragment;
        }

        switch (fragmentId) {
            case CLASSIFICATION_FRAGMENT_ID:
                fragment = ChannelFragment.newFragment();
                fragment.setRetainInstance(true);
                return fragment;
            case TOPIC_FRAGMENT_ID:
                fragment = TopicFragment.newFragment();
                fragment.setRetainInstance(true);
                return fragment;
            case SEARCH_FRAGMENT_ID:
                fragment = SearchFragment.newFragment();
                fragment.setRetainInstance(true);
                return fragment;
            case MINE_FRAGMENT_ID:
                fragment = MineFragment.newFragment();
                fragment.setRetainInstance(true);
                return fragment;
            case RECOMMEND_FRAGMENT_ID:
            default:
                fragment = RecommendFragment.newFragment();
                fragment.setRetainInstance(true);
                return fragment;
        }

    }

    private void navigateSelected(int fragmentId) {
        mBtnRecommend.setSelected(false);
        mBtnClassification.setSelected(false);
        mBtnSearch.setSelected(false);
        mBtnTopic.setSelected(false);
        mBtnMine.setSelected(false);
        switch (fragmentId) {
            case CLASSIFICATION_FRAGMENT_ID:
                mBtnClassification.setSelected(true);
                break;
            case SEARCH_FRAGMENT_ID:
                mBtnSearch.setSelected(true);
                break;
            case TOPIC_FRAGMENT_ID:
                mBtnTopic.setSelected(true);
                break;
            case MINE_FRAGMENT_ID:
                mBtnMine.setSelected(true);
                break;
            default:
                mBtnRecommend.setSelected(true);
        }
    }

    public static void openMainActivity(Context context, int fragmentId) {
        Intent intent = new Intent(context, MainActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(CURRENT_FRAGMENT, fragmentId);
        context.startActivity(intent);
    }

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_FRAGMENT, mCurrentFragmentId);
    }

    private void ToastExit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, getText(R.string.app_exit_tips), Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            ReaderActivity.finishReaderActivity();
            this.finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean SearchRecommend = false;
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mSearchFragment != null && mCurrentFragment == mSearchFragment) {
                    SearchRecommend = mSearchFragment.onKeyDown(keyCode, event);
                }
                if (!SearchRecommend && !getSupportFragmentManager().popBackStackImmediate()) {
                    ToastExit();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sInstance = null;
    }

    public void setCurrentFragment(Fragment fragment) {
        mCurrentFragment = fragment;
    }

    public static MainActivity getInstance() {
        return sInstance;
    }

    public static void gotoLogin(Activity context, int actionCode) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(UserCenterFragment.KEY_FOR_LOGIN, UserCenterFragment.ACTION_FOR_LOGIN);
        context.startActivityForResult(intent, UserCenterFragment.ACTION_FOR_LOGIN);
    }

    private void openSubFragmentInternal(Fragment subFragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        }
        mCurrentFragment = subFragment;
        transaction.add(R.id.main_content_view, subFragment);
        transaction.show(subFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    public static void openSubFragment(Fragment subFragment) {
        MainActivity mainActivity = MainActivity.getInstance();
        if (mainActivity != null) {
            mainActivity.openSubFragmentInternal(subFragment);
        }
    }

    public static void openPaymentFragmentFromActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(CURRENT_FRAGMENT, PAYMENT_FRAGMENT);
        context.startActivity(intent);
    }
}