package com.sanqiwan.reader.webservice.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/14/13
 * Time: 10:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseXmlParser {

    protected XmlPullParser newXmlPullParser() throws XmlPullParserException {
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        return parser;
    }

    protected String readText(XmlPullParser parser) throws IOException, XmlPullParserException {

        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText().trim();
            parser.nextTag();
        }

        return result;
    }

    protected int readInt(XmlPullParser parser) throws IOException, XmlPullParserException {
        String text = readText(parser);
        return Integer.parseInt(text);
    }

    protected float readFloat(XmlPullParser parser) throws IOException, XmlPullParserException {
        String text = readText(parser);
        return Float.parseFloat(text);
    }

    protected long readLong(XmlPullParser parser) throws IOException, XmlPullParserException {
        String text = readText(parser);
        return Long.parseLong(text);
    }

    protected void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
