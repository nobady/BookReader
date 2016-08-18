package com.sanqiwan.reader.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.*;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.VipChaptersAdapter;
import com.sanqiwan.reader.engine.SubscriptionManager;
import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.UserInfo;
import com.sanqiwan.reader.model.VipVolumeItem;
import com.sanqiwan.reader.ui.SubscriptionStorage;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-11-12
 * Time: 上午11:01
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionDialog extends Dialog implements View.OnClickListener {

    private static int default_height = 440;//默认高度
    private VipChaptersAdapter mVipChaptersAdapter;
    private ListView mListView;
    private Button mOkBtn, mRechargeBtn;
    private CheckBox mSelectAll;
    private TextView mCountOfSelectTextView;
    private TextView mOverageTextView, mConsumptionTextView;
    private TextView mEmptyView;
    private long mBookId, mChapterId;
    private View mHeadView;
    private Callback mCallback;
    private Context mContext;
    private ImageView mClosedBtn;
    private ProgressBar mProgressBar;
    private UserInfo mUserInfo;
    private ViewGroup mView;
    private View mSubscriptionView;
    private View mSubscribedView;
    private View mSubscribFailedView;
    private SubscriptionStorage mSubscriptionStorage;

    public interface Callback {
        public void onSubscribed();

        public void onSubscribeFailed();

        public void onRecharged();
    }

    public SubscriptionDialog(Context context, long bookId, long chapterId) {
        super(context, R.style.dialog_style);
        setContentView(R.layout.subscription_layout);
        mContext = context;
        mBookId = bookId;
        mChapterId = chapterId;
        mSubscriptionStorage = new SubscriptionStorage();
        mView = (ViewGroup) findViewById(R.id.content_view);
        mClosedBtn = (ImageView) findViewById(R.id.closed_btn);
        mClosedBtn.setOnClickListener(this);
        mSubscriptionView = LayoutInflater.from(context).inflate(R.layout.subscription_list_layout, null);
        mView.addView(mSubscriptionView);

        mHeadView = LayoutInflater.from(context).inflate(R.layout.subscription_head_view, null);
        initHeadView();
        initSubscriptionView();

        mVipChaptersAdapter = new VipChaptersAdapter(context, mListView);
        mListView.setAdapter(mVipChaptersAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                refreshView();
                setButtonState();
                mVipChaptersAdapter.notifyDataSetChanged();
            }
        });
        refreshView();
        //set window params
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        //set width,height by density and gravity
        float density = getDensity(context);
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) (default_height * density);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        loadSubscriptionDialogInfo();
    }

    private float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }

    private void initHeadView() {
        mSelectAll = (CheckBox) mHeadView.findViewById(R.id.select_all);
        mSelectAll.setOnClickListener(this);
        mOverageTextView = (TextView) mHeadView.findViewById(R.id.my_overage);
        mRechargeBtn = (Button) mHeadView.findViewById(R.id.recharge_btn);
        mRechargeBtn.setOnClickListener(this);
        setOverageText(0);
    }

    private void initSubscriptionView() {
        mListView = (ListView) mSubscriptionView.findViewById(R.id.vip_chapters_list);
        mListView.addHeaderView(mHeadView);
        mCountOfSelectTextView = (TextView) mSubscriptionView.findViewById(R.id.count_of_select);
        mConsumptionTextView = (TextView) mSubscriptionView.findViewById(R.id.consumption);
        mProgressBar = (ProgressBar) mSubscriptionView.findViewById(R.id.progress_bar);
        mOkBtn = (Button) mSubscriptionView.findViewById(R.id.ok_btn);
        mOkBtn.setOnClickListener(this);
        mEmptyView = (TextView) mSubscriptionView.findViewById(R.id.empty_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn:
                onSubscirbed();
                break;
            case R.id.recharge_btn:
                mCallback.onRecharged();
                break;
            case R.id.select_all:
                isSelectAll();
                refreshView();
                break;
            case R.id.closed_btn:
                dismiss();
                break;
            case R.id.continue_read:
                dismiss();
                mCallback.onSubscribed();
                break;
            case R.id.repurchase_btn:
                onResubscribed();
                break;
        }
    }

    private void onSubscirbed() {
        if (mVipChaptersAdapter != null && mListView != null) {
            mSubscriptionStorage.setVipVolumeItemList(mVipChaptersAdapter.getVipVolumeItemList());
            mSubscriptionStorage.setSparseBooleanArray(mListView.getCheckedItemPositions());
            if (mUserInfo != null && mUserInfo.getUserMoney() > getConsumptionOfQWB(mSubscriptionStorage.getVipVolumeItemList(), mSubscriptionStorage.getSparseBooleanArray())) {
                AsyncTaskUtil.execute(new GetVIPChapterOrderTask());
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.overage_not_enough), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onResubscribed() {
        AsyncTaskUtil.execute(new GetVIPChapterOrderTask());
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    private void refreshView() {
        if (mConsumptionTextView != null) {
            mConsumptionTextView.setText(setConsumptionText(mContext.getString(R.string.my_consumption), 2, 4));
        }
        if (mCountOfSelectTextView != null) {
            mCountOfSelectTextView.setText(setCountOfSelect(mContext.getString(R.string.my_count_of_select), 3, 1));
        }
        setOkBtnEnable();
    }

    private void setOkBtnEnable() {
        if (mListView != null) {
            if (getCountOfSelect(mListView.getCheckedItemPositions()) > 0) {
                mOkBtn.setEnabled(true);
            } else {
                mOkBtn.setEnabled(false);
            }
        }
    }

    private SpannableString setConsumptionText(String srcString, int start, int endLength) {
        srcString = String.format(srcString, getConsumptionOfQWB(mVipChaptersAdapter.getVipVolumeItemList(), mListView.getCheckedItemPositions()));
        SpannableString spannableString = new SpannableString(srcString);
        spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.record_money_color)), start, spannableString.length() - endLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private void setOverageText(int overage) {
        String myOverage = mContext.getString(R.string.my_overage);
        myOverage = String.format(myOverage, overage);
        SpannableString spannableString = new SpannableString(myOverage);
        spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.record_money_color)), 6, myOverage.length() - 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (mOverageTextView != null) {
            mOverageTextView.setText(spannableString);
        }
    }

    private SpannableString setCountOfSelect(String srcString, int start, int endLength) {
        srcString = String.format(srcString, getCountOfSelect(mListView.getCheckedItemPositions()));
        SpannableString spannableString = new SpannableString(srcString);
        spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.record_money_color)), start, spannableString.length() - endLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private void selectAllChapters(boolean isSelected) {
        int count = mVipChaptersAdapter.getElementCount();
        if (mListView != null && mListView.getAdapter() != null) {
            for (int i = 0; i < count; i++) {
                mListView.setItemChecked(i + mListView.getHeaderViewsCount(), isSelected);
            }
        }
    }

    private void setButtonState() {
        if (getCountOfSelect(mListView.getCheckedItemPositions()) == mVipChaptersAdapter.getElementCount()) {
            mSelectAll.setChecked(true);
        } else {
            mSelectAll.setChecked(false);
        }
    }

    private int getCountOfSelect(SparseBooleanArray selectedBooleanArray) {
        if (mListView != null) {
            int countOfSelect = 0;
            for (int i = 0; i < selectedBooleanArray.size(); i++) {
                if (selectedBooleanArray.valueAt(i)) {
                    countOfSelect++;
                }
            }
            return countOfSelect;
        }
        return 0;
    }

    private void isSelectAll() {
        selectAllChapters(mSelectAll.isChecked());
    }

    public void loadSubscriptionDialogInfo() {
        AsyncTaskUtil.execute(new GetSbuscriptionDialogInfoTask());
    }

    private int getConsumptionOfQWB(List<VipVolumeItem> vipVolumeItemList, SparseBooleanArray selectedBooleanArray) {
        if (mListView != null) {
            int consumption = 0;
            int numHeaders = mListView.getHeaderViewsCount();
            for (int i = 0; i < selectedBooleanArray.size(); i++) {
                if (selectedBooleanArray.valueAt(i)) {
                    consumption = consumption + vipVolumeItemList.get(selectedBooleanArray.keyAt(i) - numHeaders).getPrice();
                }
            }
            return consumption;
        }
        return 0;
    }

    private void showSubscribed() {
        mView.removeAllViews();
        mSubscribedView = LayoutInflater.from(mContext).inflate(R.layout.subscribed_layout, null);
        initSubscribedView();
        mView.addView(mSubscribedView);
    }

    private void showSbuscribedFailed() {
        mView.removeAllViews();
        mSubscribFailedView = LayoutInflater.from(mContext).inflate(R.layout.subscribedfailed_layout, null);
        initSubscribedFailedView();
        mView.addView(mSubscribFailedView);
    }

    private void initSubscribedFailedView() {
        if (mSubscribFailedView != null) {
            Button repurchaseBtn = (Button) mSubscribFailedView.findViewById(R.id.repurchase_btn);
            repurchaseBtn.setOnClickListener(this);
        }
    }

    private void initSubscribedView() {
        if (mSubscribedView != null) {
            TextView subscribedChapters = (TextView) mSubscribedView.findViewById(R.id.subscribed_chapters);
            String subscribedString = mContext.getString(R.string.subscribed_chapters);
            subscribedString = String.format(subscribedString, getCountOfSelect(mSubscriptionStorage.getSparseBooleanArray()));
            subscribedChapters.setText(setCountOfSelect(subscribedString, 6, 1));
            TextView consumption = (TextView) mSubscribedView.findViewById(R.id.consumption);
            String consumptionString = mContext.getString(R.string.subscribed_consumption);
            consumptionString = String.format(consumptionString, getConsumptionOfQWB(mSubscriptionStorage.getVipVolumeItemList(), mSubscriptionStorage.getSparseBooleanArray()));
            consumption.setText(setConsumptionText(consumptionString, 1, 3));
            Button continueBtn = (Button) mSubscribedView.findViewById(R.id.continue_read);
            continueBtn.setOnClickListener(this);
        }
    }

    class GetSbuscriptionDialogInfoTask extends AsyncTask<Void, Void, List<VipVolumeItem>> {

        @Override
        protected List<VipVolumeItem> doInBackground(Void... params) {
            if (UserManager.getInstance().isLogin()) {
                mUserInfo = UserManager.getInstance().getUserInfo(true);
                try {
                    return SubscriptionManager.getsInstance().getBuyVIPChapterList(mBookId, UserManager.getInstance().getUserId());
                } catch (ZLNetworkException e) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<VipVolumeItem> vipVolumeItemList) {
            if (mUserInfo != null) {
                setOverageText(mUserInfo.getUserMoney());
            }
            if (vipVolumeItemList != null) {
                mProgressBar.setVisibility(View.GONE);
                mVipChaptersAdapter.setVipVolumeItemList(vipVolumeItemList);
                for (int i = 0; i < vipVolumeItemList.size(); i++) {
                    if (mChapterId == vipVolumeItemList.get(i).getChapterId()) {
                        mListView.setItemChecked(i + mListView.getHeaderViewsCount(), true);
                        mListView.setSelection(i + mListView.getHeaderViewsCount());
                    }
                }
                refreshView();
            } else {
                mProgressBar.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            }
        }
    }

    class GetVIPChapterOrderTask extends AsyncTask<Void, Void, Boolean> {

        private LoadingProgressDialog mLoadingProgressDialog;

        public GetVIPChapterOrderTask() {
            mLoadingProgressDialog = new LoadingProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            mLoadingProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return SubscriptionManager.getsInstance().getVipChapterOrder(mBookId, mListView.getCheckedItemIds());
            } catch (ZLNetworkException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean isSubscription) {
            mLoadingProgressDialog.dismiss();
            if (isSubscription != null && isSubscription) {
                showSubscribed();
            } else {
                showSbuscribedFailed();
            }
        }
    }
}
