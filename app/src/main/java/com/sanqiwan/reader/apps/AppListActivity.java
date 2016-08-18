package com.sanqiwan.reader.apps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.ui.BaseFragmentActivity;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-10-15
 * Time: 上午10:13
 * To change this template use File | Settings | File Templates.
 */
public class AppListActivity extends BaseFragmentActivity implements View.OnClickListener{

    public static final String FRAGMENT_TYPE = "fragment_type";
    public static final String TITLE = "title";
    public static final String ID = "id";
    public static final String BANNER = "banner";
    public static final String DESCRIBE = "describe";
    private static final int CATEGORY_TYPE = 1;
    private static final long DEFAULT_ID = 0;

    private long mId;
    private int mFragmentType;
    private String mBanner;
    private String mDescribe;

    private AppListFragment mRankingFragment;
    private ImageView mBackImageView;
    private TextView mTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list_activity_layout);
        mId = getIntent().getLongExtra(ID, DEFAULT_ID);
        mFragmentType = getIntent().getIntExtra(FRAGMENT_TYPE, CATEGORY_TYPE);
        mBanner = getIntent().getStringExtra(BANNER);
        mDescribe = getIntent().getStringExtra(DESCRIBE);
        mBackImageView = (ImageView) findViewById(R.id.btBack);
        mBackImageView.setVisibility(View.VISIBLE);
        mBackImageView.setOnClickListener(this);
        View searchView = findViewById(R.id.btRight);
        searchView.setVisibility(View.GONE);
        mTextView = (TextView) findViewById(R.id.title);
        mTextView.setTextSize(18);
        mTextView.setText(R.string.promotion);
        mRankingFragment = AppListFragment.newFragment(mFragmentType, mId, mBanner, mDescribe);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.category_topic_content_view, mRankingFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btBack:
                finish();
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, AppListActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}