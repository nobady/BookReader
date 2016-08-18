package com.sanqiwan.reader.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.sanqiwan.reader.model.Topic;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Sam
 * Date: 13-8-1
 * Time: 下午4:34
 */
public class TopicManager {
    private final static String LOG_TAG = "TopicManager";
    private Context mContext;

    public TopicManager(Context context) {
        mContext = context;
    }

    public void addTopic(Topic topic) {
        List<Long> bookids = topic.getBookList();
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int x = 0; x < bookids.size(); x++) {
            builder.append(bookids.get(x));
            if (x == bookids.size() - 1) {
                builder.append("]");
            } else {
                builder.append(",");
            }
        }
        ContentValues values = new ContentValues();
        values.put(TopicTable.COL_TOPIC_ID, topic.getId());
        values.put(TopicTable.COL_TOPIC_TITLE, topic.getTitle());
        values.put(TopicTable.COL_TOPIC_PICURL, topic.getPictureUrl());
        values.put(TopicTable.COL_TOPIC_CREATETIME, topic.getRefreshTime());
        values.put(TopicTable.COL_TOPIC_DESCRIBE, topic.getDescribeText());
        values.put(TopicTable.COL_TOPIC_BOOKIDS, topic.getJSONArrayStringBooklist());
        values.put(TopicTable.COL_TOPIC_BANNER_URL, topic.getBannerUrl());

        mContext.getContentResolver().insert(ReaderUris.TOPIC_URI, values);
    }

    public void addTopicList(List<Topic> topicList) {
        for (Topic topic : topicList) {
            addTopic(topic);
        }
    }

    public Topic getTopicById(int id) {
        Cursor cursor = mContext.getContentResolver().query(ReaderUris.TOPIC_URI, null,
                TopicTable.COL_TOPIC_ID + "=?", new String[]{String.valueOf(id)}, null);
        if (cursor.moveToNext()) {
            Topic topic = new Topic();
            topic.setId(cursor.getInt(cursor.getColumnIndex(TopicTable.COL_TOPIC_ID)));
            topic.setTitle(cursor.getString(cursor.getColumnIndex(TopicTable.COL_TOPIC_TITLE)));
            topic.setPictureUrl(cursor.getString(cursor.getColumnIndex(TopicTable.COL_TOPIC_PICURL)));
            topic.setDescribeText(cursor.getString(cursor.getColumnIndex(TopicTable.COL_TOPIC_DESCRIBE)));
            topic.setRefreshTime(cursor.getLong(cursor.getColumnIndex(TopicTable.COL_TOPIC_CREATETIME)));
            topic.setBannerUrl(cursor.getString(cursor.getColumnIndexOrThrow(TopicTable.COL_TOPIC_BANNER_URL)));
            try {
                JSONArray jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex(TopicTable.COL_TOPIC_BOOKIDS)));
                for (int i = 0; i < jsonArray.length(); i++) {
                    topic.addBook(jsonArray.getInt(i));
                }
            } catch (JSONException e) {
                Log.i(LOG_TAG, e.getMessage());
            }
            return topic;
        }
        return null;
    }

    public List<Topic> getAllTopic() {
        Cursor cursor = mContext.getContentResolver().query(ReaderUris.TOPIC_URI, null, null, null, TopicTable.COL_TOPIC_ID+" desc");
        List<Topic> topics = new ArrayList<Topic>();
        while (cursor.moveToNext()) {
            Topic topic = new Topic();
            topic.setId(cursor.getInt(cursor.getColumnIndex(TopicTable.COL_TOPIC_ID)));
            topic.setTitle(cursor.getString(cursor.getColumnIndex(TopicTable.COL_TOPIC_TITLE)));
            topic.setPictureUrl(cursor.getString(cursor.getColumnIndex(TopicTable.COL_TOPIC_PICURL)));
            topic.setDescribeText(cursor.getString(cursor.getColumnIndex(TopicTable.COL_TOPIC_DESCRIBE)));
            topic.setRefreshTime(cursor.getLong(cursor.getColumnIndex(TopicTable.COL_TOPIC_CREATETIME)));
            topic.setBannerUrl(cursor.getString(cursor.getColumnIndexOrThrow(TopicTable.COL_TOPIC_BANNER_URL)));
            try {
                JSONArray jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex(TopicTable.COL_TOPIC_BOOKIDS)));
                topic.setBookListByJSONArray(jsonArray);
            } catch (JSONException e) {
                Log.i(LOG_TAG, e.getMessage());
            }
            topics.add(topic);
        }
        return topics;
    }

    public int deleteTopic(int id) {
        return mContext.getContentResolver().delete(ReaderUris.TOPIC_URI,
                TopicTable.COL_TOPIC_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int deleteAllTopic() {
        return mContext.getContentResolver().delete(ReaderUris.TOPIC_URI, null, null);
    }

    public int updateTopic(Topic topic) {
        List<Long> bookids = topic.getBookList();
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int x = 0; x < bookids.size(); x++) {
            builder.append(bookids.get(x));
            if (x == bookids.size() - 1) {
                builder.append("]");
            } else {
                builder.append(",");
            }
        }
        ContentValues values = new ContentValues();
        values.put(TopicTable.COL_TOPIC_ID, topic.getId());
        values.put(TopicTable.COL_TOPIC_TITLE, topic.getTitle());
        values.put(TopicTable.COL_TOPIC_PICURL, topic.getPictureUrl());
        values.put(TopicTable.COL_TOPIC_CREATETIME, topic.getRefreshTime());
        values.put(TopicTable.COL_TOPIC_DESCRIBE, topic.getDescribeText());
        values.put(TopicTable.COL_TOPIC_BOOKIDS, builder.toString());
        values.put(TopicTable.COL_TOPIC_BANNER_URL, topic.getBannerUrl());
        String[] id = new String[]{String.valueOf(topic.getId())};
        String where = TopicTable.COL_TOPIC_ID + " =?";
        return mContext.getContentResolver().update(ReaderUris.TOPIC_URI, values, where, id);
    }
}
