package com.sanqiwan.reader.data;

import com.sanqiwan.reader.AppContext;
import org.geometerplus.android.fbreader.libraryService.SQLiteBooksDatabase;
import org.geometerplus.fbreader.Paths;
import org.geometerplus.fbreader.book.*;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.text.view.ZLTextPosition;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/22/13
 * Time: 11:58 AM
 */
public class BookManager extends AbstractBookCollection {
    private static volatile BookManager sInstance;
    private BookCollection mBookCollection;

    public static BookManager getInstance() {
        if (sInstance == null) {
            synchronized (BookManager.class) {
                if (sInstance == null) {
                    sInstance = new BookManager();
                }
            }
        }
        return sInstance;
    }

    private BookManager() {
        mBookCollection = new BookCollection(SQLiteBooksDatabase.Instance(AppContext.getInstance()),
                Collections.singletonList(Paths.BooksDirectoryOption().getValue()));
    }

    @Override
    public Status status() {
        return mBookCollection.status();
    }

    @Override
    public int size() {
        return mBookCollection.size();
    }

    @Override
    public List<Book> books(BookQuery query) {
        return mBookCollection.books(query);
    }

    @Override
    public boolean hasBooks(Filter filter) {
        return mBookCollection.hasBooks(filter);
    }

    @Override
    public List<String> titles(BookQuery query) {
        return mBookCollection.titles(query);
    }

    @Override
    public List<Book> recentBooks() {
        return mBookCollection.recentBooks();
    }

    @Override
    public Book getRecentBook(int index) {
        return mBookCollection.getRecentBook(index);
    }

    @Override
    public void addBookToRecentList(Book book) {
        mBookCollection.addBookToRecentList(book);
    }

    @Override
    public Book getBookByFile(ZLFile file) {
        return mBookCollection.getBookByFile(file);
    }

    @Override
    public Book getBookById(long id) {
        return mBookCollection.getBookById(id);
    }

    @Override
    public Book getBookByUid(UID uid) {
        return mBookCollection.getBookByUid(uid);
    }

    @Override
    public List<String> labels() {
        return mBookCollection.labels();
    }

    @Override
    public List<Author> authors() {
        return mBookCollection.authors();
    }

    @Override
    public boolean hasSeries() {
        return mBookCollection.hasSeries();
    }

    @Override
    public List<String> series() {
        return mBookCollection.series();
    }

    @Override
    public List<Tag> tags() {
        return mBookCollection.tags();
    }

    @Override
    public List<String> firstTitleLetters() {
        return mBookCollection.firstTitleLetters();
    }

    @Override
    public boolean saveBook(Book book, boolean force) {
        return mBookCollection.saveBook(book, force);
    }

    @Override
    public void removeBook(Book book, boolean deleteFromDisk) {
        mBookCollection.removeBook(book, deleteFromDisk);
    }

    @Override
    public ZLTextPosition getStoredPosition(long bookId) {
        return mBookCollection.getStoredPosition(bookId);
    }

    @Override
    public void storePosition(long bookId, ZLTextPosition position) {
        mBookCollection.storePosition(bookId, position);
    }

    @Override
    public boolean isHyperlinkVisited(Book book, String linkId) {
        return mBookCollection.isHyperlinkVisited(book, linkId);
    }

    @Override
    public void markHyperlinkAsVisited(Book book, String linkId) {
        mBookCollection.markHyperlinkAsVisited(book, linkId);
    }

    @Override
    public List<Bookmark> bookmarks(BookmarkQuery query) {
        return mBookCollection.bookmarks(query);
    }

    @Override
    public void saveBookmark(Bookmark bookmark) {
        mBookCollection.saveBookmark(bookmark);
    }

    @Override
    public void deleteBookmark(Bookmark bookmark) {
        mBookCollection.deleteBookmark(bookmark);
    }

    @Override
    public HighlightingStyle getHighlightingStyle(int styleId) {
        return mBookCollection.getHighlightingStyle(styleId);
    }

    @Override
    public List<HighlightingStyle> highlightingStyles() {
        return mBookCollection.highlightingStyles();
    }

    @Override
    public void saveHighlightingStyle(HighlightingStyle style) {
        mBookCollection.saveHighlightingStyle(style);
    }
}
