package com.sanqiwan.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.apps.AppListActivity;
import com.sanqiwan.reader.preference.Settings;
import com.sanqiwan.reader.ui.AboutVipFragment;
import com.sanqiwan.reader.ui.MainActivity;
import com.sanqiwan.reader.util.UpdateUtil;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/8/13
 * Time: 7:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingListAdapter extends BaseAdapter implements View.OnClickListener{
    private Context mContext;
    private String[]  mSettingString;
    private final static int CHECK_UPDATE = 0;
    private final static int FEED_BACK = 1;
    private final static int ALWAY_BACKGROUND_LIGHT = 2;
    private final static int VOLUME_PAGING = 3;
    private final static int PROMTION = 4;
    private final static int VIP = 5;
    private boolean mShowVip;

    public SettingListAdapter(Context context, boolean showVip) {
        mContext = context;
        mShowVip = showVip;
        mSettingString = mContext.getResources().getStringArray(R.array.settings_string);
    }

    @Override
    public int getCount() {
        if (mShowVip) {
            return mSettingString.length;
        } else {
            return mSettingString.length - 1;
        }
    }

    @Override
    public Object getItem(int position) {
        return mSettingString[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.setting_item, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        textView.setText(mSettingString[position]);
        convertView.setTag(position);
        ImageView checkBox = (ImageView) convertView.findViewById(R.id.checkBox);
        if (position == VOLUME_PAGING) {
            checkBox.setSelected(Settings.getInstance().isReaderVolumePaging());
            checkBox.setVisibility(View.VISIBLE);
        } else if (position == ALWAY_BACKGROUND_LIGHT) {
            checkBox.setSelected(Settings.getInstance().isReaderBackgroundLightAlways());
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }
        convertView.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        int tag = (Integer)v.getTag();
        switch (tag) {
            case CHECK_UPDATE:
                UpdateUtil.checkUpdate(mContext);
                break;
            case FEED_BACK:
                if (mOpenFeedbackFragmentListener != null) {
                    mOpenFeedbackFragmentListener.onClick(v);
                }
                break;
            case VOLUME_PAGING:
                Settings.getInstance().setReaderVolumePaging(
                        !Settings.getInstance().isReaderVolumePaging());
                notifyDataSetChanged();
                break;
            case ALWAY_BACKGROUND_LIGHT:
                Settings.getInstance().setReaderBackgroundLightAlways(
                        !Settings.getInstance().isReaderBackgroundLightAlways());
                notifyDataSetChanged();
                break;
            case PROMTION:
                AppListActivity.start(mContext);
                break;
            case VIP:
                MainActivity.openSubFragment(AboutVipFragment.newFragment());
                break;
        }

    }

    public void setOpenFeedbackFragmentListener(View.OnClickListener listener) {
        mOpenFeedbackFragmentListener = listener;
    }
    private View.OnClickListener mOpenFeedbackFragmentListener;
}
