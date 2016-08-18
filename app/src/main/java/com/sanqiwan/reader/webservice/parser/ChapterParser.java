package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.model.Chapter;
import com.sanqiwan.reader.webservice.IParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/26/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChapterParser extends BaseXmlParser implements IParser<Chapter> {

    private static final String LOG_TAG = "ChapterParser";
    private static final String TAG_DATA = "data";
    private static final String TAG_CHAPTER_ID = "chapterid";
    private static final String TAG_IS_VIP = "isvip";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTENT = "content";
    private static final String ENCODING = "utf-8";

    @Override
    public Chapter parse(InputStream inputStream) {
        try {
            XmlPullParser xmlPullParser = newXmlPullParser();
            xmlPullParser.setInput(inputStream, ENCODING);
            xmlPullParser.nextTag();
            return readChapter(xmlPullParser);
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException exception) {
            Log.e(LOG_TAG, exception.getMessage());
            exception.printStackTrace();
        }

        return null;
    }

    private Chapter readChapter(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);

        Log.d(LOG_TAG, ""+parser.getName());

        Chapter chapter = new Chapter();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, ""+name);
            if (name.equals(TAG_CHAPTER_ID)) {
                long chapterId = readLong(parser);
                chapter.setChapterId(chapterId);
            } else if (name.equals(TAG_IS_VIP)) {
                int code = readInt(parser);
                boolean vip = code == 1;
                chapter.setVip(vip);
            } else if (name.equals(TAG_TITLE)) {
                String title = readText(parser);
                chapter.setTitle(title);
            } else if (name.equals(TAG_CONTENT)) {
                String content = readText(parser);
                chapter.setContent(content);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_DATA);

        return chapter;
    }

    @Override
    public List<Chapter> parseList(InputStream inputStream) {
        return null;
    }
}
