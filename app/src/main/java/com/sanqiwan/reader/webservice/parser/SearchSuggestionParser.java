package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.webservice.IParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/25/13
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchSuggestionParser extends BaseXmlParser implements IParser<String> {

    private static final String ENCODING = "UTF-8";
    private static final String TAG_DATA = "data";
    private static final String TAG_ITEM = "item";
    private static final String TAG_BOOK_NAME = "bookname";
    private static final String LOG_TAG = "SuggestionParser";

    @Override
    public String parse(InputStream inputStream) {
        List<String> bookItemList = parseList(inputStream);
        return bookItemList.size() > 0 ? bookItemList.get(0) : null;
    }

    @Override
    public List<String> parseList(InputStream inputStream) {
        try {
            XmlPullParser parser = newXmlPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, ENCODING);
            parser.nextTag();
            return readData(parser);
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (IOException exception) {
            Log.e(LOG_TAG, exception.getMessage());
        }
        return null;
    }

    private List<String> readData(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);

        List<String> suggestionList = new ArrayList<String>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, name);
            if (name.equals(TAG_ITEM)) {
                String item = readItem(parser);
                suggestionList.add(item);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_DATA);
        return suggestionList;
    }

    private String readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_ITEM);
        String item = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TAG_BOOK_NAME)) {
                item = readText(parser);
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, TAG_ITEM);
        return item;
    }
}
