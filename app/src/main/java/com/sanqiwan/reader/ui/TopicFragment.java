package com.sanqiwan.reader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.TopicAdapter;
import com.sanqiwan.reader.data.TopicManager;
import com.sanqiwan.reader.model.Topic;
import com.sanqiwan.reader.util.UIUtil;
import com.sanqiwan.reader.webservice.OperationWebService;
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
public class TopicFragment extends BaseFragment {

    private View mFragmentView;
    private Context mContext;
    private final static String SP_NAME = "book_config";
    private final static String LAST_REFRESH_TIME = "refresh_time";
    public final static String BOOK_IDS = "book_ids";
    private ListView mListView;
    private List<Topic> mTopicList;
    private TopicAdapter mTopicAdapter;
    private TopicManager mTopicManager;
    private long mRefreshTime;
    private TextView mEmptyView;
    private ViewGroup mTopView;
    private TextView mTopicTitle;

    public static TopicFragment newFragment() {
        return new TopicFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mFragmentView == null) {
            mContext = getContext();
            mFragmentView = inflater.inflate(R.layout.topic, container, false);

            mTopicList = new ArrayList<Topic>();
            mListView = (ListView) mFragmentView.findViewById(R.id.lv_recommend);
            mTopicList = new ArrayList<Topic>();
            mTopicManager = new TopicManager(mContext);
            mTopicList = mTopicManager.getAllTopic();
            mTopicAdapter = new TopicAdapter((Activity)mContext, mTopicList, mListView);
            mListView.setAdapter(mTopicAdapter);

            initTopView();

            updateTopics();
            mListView.setOnItemClickListener(mOnItemClickListener);

            mEmptyView = (TextView) mFragmentView.findViewById(R.id.empty_view);
            mListView.setEmptyView(mEmptyView);
            mEmptyView.setText(R.string.data_loading);
        }
        if (mFragmentView.getParent() != null) {
            ((ViewGroup) mFragmentView.getParent()).removeAllViews();
        }
        return mFragmentView;
    }

    private void initTopView() {
        mTopView = (ViewGroup) mFragmentView.findViewById(R.id.topic_top_bar);
        mTopicTitle = (TextView) mTopView.findViewById(R.id.top_title);
        mTopicTitle.setText(R.string.book_topic);
        mTopView.findViewById(R.id.btn_return).setVisibility(View.GONE);
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Topic topic = (Topic) parent.getItemAtPosition(position);
            MainActivity.openSubFragment(TopicDetailFragment.newFragment(topic));
        }
    };

    private void updateTopics() {
        mRefreshTime = mContext.getSharedPreferences(SP_NAME, mContext.MODE_PRIVATE).getLong(LAST_REFRESH_TIME, 0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OperationWebService operationWebService = new OperationWebService();
                List<Topic> topics = null;
                try {
                    topics = operationWebService.getTopics(mRefreshTime);
                } catch (ZLNetworkException e) {
                }
                if (topics != null && topics.size() > 0) {
                    mTopicManager.deleteAllTopic();
                    mTopicManager.addTopicList(topics);
                    mTopicList = mTopicManager.getAllTopic();
                    mRefreshTime = topics.get(topics.size() - 1).getRefreshTime();
                    UIUtil.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mTopicAdapter.setData(mTopicList);
                            SharedPreferences.Editor editor = mContext.getSharedPreferences(SP_NAME, mContext.MODE_PRIVATE).edit();
                            editor.putLong(LAST_REFRESH_TIME, mRefreshTime);
                            editor.commit();
                        }
                    });
                } else {
                    UIUtil.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mEmptyView.setText(R.string.no_wifi);
                        }
                    });
                }
            }
        }).start();
    }

}
