package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.model.BookDetail;
import com.sanqiwan.reader.webservice.IParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/25/13
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class BookDetailParser extends BaseXmlParser implements IParser<BookDetail> {

    private static final String LOG_TAG = "BookDetailParser";
    private static final String ENCODING = "UTF-8";
    private static final String TAG_DATA = "data";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_SCENARIO = "scenario";
    private static final String KEY_BOOK_ID = "bookid";
    private static final String KEY_BOOK_NAME = "bookname";
    private static final String KEY_PIC = "bookpic";
    private static final String KEY_ZZJS = "zzjs";
    private static final String KEY_AUTHOR_ID = "authorid";
    private static final String KEY_AUTHOR_NAME = "authorname";
    private static final String KEY_CREATE_TIME = "createtime";
    private static final String KEY_BOOK_SIZE = "bksize";
    private static final String KEY_IS_VIP = "isvip";
    private static final String KEY_FINISH = "finish";
    private static final String KEY_WEEK_VISIT = "weekvisit";
    private static final String KEY_MONTH_VISIT = "monthvisit";
    private static final String KEY_ALL_VISIT = "allvisit";
    private static final String KEY_TAGS= "tags";
    private static final String KEY_AUTHORIZATION = "authorization";
    private static final int FINISH = 1;
    private static final int AUTHORIZATION = 1;

    @Override
    public BookDetail parse(InputStream inputStream) {
        try {
            XmlPullParser xmlPullParser = newXmlPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, ENCODING);
            xmlPullParser.nextTag();
            return readBookDetail(xmlPullParser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private BookDetail readBookDetail(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);

        Log.d(LOG_TAG, "" + parser.getName());

        BookDetail bookDetail = new BookDetail();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            Log.d(LOG_TAG, "" + tagName);
            if (tagName.equals(KEY_CATEGORY)) {
                bookDetail.setCategory(parser.nextText().trim());
            } else if (tagName.equals(KEY_SCENARIO)) {
                bookDetail.setScenario(parser.nextText().trim());
            } else if (tagName.equals(KEY_BOOK_ID)) {
                long id = readLong(parser);
                bookDetail.setBookId(id);
            } else if (tagName.equals(KEY_BOOK_NAME)) {
                bookDetail.setBookName(parser.nextText().trim());
            } else if (tagName.equals(KEY_PIC)) {
                bookDetail.setBookCover(parser.nextText().trim());
            } else if (tagName.equals(KEY_ZZJS)) {
                bookDetail.setDescription(readText(parser));
            } else if (tagName.equals(KEY_AUTHOR_ID)) {
                bookDetail.setAuthorId(readLong(parser));
            } else if (tagName.equals(KEY_AUTHOR_NAME)) {
                bookDetail.setAuthorName(parser.nextText().trim());
            } else if (tagName.equals(KEY_CREATE_TIME)) {
                bookDetail.setCreateTime(parser.nextText().trim());
            } else if (tagName.equals(KEY_BOOK_SIZE)) {
                bookDetail.setBookSize(readInt(parser));
            } else if (tagName.equals(KEY_IS_VIP)) {
                int code = readInt(parser);
                boolean vip = code == 1;
                bookDetail.setVip(vip);
            } else if (tagName.equals(KEY_FINISH)) {
                boolean finish = readInt(parser) == FINISH;
                bookDetail.setFinish(finish);
            } else if (tagName.equals(KEY_WEEK_VISIT)) {
                bookDetail.setWeekVisit(readInt(parser));
            } else if (tagName.equals(KEY_MONTH_VISIT)) {
                bookDetail.setMonthVisit(readInt(parser));
            } else if (tagName.equals(KEY_ALL_VISIT)) {
                bookDetail.setAllVisit(readInt(parser));
            } else if (tagName.equals(KEY_TAGS)) {
                bookDetail.setTags(parser.nextText().trim());
            } else if (tagName.equals(KEY_AUTHORIZATION)) {
                boolean authorization = readInt(parser) == AUTHORIZATION;
                bookDetail.setAuthorization(authorization);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_DATA);

        return bookDetail;
    }

    @Override
    public List<BookDetail> parseList(InputStream inputStream) {

        return null;
    }
}
