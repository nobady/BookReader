package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.model.BookItem;
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
public class BookItemParser extends BaseXmlParser implements IParser<BookItem> {

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
    private static final String LOG_TAG = "NewTopBookItemParser";
    private static final int FINISH = 1;

    @Override
    public BookItem parse(InputStream inputStream) {
        List<BookItem> bookItemList = parseList(inputStream);
        return bookItemList.size() > 0 ? bookItemList.get(0) : null;
    }

    @Override
    public List<BookItem> parseList(InputStream inputStream) {
        List<BookItem> bookItemList = new ArrayList<BookItem>();
        try {
            XmlPullParser parser = newXmlPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, ENCODING);
            parser.nextTag();
            bookItemList = readData(parser);
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (IOException exception) {
            Log.e(LOG_TAG, exception.getMessage());
        }
        return bookItemList;
    }

    private List<BookItem> readData(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);

        List<BookItem> bookItemList = new ArrayList<BookItem>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, name);
            if (name.equals(TAG_ITEM)) {
                BookItem item = readItem(parser);
                bookItemList.add(item);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_DATA);
        return bookItemList;
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
}
