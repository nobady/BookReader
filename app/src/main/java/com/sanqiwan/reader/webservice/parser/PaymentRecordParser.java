package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.model.PaymentRecord;
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
public class PaymentRecordParser extends BaseXmlParser implements IParser<PaymentRecord> {

    private static final String ENCODING = "UTF-8";
    private static final String TAG_DATA = "data";
    private static final String TAG_ITEM = "pay";
    private static final String TAG_CHANNEL = "channel";
    private static final String TAG_TIME = "time";
    private static final String TAG_AMOUNT = "amount";
    private static final String LOG_TAG = "PaymentRecordParser";

    @Override
    public PaymentRecord parse(InputStream inputStream) {
        List<PaymentRecord> paymentRecordsList = parseList(inputStream);
        return paymentRecordsList.size() > 0 ? paymentRecordsList.get(0) : null;
    }

    @Override
    public List<PaymentRecord> parseList(InputStream inputStream) {
        List<PaymentRecord> paymentRecordsList = new ArrayList<PaymentRecord>();
        try {
            XmlPullParser parser = newXmlPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, ENCODING);
            parser.nextTag();
            paymentRecordsList = readData(parser);
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (IOException exception) {
            Log.e(LOG_TAG, exception.getMessage());
        }
        return paymentRecordsList;
    }

    private List<PaymentRecord> readData(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);

        List<PaymentRecord> paymentRecordsList = new ArrayList<PaymentRecord>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, name);
            if (name.equals(TAG_ITEM)) {
                PaymentRecord item = readItem(parser);
                paymentRecordsList.add(item);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_DATA);
        return paymentRecordsList;
    }

    private PaymentRecord readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_ITEM);
        PaymentRecord item = new PaymentRecord();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TAG_CHANNEL)) {
                item.setChannel(readText(parser));
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
