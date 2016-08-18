package com.sanqiwan.reader.engine;

import com.sanqiwan.reader.model.VipVolumeItem;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-11-6
 * Time: 上午10:38
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionManager {

    private static SubscriptionManager sInstance;
    private BookWebService mBookWebService;
    private static final String INTERVAL = "|";

    private SubscriptionManager() {
        mBookWebService = new BookWebService();
    }

    public static SubscriptionManager getsInstance() {
        if (sInstance == null) {
            synchronized (UserManager.class) {
                if (sInstance == null) {
                    sInstance = new SubscriptionManager();
                }
            }
        }
        return sInstance;
    }

    public boolean isSubscriptionOfVip(long bookId, long chapterId, long userId) throws ZLNetworkException {
        return mBookWebService.isSubscriptionOfVip(bookId, chapterId, userId, UserManager.getInstance().generateCert());
    }


    public Boolean getVipChapterOrder(long bookId, long[] chapterIdList) throws ZLNetworkException {
        if (chapterIdList != null) {
            StringBuilder chapterIdListBuilder = new StringBuilder();
            for (int i = 0; i < chapterIdList.length; i++) {
                chapterIdListBuilder.append(chapterIdList[i]);
                chapterIdListBuilder.append(INTERVAL);
            }
            return mBookWebService.getVIPChapterOrder(bookId, chapterIdListBuilder.toString(), UserManager.getInstance().getUserId(), UserManager.getInstance().generateCert());
        }
        return false;
    }

    public List<VipVolumeItem> getBuyVIPChapterList(long bookId, int userId) throws ZLNetworkException {
        return mBookWebService.getBuyVIPChapterList(bookId, userId, UserManager.getInstance().generateCert());
    }
}
