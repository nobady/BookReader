package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.Config;
import com.sanqiwan.reader.model.TOC;
import com.sanqiwan.reader.model.Volume;
import com.sanqiwan.reader.model.VolumeItem;
import com.sanqiwan.reader.webservice.IParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/29/13
 * Time: 8:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class TOCParser extends BaseXmlParser implements IParser<TOC> {

    private static final String LOG_TAG = "ChapterParser";
    private static final String TAG_CHAPTER_ID = "chapterid";
    private static final String TAG_IS_VIP = "isvip";
    private static final String TAG_CHAPTER_NAME = "chaptername";
    private static final String TAG_DATA = "data";
    private static final String TAG_VOL = "vol";
    private static final String TAG_VOLUME_NAME = "volumename";
    private static final String TAG_URL = "url";
    private static final String TAG_ITEM = "item";
    private static final String TAG_VOLID = "volID";
    private static final String ENCODING = "utf-8";

    @Override
    public TOC parse(InputStream inputStream) {
        try {
            XmlPullParser parser = newXmlPullParser();
            parser.setInput(inputStream, ENCODING);
            parser.nextTag();
            return readTOC(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private TOC readTOC(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);

        Log.d(LOG_TAG, parser.getName());

        TOC toc = new TOC();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, name);
            if (name.equals(TAG_VOL)) {
                Volume volume = readVolume(parser);
                toc.addVolume(volume);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_DATA);

        return toc;
    }


    private Volume readVolume(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_VOL);

        Log.d(LOG_TAG, parser.getName());

        Volume volume = new Volume();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, name);
            if (name.equals(TAG_VOLUME_NAME)) {
                String volumeName = readText(parser);
                volume.setName(volumeName);
            } else if (name.equals(TAG_ITEM)) {
                VolumeItem item = readItem(parser);
                if (!Config.SKIP_VIP_ITEM || !item.isVip()) {
                    volume.addItem(item);
                }
            } else if (name.equals(TAG_VOLID)) {
                volume.setId(readLong(parser));
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_VOL);

        return volume;
    }

    private VolumeItem readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_ITEM);

        Log.d(LOG_TAG, parser.getName());

        VolumeItem volumeItem = new VolumeItem();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, name);
            if (name.equals(TAG_URL)) {
                String url = readText(parser);
                volumeItem.setUrl(url);
            } else if (name.equals(TAG_CHAPTER_NAME)) {
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

        parser.require(XmlPullParser.END_TAG, null, TAG_ITEM);

        return volumeItem;
    }

    @Override
    public List<TOC> parseList(InputStream inputStream) {
        return null;
    }
}
