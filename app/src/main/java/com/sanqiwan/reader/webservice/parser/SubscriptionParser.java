package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.webservice.IParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-11-5
 * Time: 下午3:25
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionParser extends BaseXmlParser implements IParser<Boolean> {

    private static final String LOG_TAG = "SubscriptionParser";
    private static final String TAG_DATA = "data";
    private static final String TAG_CODE = "code";
    private static final String ENCODING = "utf-8";

    @Override
    public Boolean parse(InputStream inputStream) {
        try {
            XmlPullParser parser = newXmlPullParser();
            parser.setInput(inputStream, ENCODING);
            parser.nextTag();
            return readSubscription(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Boolean readSubscription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);

        Log.d(LOG_TAG, parser.getName());
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, name);
            if (name.equals(TAG_CODE)) {
                int code = readInt(parser);
                if (code == 0) {
                    return false;
                } else if (code == 1) {
                    return true;
                }
                return false;
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_DATA);
        return false;
    }
    @Override
    public List<Boolean> parseList(InputStream inputStream) {
        return null;
    }
}
