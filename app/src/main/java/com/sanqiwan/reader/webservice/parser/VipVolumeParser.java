package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.model.VipVolumeItem;
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
 * User: jwb
 * Date: 13-11-8
 * Time: 下午5:08
 * To change this template use File | Settings | File Templates.
 */
public class VipVolumeParser extends BaseXmlParser implements IParser<VipVolumeItem> {

    private static final String LOG_TAG = "vipvolumeParser";
    private static final String TAG_DATA = "data";
    private static final String TAG_ITEM = "item";
    private static final String TAG_CHAPTER_ID = "chapterid";
    private static final String TAG_TITLE = "title";
    private static final String TAG_PRICE = "price";
    private static final String ENCODING = "utf-8";

    @Override
    public VipVolumeItem parse(InputStream inputStream) {
        List<VipVolumeItem> vipVolumeItemList = parseList(inputStream);
        return vipVolumeItemList.size() > 0 ? vipVolumeItemList.get(0) : null;
    }

    @Override
    public List<VipVolumeItem> parseList(InputStream inputStream) {
        List<VipVolumeItem> vipVolumeItemList = new ArrayList<VipVolumeItem>();
        try {
            XmlPullParser parser = newXmlPullParser();
            parser.setInput(inputStream, ENCODING);
            parser.nextTag();
            vipVolumeItemList = readData(parser);
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (IOException exception) {
            Log.e(LOG_TAG, exception.getMessage());
        }
        return vipVolumeItemList;
    }

    private List<VipVolumeItem> readData(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);
        List<VipVolumeItem> vipVolumeItemList = new ArrayList<VipVolumeItem>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, name);
            if (name.equals(TAG_ITEM)) {
                VipVolumeItem vipVolumeItem = readItem(parser);
                vipVolumeItemList.add(vipVolumeItem);
            } else {
                skip(parser);
            }
        }
        return vipVolumeItemList;
    }

    private VipVolumeItem readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, TAG_ITEM);
        VipVolumeItem vipVolumeItem = new VipVolumeItem();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TAG_CHAPTER_ID)) {
                vipVolumeItem.setChapterId(readLong(parser));
            } else if (name.equals(TAG_TITLE)) {
                vipVolumeItem.setChapterName(readText(parser));
            } else if (name.equals(TAG_PRICE)) {
                vipVolumeItem.setPrice(readInt(parser));
            } else {
                skip(parser);
            }
        }
        return vipVolumeItem;
    }
}
