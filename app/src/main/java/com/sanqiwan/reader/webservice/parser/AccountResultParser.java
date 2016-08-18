package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.model.AccountResult;
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
public class AccountResultParser extends BaseXmlParser implements IParser<AccountResult> {
    private static final String LOG_TAG = "AccountResultParser";
    private static final String ENCODING = "UTF-8";
    private static final String TAG_DATA = "data";
    private static final String TAG_CODE = "code";
    private static final String TAG_MSG = "msg";
    @Override
    public AccountResult parse(InputStream inputStream) {
        try {
            XmlPullParser xmlPullParser = newXmlPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, ENCODING);
            xmlPullParser.nextTag();
            return readAccountResult(xmlPullParser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private AccountResult readAccountResult(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);

        Log.d(LOG_TAG, "" + parser.getName());

        AccountResult result = new AccountResult();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            Log.d(LOG_TAG, "" + tagName);
            if (tagName.equals(TAG_CODE)) {
                result.setCode(readInt(parser));
            } else if(tagName.equals(TAG_MSG)) {
                result.setMessage(readText(parser));
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, TAG_DATA);
        return result;
    }

    @Override
    public List<AccountResult> parseList(InputStream inputStream) {
        return null;
    }
}
