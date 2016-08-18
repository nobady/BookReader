package com.sanqiwan.reader.data;

import com.sanqiwan.reader.model.Topic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/22/13
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class Operation {
    private List<Topic> mTopicList;

    public Operation() {
        mTopicList = new ArrayList<Topic>();
    }

    public void addTopic(Topic topic) {
        mTopicList.add(topic);
    }

    public Topic getTopic(int topicId) {
        for (Topic topic : mTopicList) {
            if (topic.getId() == topicId) {
                return topic;
            }
        }
        return null;
    }

    public List<Topic> getTopicList() {
        return mTopicList;
    }
}
