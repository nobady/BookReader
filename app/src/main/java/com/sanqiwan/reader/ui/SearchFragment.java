package com.sanqiwan.reader.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.BucketListAdapter;
import com.sanqiwan.reader.adapter.OnlineBooksAdapter;
import com.sanqiwan.reader.adapter.SearchSuggestionAdapter;
import com.sanqiwan.reader.model.BookItem;
import com.sanqiwan.reader.track.UMengEventId;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import com.sanqiwan.reader.util.UIUtil;
import com.sanqiwan.reader.view.RecommendAnimation;
import com.sanqiwan.reader.view.RecommendLayout;
import com.sanqiwan.reader.webservice.BookWebService;
import com.sanqiwan.reader.webservice.OperationWebService;
import com.umeng.analytics.MobclickAgent;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-21
 * Time: 下午6:57
 * To change this template use File | Settings | File Templates.
 */
public class SearchFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int RECOMMEND_SIZE = 7;
    private static final int RECOMMEND_PAGE_SIZE = 40;
    private static final int PAGE_SIZE = 30;
    private View mFragmentView;
    private Context mContext;
    private AutoCompleteTextView mSearchBox;
    private ListView mResultListView;
    private TextView mEmptyView;
    private View mSearchBoxDelete;
    private View mHintSearchIcon;
    private View mRecommendMore;

    private BookWebService mBookWebService;
    private SearchSuggestionAdapter mSearchSuggestionAdapter;
    private OnlineBooksAdapter mSearchResultAdapter;
    private SearchLoadMoreTask mSearchLoadMoreTask;
    private OperationWebService mOperationWebService;
    private int mSearchLoadMoreIndex = 2;
    private String mSearchKey;
    private LayoutInflater mInflater;
    private ViewGroup mRecommendPanel;
    private ViewGroup mSearchResultPanel;
    private RecommendLayout mRecommendAnimation;
    private boolean mIsShowSuggestion = true;
    private List<String> mRecommendKeyList;
    private List<String> mResultRecommendList;

    public static SearchFragment newFragment() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        mContext = getContext();

        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.search, container, false);
            mBookWebService = new BookWebService();
            mOperationWebService = new OperationWebService();
            mRecommendKeyList = new ArrayList<String>();
            mSearchBox = (AutoCompleteTextView) mFragmentView.findViewById(R.id.search_box);
            mSearchBox.setDropDownBackgroundResource(R.drawable.search_drop_down_bg);
            mSearchBox.addTextChangedListener(new SearchTextWatcher());
            mSearchBox.setThreshold(Integer.MAX_VALUE);
            mSearchBox.setOnItemClickListener(mSearchSuggestionClickListener);
            mSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    String key = v.getText().toString().trim();
                    showSearchResult(key);
                    return true;
                }
            });
            mSearchBoxDelete = mFragmentView.findViewById(R.id.search_box_delete);
            mHintSearchIcon = mFragmentView.findViewById(R.id.hint_search_icon);
            mSearchBoxDelete.setOnClickListener(this);
            mFragmentView.findViewById(R.id.search_button).setOnClickListener(this);

            mResultListView = (ListView) mFragmentView.findViewById(R.id.search_result);
            mResultListView.setOnItemClickListener(this);
            mEmptyView = (TextView) mFragmentView.findViewById(R.id.empty_view);
            mResultListView.setEmptyView(mEmptyView);
            mSearchResultAdapter = new OnlineBooksAdapter(mContext);
            mResultListView.setAdapter(mSearchResultAdapter);
            mSearchResultPanel = (ViewGroup) mFragmentView.findViewById(R.id.search_result_panel);
            mSearchResultPanel.setVisibility(View.GONE);

            mRecommendPanel = (ViewGroup) mFragmentView.findViewById(R.id.recommend_panel);
            mRecommendMore = mFragmentView.findViewById(R.id.recommend_more);
            mRecommendMore.setOnClickListener(this);

            mRecommendAnimation = (RecommendLayout) mFragmentView.findViewById(R.id.animation_view);
            recommendThreadStart();
        }
        if (mFragmentView.getParent() != null) {
            ((ViewGroup) mFragmentView.getParent()).removeAllViews();
        }
        return mFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecommendAnimation.setChildOnClickListener(mListener);
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String key = (String) v.getTag();
            mIsShowSuggestion = false;
            mSearchBox.setText(key);
            showSearchResult(key);
        }
    };

    private AdapterView.OnItemClickListener mSearchSuggestionClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String key = (String) view.getTag();
            showSearchResult(key);
        }
    };

    private void showSearchResult(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mSearchResultPanel.setVisibility(View.VISIBLE);
        mRecommendPanel.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(key)) {
            mResultListView.requestFocus();
            mSearchKey = key;
            hideSoftKeyboard();
            // 这个方法表示用户执行一个新的关键字搜索，所以分页从0开始
            mSearchLoadMoreIndex = 2;//新的关键字开始搜索，那么加载更多需要重新初始化
            AsyncTaskUtil.execute(new SearchTask(key, 0));
        }
    }

    private BucketListAdapter.LoadMoreListener mLoadMoreResultListener = new BucketListAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (!TextUtils.isEmpty(mSearchKey) && !isLoadMoreTaskRunning()) {
                mSearchLoadMoreTask = new SearchLoadMoreTask(mSearchKey, mSearchLoadMoreIndex);
                AsyncTaskUtil.execute(mSearchLoadMoreTask);
                mSearchLoadMoreIndex++;
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.search_result:
                if (position < 0 || position >= mSearchResultAdapter.getElementCount()) {
                    return;
                }
                BookItem searchItem = mSearchResultAdapter.getElement(position);
                startBookDetail(searchItem.getBookId(), searchItem.getBookName(), searchItem.isFinish());
                break;
        }
    }

    private void startBookDetail(long bookId, String bookName, boolean isFinish) {
        MainActivity.openSubFragment(BookDetailFragment.newFragment(bookId, bookName, isFinish));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_button:
                String key = mSearchBox.getText().toString().trim();
                showSearchResult(key);
                break;
            case R.id.recommend_more:
                MobclickAgent.onEvent(getContext(), UMengEventId.SEARCH_YOYO_CLICK);
                //mRecommendAnimation.replaceKeys(getRecommendKeys());
                mRecommendAnimation.setWords(getRecommendKeys());
                break;
            case R.id.search_box_delete:
                mSearchBox.setText("");
                break;
        }
    }

    private int mCurrentPage;
    private int mSumPage;
    private void bindRecommendKey() {
        List<String> recommends = getRecommendKeys();
        if (recommends == null) {
            return;
        }
        //mRecommendAnimation.addChildren(recommends);
        //mRecommendAnimation.startAnimation();
        mRecommendAnimation.setWords(recommends);
    }

    private List<String> getRecommendKeys() {
        if (isRecommendEmpty()) {
            recommendThreadStart();
            return null;
        }
//        if (mRecommendKeyList.size() < RECOMMEND_SIZE * 2) {
//            recommendThreadStart();
//        }
//        if (mSumPage > 2 && mCurrentPage == mSumPage - 2) {
//            recommendThreadStart();
//        }
        if (mCurrentPage == mSumPage) {
            mCurrentPage = 0;
        }
        if (mSumPage > 1) {
            int startIndex = (mCurrentPage == mSumPage - 1) ? mRecommendKeyList.size() - RECOMMEND_SIZE : mCurrentPage * RECOMMEND_SIZE;
            mCurrentPage++;
            return mRecommendKeyList.subList(startIndex, startIndex + RECOMMEND_SIZE);
        } else {
            //recommendThreadStart();
            return mRecommendKeyList;
        }
    }

    private boolean isRecommendEmpty() {
        if (mRecommendKeyList == null) {
            return true;
        }
        if (mRecommendKeyList.isEmpty()) {
            return true;
        }
        return  false;
    }

    private void showSearchSuggestion(List<String> suggestion) {
        if (suggestion == null || getActivity() == null) {
            return;
        }
        mSearchSuggestionAdapter = new SearchSuggestionAdapter(mContext, suggestion);
        mSearchBox.setAdapter(mSearchSuggestionAdapter);
        mSearchBox.showDropDown();
    }

    private class SearchSuggestionTask extends AsyncTask<Void, Void, List<String>> {
        private String key;

        public SearchSuggestionTask(String txt) {
            key = txt;
        }

        @Override
        protected void onPostExecute(List<String> list) {
            if (getActivity() == null) {
                return;
            }
            if (mSearchBox.hasFocus()) {
                showSearchSuggestion(list);
            }
        }

        @Override
        protected List<String> doInBackground(Void... objects) {
            try {
                return mBookWebService.getSearchSuggestion(key);
            } catch (ZLNetworkException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class SearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mSearchBox.isPerformingCompletion()) {
                return;
            }
            if (!mIsShowSuggestion) {
                mIsShowSuggestion = true;
                return;
            }
            final String text = s.toString().trim();
            if (!TextUtils.isEmpty(text)) {
                AsyncTaskUtil.execute(new SearchSuggestionTask(text));
            } else {
                //如果文本变化，输入框内容为空，并且搜索结果为空，显示推荐关键字
                mRecommendPanel.requestFocus();
                mRecommendPanel.setVisibility(View.VISIBLE);
                mSearchResultPanel.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                mSearchBoxDelete.setVisibility(View.VISIBLE);
                mHintSearchIcon.setVisibility(View.GONE);
                mSearchBox.setPadding(UIUtil.dipToPixel(12), UIUtil.dipToPixel(8), UIUtil.dipToPixel(12), UIUtil.dipToPixel(8));
            } else {
                mSearchBoxDelete.setVisibility(View.GONE);
                mHintSearchIcon.setVisibility(View.VISIBLE);
                mSearchBox.setPadding(UIUtil.dipToPixel(34), UIUtil.dipToPixel(8), UIUtil.dipToPixel(12), UIUtil.dipToPixel(8));
            }
        }
    }

    private class SearchTask extends AsyncTask<Void, Void, List<BookItem>> {
        private String mKey;
        private int mPageIndex;

        public SearchTask(String txt, int pageIndex) {
            mKey = txt;
            mPageIndex = pageIndex;
        }

        @Override
        protected void onPreExecute() {
            mSearchResultAdapter.disableLoadMore();
            mSearchResultAdapter.clear();
        }

        @Override
        protected List<BookItem> doInBackground(Void... objects) {
            try {
                return mBookWebService.getSearchItems(mKey, mPageIndex);
            } catch (ZLNetworkException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<BookItem> list) {
            if (getActivity() == null) {
                return;
            }
            if (list == null) {
                mEmptyView.setText(mContext.getResources().getString(R.string.no_wifi));
                return;
            } else if (list.isEmpty()) {
                mEmptyView.setText(mContext.getResources().getString(R.string.no_data));
                return;
            }
            if (list.size() == PAGE_SIZE) {
                mSearchResultAdapter.setLoadMoreListener(mLoadMoreResultListener);
            } else {
                mSearchResultAdapter.disableLoadMore();
            }
            mSearchResultAdapter.addAll(list);
        }
    }

    public boolean isLoadMoreTaskRunning() {
        if (mSearchLoadMoreTask == null) {
            return false;
        }
        if (mSearchLoadMoreTask.getStatus() == AsyncTask.Status.RUNNING) {
            return true;
        }
        return false;
    }

    private class SearchLoadMoreTask extends AsyncTask<Void, Void, List<BookItem>> {
        private String mKey;
        private int mPageIndex;

        public SearchLoadMoreTask(String txt, int pageIndex) {
            mKey = txt;
            mPageIndex = pageIndex;
        }

        @Override
        protected void onPostExecute(List<BookItem> list) {
            if (getActivity() == null) {
                return;
            }
            if (list == null) {
                mSearchResultAdapter.disableLoadMore();
                return;
            } else if (list.isEmpty()) {
                mSearchResultAdapter.disableLoadMore();
                return;
            } else if (list.size() < PAGE_SIZE) {
                mSearchResultAdapter.disableLoadMore();
            }
            mSearchResultAdapter.addAll(list);
        }

        @Override
        protected List<BookItem> doInBackground(Void... objects) {
            try {
                return mBookWebService.getSearchItems(mKey, mPageIndex);
            } catch (ZLNetworkException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private Thread mRecommendThread;
    private void recommendThreadStart() {
        if (mRecommendThread == null) {
            mRecommendThread = new Thread(mSearchRecommend);
            mRecommendThread.start();
        }
    }

    private Runnable mSearchRecommend = new Runnable() {
        @Override
        public void run() {
            try {
                mResultRecommendList = null;
                mResultRecommendList = mOperationWebService.getSearchRecommend(RECOMMEND_PAGE_SIZE);
            } catch (ZLNetworkException e) {
                e.printStackTrace();
                mResultRecommendList = null;
            }
            UIUtil.getHandler().post(mRefreshRecommend);
        }
    };
    private Runnable mRefreshRecommend = new Runnable() {
        @Override
        public void run() {
            mRecommendThread = null;
            if (mResultRecommendList != null && mResultRecommendList.size() > 0) {
                mRecommendKeyList.addAll(mResultRecommendList);
                //从返回关键字从取出7条数据绑定。
                mSumPage = mRecommendKeyList.size() / RECOMMEND_SIZE;
                if (mRecommendKeyList.size() % RECOMMEND_SIZE > 0) {
                    mSumPage++;
                }
                if (mCurrentPage == 0) {
                    bindRecommendKey();
                }
            }
        }
    };

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
    }

    /**
     * 返回键监听事件
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mRecommendPanel.getVisibility() == View.GONE) {
            mSearchBox.setText("");
            mRecommendPanel.requestFocus();
            mRecommendPanel.setVisibility(View.VISIBLE);
            mSearchResultPanel.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

}
