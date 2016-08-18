package com.sanqiwan.reader.webservice;

import com.sanqiwan.reader.model.*;
import com.sanqiwan.reader.payment.NullPostDataException;
import com.sanqiwan.reader.webservice.parser.*;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;
import org.geometerplus.zlibrary.core.network.ZLNetworkManager;
import org.geometerplus.zlibrary.core.network.ZLNetworkRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/25/13
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class BookWebService {

    public final static String KEY_GUDAI = "gd";
    public final static String KEY_DUSHI = "ds";
    public final static String KEY_CHUANYUE = "cy";
    public final static String KEY_XUANHUAN = "xh";
    public final static String TYPE_MONTH = "m";
    public final static String TYPE_WEEK = "w";
    private static final String COMMENT_PARAM = "comment";


    private UriBuilder mUriBuilder;
    private PostJsonBuilder mPostJsonBuilder;
    private HttpRequester<BookItem> mHttpRequester;


    public BookWebService() {
        mUriBuilder = new UriBuilder(new WebServiceConfig());
        mPostJsonBuilder = new PostJsonBuilder();
        mHttpRequester = new HttpRequester<BookItem>();
    }

    public Chapter getChapter(long bookId, long chapterId) throws ZLNetworkException {
        String url = mUriBuilder.buildUriForGetChapter(bookId, chapterId);
        HttpRequester<Chapter> httpRequester = new HttpRequester<Chapter>();
        Chapter chapter = httpRequester.request(url, new ChapterParser());
        if (chapter != null) {
            chapter.setBookId(bookId);
        }
        return chapter;
    }

    public TOC getTOC(long bookId) throws ZLNetworkException {
        TOC toc = getTOC(bookId, -1);
        return toc;
    }

    public TOC getTOC(long bookId, int page) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetTOC(bookId, page);
        HttpRequester<TOC> httpRequester = new HttpRequester<TOC>();
        TOC toc = httpRequester.request(url, new TOCParser());
        if (toc != null) {
            toc.setBookId(bookId);
        }
        return toc;
    }

    public BooksUpdateChapter getBooksUpdateChapterId(long bookId, long chapterId) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetBooksUpdate(bookId, chapterId);
        HttpRequester<BooksUpdateChapter> httpRequester = new HttpRequester<BooksUpdateChapter>();
        BooksUpdateChapter booksUpdateChapter = httpRequester.request(url, new BooksUpdateParser());
        return booksUpdateChapter;
    }

    public BookDetail getBookDetail(long bookId) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetBookInfo(bookId);
        HttpRequester<BookDetail> httpRequester = new HttpRequester<BookDetail>();
        BookDetail bookDetail = httpRequester.request(url, new BookDetailParser());
        return bookDetail;
    }

    public List<BookItem> getNewTopBookItems(String key, String type, int nowpage) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetNewTopBooks(key, type, nowpage);
        List<BookItem> result = mHttpRequester.requestList(url, new BookItemParser());
        return result;
    }

    public List<BookItem> getNewTopBookItems(String key, String type) throws ZLNetworkException {
        return getNewTopBookItems(key, type, 1);
    }

    public List<BookItem> getNewTopBookItemsByCategoryId(int categoryId, String type) throws ZLNetworkException {
        return getNewTopBookItemsByCategoryId(categoryId, type, 1);
    }

    public List<BookItem> getNewTopBookItemsByCategoryId(int categoryId, String type, int nowpage) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetNewTopBooksByCategoryId(categoryId, type, nowpage);
        List<BookItem> result = mHttpRequester.requestList(url, new BookItemParser());
        return result;
    }

    public List<BookItem> getNewUpdateBooks(String key, int pageNum) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetNewUpdateBooks(key, pageNum);
        List<BookItem> newUpdateBooks = mHttpRequester.requestList(url, new BookItemParser());
        return newUpdateBooks;
    }

    public List<BookItem> getNewUpdateBooksByCategoryId(int categoryId, int pageNum) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetNewUpdateBooksByCategoryId(categoryId, pageNum);
        List<BookItem> newUpdateBooks = mHttpRequester.requestList(url, new BookItemParser());
        return newUpdateBooks;
    }

    public List<BookItem> getSearchItems(String key) throws ZLNetworkException {
        return getSearchItems(key, 1);
    }

    public List<BookItem> getSearchItems(String key, int nowpage) throws ZLNetworkException {
        String url = mUriBuilder.buildUriForGetSearchBooks(key, nowpage);
        List<BookItem> result = mHttpRequester.requestList(url, new BookItemParser());
        return result;
    }

    public List<BookItem> getBookList(int cateId, int scenId, int nowpage) throws ZLNetworkException {
        String url = mUriBuilder.buildUriForGetBooks(cateId, scenId, nowpage);
        return mHttpRequester.requestList(url, new BookItemParser());
    }

    public List<BookItem> getBookList(int cateId, int scenId) throws ZLNetworkException {
        return getBookList(cateId, scenId, 1);
    }

    public Boolean isSubscriptionOfVip(long bookId, long chapterId, long userId, String cert) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetVipChapterResult(bookId, chapterId, userId, cert);
        HttpRequester<Boolean> httpRequester = new HttpRequester<Boolean>();
        return httpRequester.request(url, new SubscriptionParser());
    }

    public Boolean getVIPChapterOrder(long bookId, String chapterList, int userId, String cert) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetVIPChapterOrder(bookId, chapterList, userId, cert);
        HttpRequester<Boolean> httpRequester = new HttpRequester<Boolean>();
        return httpRequester.request(url, new SubscriptionParser());
    }

    public List<VipVolumeItem> getBuyVIPChapterList(long bookId, int userId, String cert) throws ZLNetworkException {
        String url = mUriBuilder.buliderUriForGetBuyVIPChapterList(bookId, userId, cert);
        HttpRequester<VipVolumeItem> httpRequester = new HttpRequester<VipVolumeItem>();
        return httpRequester.requestList(url, new VipVolumeParser());
    }

    public List<CommentInfo> getCommentListBySinceId(long bookId, long since, int page) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetCommentBySince(bookId, since, page);
        HttpRequester<CommentInfo> httpRequester = new HttpRequester<CommentInfo>();
        return httpRequester.requestList(url, new CommentParser());
    }

    public List<CommentInfo> getCommentListByMaxId(long bookId, long max, int page) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetCommentByMax(bookId, max, page);
        HttpRequester<CommentInfo> httpRequester = new HttpRequester<CommentInfo>();
        return httpRequester.requestList(url, new CommentParser());
    }

    public List<CommentInfo> getLatestComment(long bookId, int commentsTop) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetLatestComment(bookId, commentsTop);
        HttpRequester<CommentInfo> httpRequester = new HttpRequester<CommentInfo>();
        return httpRequester.requestList(url, new CommentParser());
    }

    public boolean sendComment(CommentInfo commentInfo) throws ZLNetworkException, NullPostDataException {
        String url = mUriBuilder.builderUriForSendComment();
        String postData = mPostJsonBuilder.buildCommentJson(commentInfo);
        if (postData == null) {
            throw new NullPostDataException("postData is null");
        }
        PaymentResponseInfo responseInfo = sendData(url, COMMENT_PARAM, postData);
        if (responseInfo == null) {
            return false;
        }
        return responseInfo.isSuccess();
    }

    public TOC getLatestChapter(long bookId, int pageSize) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetLatestChapter(bookId, pageSize);
        HttpRequester<TOC> httpRequester = new HttpRequester<TOC>();
        TOC toc = httpRequester.request(url, new TOCParser());
        if (toc != null) {
            toc.setBookId(bookId);
        }
        return toc;
    }

    public List<String> getSearchSuggestion(String key) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetSearchSuggestion(key);
        HttpRequester<String> httpRequester = new HttpRequester<String>();
        return httpRequester.requestList(url, new SearchSuggestionParser());
    }

    public List<BookItem> getHotReadBooks(int pageNum) throws ZLNetworkException{
        String url = mUriBuilder.builderUriForGetHotReadBooks(pageNum);
        HttpRequester<BookItem> httpRequester = new HttpRequester<BookItem>();
        return httpRequester.requestList(url, new BookItemParser());
    }

    public HotRecommendList getHotRecommendList(String type) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetHotRecommenBooks(type);
        HttpRequester<HotRecommendList> httpRequester = new HttpRequester<HotRecommendList>();
        return httpRequester.request(url, new HotRecommendListParser());
    }

    public List<BookItem> getNewFinishBooks(int pageNum) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetNewFinishBook(pageNum);
        HttpRequester<BookItem>httpRequester = new HttpRequester<BookItem>();
        return httpRequester.requestList(url, new BookItemParser());
    }

    public List<BookItem> getNewPotentialBooks(int pageNum) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetNewPotentialBooks(pageNum);
        HttpRequester<BookItem>httpRequester = new HttpRequester<BookItem>();
        return httpRequester.requestList(url, new BookItemParser());
    }

    private PaymentResponseInfo sendData(String url, String postParam, String postData) throws ZLNetworkException {
        final HttpResult<PaymentResponseInfo> httpResult = new HttpResult<PaymentResponseInfo>();
        ZLNetworkRequest request = new ZLNetworkRequest(url, false) {
            @Override
            public void handleStream(InputStream inputStream, int length) throws IOException, ZLNetworkException {
                ResponseDataParser parser = new ResponseDataParser();
                httpResult.setResult(parser.parse(inputStream));
            }

            @Override
            public void doAfter(boolean success) throws ZLNetworkException {
            }
        };
        request.addPostParameter(postParam, postData);
        ZLNetworkManager.Instance().perform(request);
        return httpResult.getResult();
    }
}
