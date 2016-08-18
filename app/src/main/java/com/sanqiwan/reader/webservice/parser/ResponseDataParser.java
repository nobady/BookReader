package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.model.PaymentResponseInfo;
import com.sanqiwan.reader.webservice.IParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-10-16
 * Time: 下午2:39
 * To change this template use File | Settings | File Templates.
 */
public class ResponseDataParser extends BaseXmlParser implements IParser<PaymentResponseInfo> {

    private static final String LOG_TAG = "ResponseDataParser";
    private static final String TAG_CODE = "code";
    private static final String TAG_MSG = "msg";
    private static final String TAG_ORDER = "orderInfo";
    private static final String TAG_DATA = "data";
    private static final String ENCODING = "utf-8";
    private static final String ORDER_INFO_ERROR = "0";

    @Override
    public PaymentResponseInfo parse(InputStream inputStream) {
        try {
            XmlPullParser xmlPullParser = newXmlPullParser();
            xmlPullParser.setInput(inputStream, ENCODING);
            xmlPullParser.nextTag();
            return readResponseData(xmlPullParser);
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException exception) {
            Log.e(LOG_TAG, exception.getMessage());
            exception.printStackTrace();
        }

        return null;
    }

    private PaymentResponseInfo readResponseData(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TAG_DATA);

        Log.d(LOG_TAG, "" + parser.getName());
        PaymentResponseInfo responseInfo = new PaymentResponseInfo();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(LOG_TAG, "" + name);
            if (name.equals(TAG_CODE)) {
                responseInfo.setIsSuccess(readInt(parser) == 1 ? true : false);
            } else if (name.equals(TAG_ORDER)) {
                responseInfo.setOrderInfo(readText(parser));
                responseInfo.setIsSuccess(ORDER_INFO_ERROR.equals(responseInfo.getOrderInfo()) ? false : true);
            } else if (name.equals(TAG_MSG)) {
                responseInfo.setMsg(readText(parser));
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, TAG_DATA);
        return responseInfo;
    }


    @Override
    public List<PaymentResponseInfo> parseList(InputStream inputStream) {
        return null;
    }
}
