package com.sanqiwan.reader.engine;

import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.data.BookManager;
import com.sanqiwan.reader.model.Chapter;
import com.sanqiwan.reader.util.StringUtil;
import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/27/13
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChapterReader {

    private static final String TXT_EXTENSION = ".txt";

    public void openChapter(Chapter chapter) {
        // Step 1: write the content of chapter to file
        File chapterCacheFile = getChapterCacheFile(chapter);
        StringUtil.saveStringToFile(chapter.getContent(), chapterCacheFile);

        // Step 2: create ZLFile from the chapter content file
        ZLFile chapterFile = ZLFile.createFileByPath(chapterCacheFile.getPath());

        // Step 3: create Book from ZLFile
        Book book = BookManager.getInstance().getBookByFile(chapterFile);

        // Step 4: call the FBReader activity to open the book.
        FBReader.openBookActivity(AppContext.getInstance(), book, null);
    }

    public Book createBookForChapter(Chapter chapter) {
        // Step 1: write the content of chapter to file
        File chapterCacheFile = getChapterCacheFile(chapter);
        StringUtil.saveStringToFile(chapter.getContent(), chapterCacheFile);

        // Step 2: create ZLFile from the chapter content file
        ZLFile chapterFile = ZLFile.createFileByPath(chapterCacheFile.getPath());

        // Step 3: create Book from ZLFile
        Book book = BookManager.getInstance().getBookByFile(chapterFile);
        book.setTitle(chapter.getTitle());
        return book;
    }

    public ZLFile createFileForChapter(Chapter chapter) {
        // Step 1: write the content of chapter to file
        File chapterCacheFile = getChapterCacheFile(chapter);
        StringUtil.saveStringToFile(chapter.getContent(), chapterCacheFile);

        // Step 2: create ZLFile from the chapter content file
        ZLFile chapterFile = ZLFile.createFileByPath(chapterCacheFile.getPath());
        return chapterFile;
    }


    private File getChapterCacheFile(Chapter chapter) {
        File cacheDir = AppContext.getInstance().getCacheDir();
        File bookDir = new File(cacheDir, String.valueOf(chapter.getBookId()));
        File chapterFile = new File(bookDir, String.valueOf(chapter.getChapterId()) + TXT_EXTENSION);
        return chapterFile;
    }

}
