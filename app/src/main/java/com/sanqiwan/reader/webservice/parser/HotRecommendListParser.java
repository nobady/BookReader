package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.model.BookItem;
import com.sanqiwan.reader.model.HotRecommendList;
import com.sanqiwan.reader.webservice.IParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jwb on 14-8-29.
 */
public class HotRecommendListParser extends BaseXmlParser
        implements IParser<HotRecommendList> {

    private static final String ENCODING = "UTF-8";
    private static final String TAG_DATA = "data";
    private static final String TAG_ITEM = "item";
    private static final String TAG_BOOKID = "bookid";
    private static final String TAG_BOOKNAME = "bookname";
    private static final String TAG_SCHEDULE = "finish";
    private static final String TAG_COMMENDS = "commends";
    private static final String TAG_PIC = "bookpic";
    private static final String TAG_AUTHOR = "author";
    private static final String TAG_DESCRIBE = "bookdescribe";
    private static final String TAG_COLUMNNAME = "columnname";
    private static final String LOG_TAG = "NewTopBookItemParser";
    private static final int FINISH = 1;
    private static final int HOT = 0;
    private static final int STRONG = 1;
    private static final int SELLING = 2;

    @Override
    public HotRecommendList parse(InputStream inputStream) {
        try {
            XmlPullParser xmlPullParser = newXmlPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, ENCODING);
            xmlPullParser.nextTag();
            return readHotRecommendList(xmlPullParser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private HotRecommendList readHotRecommendList(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);

        HotRecommendList hotRecommendList = new HotRecommendList();
        List<BookItem> hotBookItems = new ArrayList<BookItem>();
        List<BookItem> strongBookItems = new ArrayList<BookItem>();
        List<BookItem> sellingBookItems = new ArrayList<BookItem>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, name);
            if (name.equals(TAG_ITEM)) {
                BookItem item = readItem(parser);
                switch (item.getColumnName()) {
                    case HOT:
                        hotBookItems.add(item);
                        break;
                    case STRONG:
                        strongBookItems.add(item);
                        break;
                    case SELLING:
                        sellingBookItems.add(item);
                        break;
                    default:
                        hotBookItems.add(item);
                        break;
                }
            } else {
                skip(parser);
            }
        }
        hotRecommendList.setHotBookItems(hotBookItems);
        hotRecommendList.setStrongBookItems(strongBookItems);
        hotRecommendList.setSellingBookItems(sellingBookItems);
        parser.require(XmlPullParser.END_TAG, null, TAG_DATA);
        return hotRecommendList;
    }

    private BookItem readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_ITEM);
        BookItem item = new BookItem();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TAG_BOOKID)) {
                item.setBookId(readLong(parser));
            } else if (name.equals(TAG_BOOKNAME)) {
                item.setBookName(readText(parser));
            } else if (name.equals(TAG_SCHEDULE)) {
                boolean finish = readInt(parser) == FINISH;
                item.setIsFinish(finish);
            } else if (name.equals(TAG_COMMENDS)) {
                item.setCommendNumber(readInt(parser));
            } else if (name.equals(TAG_PIC)) {
                item.setCover(readText(parser));
            } else if (name.equals(TAG_COLUMNNAME)) {
                item.setColumnName(readInt(parser));
            } else if (name.equals(TAG_AUTHOR)) {
                item.setAuthor(readText(parser));
            } else if (name.equals(TAG_DESCRIBE)) {
                item.setDescribe(readText(parser));
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, TAG_ITEM);
        return item;
    }

    @Override
    public List<HotRecommendList> parseList(InputStream inputStream) {
        return null;
    }
}
