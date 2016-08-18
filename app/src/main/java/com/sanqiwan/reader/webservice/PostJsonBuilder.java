package com.sanqiwan.reader.webservice;

import com.sanqiwan.reader.model.CommentInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-24
 * Time: 下午1:09
 * To change this template use File | Settings | File Templates.
 */
public class PostJsonBuilder {
    private static final String USER_ID =  "user_id";
    private static final String BOOK_ID = "book_id";
    private static final String CONTENT = "content";
    private static final String TIME = "time";


    public String buildCommentJson(CommentInfo commentInfo) {
        if (commentInfo == null) {
            return null;
        }
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_ID, commentInfo.getUserId());
            jsonObject.put(BOOK_ID, commentInfo.getBookId());
            jsonObject.put(CONTENT, commentInfo.getContent());
            jsonObject.put(TIME, commentInfo.getTime());

            jsonArray.put(jsonObject);
            return jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
