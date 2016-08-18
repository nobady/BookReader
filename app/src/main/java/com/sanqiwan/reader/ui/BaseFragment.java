package com.sanqiwan.reader.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import com.sanqiwan.reader.AppContext;
import com.umeng.fb.FeedbackAgent;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-8-9
 * Time: 上午11:00
 * To change this template use File | Settings | File Templates.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public Context getContext() {
        if (getActivity() != null) {
            return getActivity();
        } else {
            return AppContext.getInstance();
        }
    }

    void openFeedbackActivity(Context context) {
        FeedbackAgent agent = new FeedbackAgent(context);
        agent.startFeedbackActivity();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            MainActivity mainActivity = MainActivity.getInstance();
            if (mainActivity != null) {
                mainActivity.setCurrentFragment(this);
            }
        }
    }
}