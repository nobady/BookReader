package com.sanqiwan.reader.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.BucketListAdapter;
import com.sanqiwan.reader.data.Categories;
import com.sanqiwan.reader.data.CategoryItem;
import com.sanqiwan.reader.model.RankItem;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import com.sanqiwan.reader.view.ExpandableItemView;
import com.sanqiwan.reader.webimageview.WebImageView;
import com.sanqiwan.reader.webservice.RankListWebService;
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
public class RankListFragment extends BaseFragment {

    private Context mContext;
    private View mView;
    private RankListAdapter mAdapter;
    private ProgressBar mProgressBar;
    private View mErrorView;
    private Button mRetryButton;

    public static RankListFragment newFragment() {
        return new RankListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        if (mView == null) {
            mView = inflater.inflate(R.layout.ranklist_fragment, container, false);
            ListView list = (ListView) mView.findViewById(R.id.listview);
            mProgressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
            mRetryButton = (Button) mView.findViewById(R.id.retry_btn);
            mErrorView = mView.findViewById(R.id.error_layout);
            mRetryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncTaskUtil.execute(new LoadRankListTask());
                }
            });
            List<RankItem> items = new ArrayList<RankItem>();
            mAdapter = new RankListAdapter(getContext(), items);
            list.setAdapter(mAdapter);
            AsyncTaskUtil.execute(new LoadRankListTask());

        }
        if (mView.getParent() != null) {
            ((ViewGroup) mView.getParent()).removeAllViews();
        }
        return mView;
    }

    class RankListAdapter extends BucketListAdapter<RankItem> {


        public RankListAdapter(Context ctx, List<RankItem> elements) {
            super(ctx, elements, 2);
        }

        @Override
        protected View newView(int position, RankItem element) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.ranklist_item, null);
            ViewHolder holder = new ViewHolder();
            holder.nameView = (TextView) view.findViewById(R.id.name);
            holder.coverView = (WebImageView) view.findViewById(R.id.cover);
            view.setTag(holder);
            return view;
        }

        @Override
        protected void bindView(View view, int position, final RankItem element) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.nameView.setText(element.getName());
            holder.coverView.setDefaultImageResource(R.drawable.no_book_pic);
            holder.coverView.setImageUrl(element.getCover());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = RankBookListFragment.newInstance(element);
                    MainActivity.openSubFragment(fragment);
                }
            });
        }

        private class ViewHolder {
            WebImageView coverView;
            TextView nameView;
        }
    }


    class LoadRankListTask extends AsyncTask<Void, Void, List<RankItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mErrorView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<RankItem> doInBackground(Void... params) {
            try {
                return new RankListWebService().getRankList();
            } catch (ZLNetworkException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(List<RankItem> rankItemList) {
            mProgressBar.setVisibility(View.GONE);
            if (rankItemList != null && !rankItemList.isEmpty()) {
                mAdapter.addAll(rankItemList);
            } else {
                mErrorView.setVisibility(View.VISIBLE);
            }
        }
    }
}