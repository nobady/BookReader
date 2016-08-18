package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.model.UserInfo;
import com.sanqiwan.reader.webservice.IParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/6/13
 * Time: 11:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserInfoParser extends BaseXmlParser implements IParser<UserInfo> {
    private static final String LOG_TAG = "AccountResultParser";
    private static final String ENCODING = "UTF-8";
    private static final String TAG_DATA = "data";
    private static final String TAG_USER_ID = "userId";
    private static final String TAG_USER_NAME = "userName";
    private static final String TAG_USER_VIP = "userVIP";
    private static final String TAG_USER_MONEY = "userMoney";
    private static final String TAG_USER_EMAIL = "userEmail";
    private static final String TAG_USER_PHONE = "phoneNum";
    private static final String TAG_INTEGRATION = "integration";
    private static final String TAG_AVATAR = "avatar";

    @Override
    public UserInfo parse(InputStream inputStream) {
        try {
            XmlPullParser xmlPullParser = newXmlPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, ENCODING);
            xmlPullParser.nextTag();
            return readUserInfo(xmlPullParser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private UserInfo readUserInfo(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);

        Log.d(LOG_TAG, "" + parser.getName());

        UserInfo userInfo = new UserInfo();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            Log.d(LOG_TAG, "" + tagName);
            if (tagName.equals(TAG_USER_ID)) {
                userInfo.setUid(readInt(parser));
            } else if (tagName.equals(TAG_USER_NAME)) {
                userInfo.setUserName(readText(parser));
            } else if (tagName.equals(TAG_USER_MONEY)) {
                userInfo.setUserMoney(readInt(parser));
            } else if (tagName.equals(TAG_INTEGRATION)) {
                userInfo.setUserPoint(readFloat(parser));
            } else if (tagName.equals(TAG_USER_VIP)) {
                userInfo.setLevel(readText(parser));
            } else if (tagName.equals(TAG_USER_EMAIL)) {
                userInfo.setEmail(readText(parser));
            } else if (tagName.equals(TAG_USER_PHONE)) {
                userInfo.setPhoneNumber(readText(parser));
            } else if (tagName.equals(TAG_AVATAR)) {
                userInfo.setAvatar(readText(parser));
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, TAG_DATA);
        return userInfo;
    }

    @Override
    public List<UserInfo> parseList(InputStream inputStream) {
        return null;
    }
}
