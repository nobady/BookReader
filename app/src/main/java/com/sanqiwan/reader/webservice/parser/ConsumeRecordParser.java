package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.model.ConsumeRecord;
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
public class ConsumeRecordParser extends BaseXmlParser implements IParser<ConsumeRecord> {

    private static final String ENCODING = "UTF-8";
    private static final String TAG_DATA = "data";
    private static final String TAG_ITEM = "consume";
    private static final String TAG_BOOK_NAME = "bookName";
    private static final String TAG_CHAPTER = "chapter";
    private static final String TAG_COVER = "cover";
    private static final String TAG_TIME = "time";
    private static final String TAG_AMOUNT = "amount";
    private static final String LOG_TAG = "ConsumeRecordParser";

    @Override
    public ConsumeRecord parse(InputStream inputStream) {
        List<ConsumeRecord> consumeRecordsList = parseList(inputStream);
        return consumeRecordsList.size() > 0 ? consumeRecordsList.get(0) : null;
    }

    @Override
    public List<ConsumeRecord> parseList(InputStream inputStream) {
        List<ConsumeRecord> consumeRecordsList = new ArrayList<ConsumeRecord>();
        try {
            XmlPullParser parser = newXmlPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, ENCODING);
            parser.nextTag();
            consumeRecordsList = readData(parser);
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (IOException exception) {
            Log.e(LOG_TAG, exception.getMessage());
        }
        return consumeRecordsList;
    }

    private List<ConsumeRecord> readData(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);

        List<ConsumeRecord> consumeRecordsList = new ArrayList<ConsumeRecord>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, name);
            if (name.equals(TAG_ITEM)) {
                ConsumeRecord item = readItem(parser);
                consumeRecordsList.add(item);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_DATA);
        return consumeRecordsList;
    }

    private ConsumeRecord readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_ITEM);
        ConsumeRecord item = new ConsumeRecord();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TAG_BOOK_NAME)) {
                item.setBookName(readText(parser));
            } else if (name.equals(TAG_CHAPTER)) {
                item.setChapter(readText(parser));
            }else if (name.equals(TAG_COVER)) {
                item.setCover(readText(parser));
            } else if (name.equals(TAG_TIME)) {
                item.setTime(readLong(parser));
            } else if (name.equals(TAG_AMOUNT)) {
                item.setAmount(readInt(parser));
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, TAG_ITEM);
        return item;
    }
}
