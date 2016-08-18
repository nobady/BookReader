package com.sanqiwan.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.sanqiwan.reader.R;

import java.util.List;

/**
 * User: Sam
 * Date: 13-7-27
 * Time: 下午3:55
 */
public class SearchSuggestionAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private LayoutInflater mInflater;
    private Filter mFilter;

    public SearchSuggestionAdapter(Context context, List<String> list) {
        super(context, 0, list);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.search_popup_item, null);
        }
        TextView nameTextView = (TextView) convertView.findViewById(R.id.tv_name);
        nameTextView.setText(getItem(position));
        convertView.setTag(getItem(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new MyFilter();
        }
        return mFilter;
    }

    private class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
        }
    }
}
