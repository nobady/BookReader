package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.model.RankItem;
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
public class RankItemParser extends BaseXmlParser implements IParser<RankItem> {

    private static final String ENCODING = "UTF-8";
    private static final String TAG_DATA = "data";
    private static final String TAG_ITEM = "rank";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_COVER = "cover";
    private static final String LOG_TAG = "RankItemParser";

    @Override
    public RankItem parse(InputStream inputStream) {
        List<RankItem> rankItemList = parseList(inputStream);
        return rankItemList.size() > 0 ? rankItemList.get(0) : null;
    }

    @Override
    public List<RankItem> parseList(InputStream inputStream) {
        List<RankItem> rankItemList = new ArrayList<RankItem>();
        try {
            XmlPullParser parser = newXmlPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, ENCODING);
            parser.nextTag();
            rankItemList = readData(parser);
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (IOException exception) {
            Log.e(LOG_TAG, exception.getMessage());
        }
        return rankItemList;
    }

    private List<RankItem> readData(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);

        List<RankItem> bookItemList = new ArrayList<RankItem>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TAG_ITEM)) {
                RankItem item = readItem(parser);
                bookItemList.add(item);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_DATA);
        return bookItemList;
    }

    private RankItem readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_ITEM);
        RankItem item = new RankItem();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TAG_ID)) {
                item.setId(readInt(parser));
            } else if (name.equals(TAG_NAME)) {
                item.setName(readText(parser));
            } else if (name.equals(TAG_COVER)) {
                item.setCover(readText(parser));
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, TAG_ITEM);
        return item;
    }
}
