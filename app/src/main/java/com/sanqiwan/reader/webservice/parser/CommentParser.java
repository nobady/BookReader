package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.model.CommentInfo;
import com.sanqiwan.reader.util.SimpleDateFormat;
import com.sanqiwan.reader.util.StringUtil;
import com.sanqiwan.reader.webservice.IParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-24
 * Time: 上午11:08
 * To change this template use File | Settings | File Templates.
 */
public class CommentParser extends BaseXmlParser implements IParser<CommentInfo> {
    private static final String LOG_TAG = "CommentParser";
    private static final String ENCODING = "utf-8";
    private static final String TAG_DATA = "data";
    private static final String TAG_ITEM = "item";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_USER_NAME = "username";
    private static final String TAG_USER_ID = "buseid";
    private static final String TAG_IMG = "img";
    private static final String TAG_TIME = "createtime";
    private static final String TAG_AVATAR_URL = "avatarurl";
    private static final String TAG_CONTENT_START = "<content>";
    private static final String TAG_CONTENT_END = "</content>";

    @Override
    public CommentInfo parse(InputStream inputStream) {
        List<CommentInfo> commentList = parseList(inputStream);
        return commentList.size() > 0 ? commentList.get(0) : null;
    }

    @Override
    public List<CommentInfo> parseList(InputStream inputStream) {
        List<CommentInfo> commentList = new ArrayList<CommentInfo>();
        try {
            XmlPullParser parser = newXmlPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, ENCODING);
            parser.nextTag();
            commentList = readData(parser);
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (IOException exception) {
            Log.e(LOG_TAG, exception.getMessage());
        }
        return commentList;
    }

    private List<CommentInfo> readData(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);

        List<CommentInfo> commentList = new ArrayList<CommentInfo>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, name);
            if (name.equals(TAG_ITEM)) {
                CommentInfo item = readItem(parser);
                commentList.add(item);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_DATA);
        return commentList;
    }

    private CommentInfo readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_ITEM);
        CommentInfo item = new CommentInfo();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TAG_ID)) {
                item.setId(readLong(parser));
            } else if (name.equals(TAG_TITLE)) {
                item.setTitle(readText(parser));
            } else if (name.equals(TAG_CONTENT)) {
                String content = readContentText(parser);
                item.setContent(getParser(new ByteArrayInputStream(content.getBytes(ENCODING))));
            } else if (name.equals(TAG_USER_NAME)) {
                item.setUserName(readText(parser));
            } else if (name.equals(TAG_USER_ID)) {
                item.setUserId(readInt(parser));
            } else if (name.equals(TAG_TIME)) {
                item.setTime(SimpleDateFormat.formatUnixTimestamp("yyyy-MM-dd", readLong(parser)).toString());
            } else if (name.equals(TAG_AVATAR_URL)) {
                item.setUserImageUrl(readText(parser));
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, TAG_ITEM);
        return item;
    }

    private String readContentText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String contentResult = "";
        parser.require(XmlPullParser.START_TAG, null, TAG_CONTENT);
        if (parser.next() == XmlPullParser.TEXT) {
            contentResult += parser.getText().trim();
            parser.next();
        }
        String endTagName = parser.getName();
        while (!endTagName.equals(TAG_CONTENT)) {
            parser.next();
            endTagName = parser.getName();
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_CONTENT);
        contentResult = TAG_CONTENT_START + contentResult + TAG_CONTENT_END;
        return contentResult;
    }

    private String readContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        String contentResult = "";
        parser.require(XmlPullParser.START_TAG, null, TAG_CONTENT);

        if (parser.next() == XmlPullParser.TEXT) {
            contentResult += parser.getText().trim();
            parser.next();
        }
        String endTagName = parser.getName();
        while (!endTagName.equals(TAG_CONTENT)) {
            String name = parser.getName();
            if (name.equals(TAG_IMG)) {
                contentResult += AppContext.getInstance().getResources().getString(R.string.replace_img_tag);
            } else {
                skip(parser);
            }
            if (parser.next() == XmlPullParser.TEXT) {
                contentResult += parser.getText().trim();
                parser.next();
            }
            endTagName = parser.getName();
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_CONTENT);

        return contentResult;
    }

    private String getParser(InputStream inputStream) throws IOException, XmlPullParserException {
        XmlPullParser parser = newXmlPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(inputStream, ENCODING);
        parser.nextTag();
        return readContent(parser);
    }
}
