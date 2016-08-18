package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.Config;
import com.sanqiwan.reader.model.BooksUpdateChapter;
import com.sanqiwan.reader.model.VolumeItem;
import com.sanqiwan.reader.webservice.IParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-8-6
 * Time: 下午3:36
 * To change this template use File | Settings | File Templates.
 */
public class BooksUpdateParser extends BaseXmlParser implements IParser<BooksUpdateChapter> {
    private static final String LOG_TAG = "GetBooksUpdate";
    private static final String TAG_DATA = "data";
    private static final String TAG_ITEM = "item";
    private static final String TAG_CODE = "code";
    private static final String TAG_CHAPTERS = "chapters";
    private static final String TAG_CHAPTER = "chapter";
    private static final String TAG_CHAPTER_ID = "chapterID";
    private static final String TAG_CHAPTER_NAME = "chaptername";
    private static final String TAG_IS_VIP = "isvip";
    private static final String ENCODING = "utf-8";

    @Override
    public BooksUpdateChapter parse(InputStream inputStream) {
        try {
            XmlPullParser parser = newXmlPullParser();
            parser.setInput(inputStream, ENCODING);
            parser.nextTag();
            return readChapterId(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BooksUpdateChapter readChapterId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);

        Log.d(LOG_TAG, parser.getName());

        BooksUpdateChapter chapterIdItem = new BooksUpdateChapter();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, name);
            if (name.equals(TAG_ITEM)) {
                chapterIdItem = readItem(parser);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_DATA);

        return chapterIdItem;
    }

    private BooksUpdateChapter readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_ITEM);

        Log.d(LOG_TAG, parser.getName());

        BooksUpdateChapter chapterIdItem = new BooksUpdateChapter();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, name);
            if (name.equals(TAG_CODE)) {
                chapterIdItem.setCode(readInt(parser));
            } else if (name.equals(TAG_CHAPTERS)) {
                chapterIdItem.setItems(readChapters(parser));
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_ITEM);

        return chapterIdItem;
    }

    private List<VolumeItem> readChapters(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_CHAPTERS);

        Log.d(LOG_TAG, parser.getName());

        List<VolumeItem> items = new ArrayList<VolumeItem>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, name);
            if (name.equals(TAG_CHAPTER)) {
                VolumeItem item = readChapter(parser);
                if (!Config.SKIP_VIP_ITEM || !item.isVip()) {
                    items.add(item);
                }
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_CHAPTERS);

        return items;
    }

    private VolumeItem readChapter(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_CHAPTER);

        Log.d(LOG_TAG, parser.getName());

        VolumeItem volumeItem = new VolumeItem();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, name);
            if (name.equals(TAG_CHAPTER_NAME)) {
                String chapterName = readText(parser);
                volumeItem.setName(chapterName);
            } else if (name.equals(TAG_CHAPTER_ID)) {
                long chapterId = readLong(parser);
                volumeItem.setId(chapterId);
            } else if (name.equals(TAG_IS_VIP)) {
                int code = readInt(parser);
                boolean vip = code == 1;
                volumeItem.setVip(vip);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_CHAPTER);

        return volumeItem;
    }

    @Override
    public List<BooksUpdateChapter> parseList(InputStream inputStream) {

        return null;
    }
}
