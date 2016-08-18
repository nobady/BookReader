package com.sanqiwan.reader.format.xbook;

import android.graphics.Bitmap;
import android.util.Log;
import com.sanqiwan.reader.model.*;
import com.sanqiwan.reader.util.StringUtil;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/8/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class XBookWriter {

    public void write(XBook book, File bookDir) {
        if (bookDir.isFile()) {
            return;
        }

        writeDetail(book, bookDir);

        File tocFile = new File(bookDir, Constants.TOC);
        writeTOC(book.getTOC(), tocFile);

        writeContent(book, bookDir);

        writeStyle(book, bookDir);

        writeResource(book, bookDir);
    }


    private void writeDetail(XBook book, File bookDir) {
        File detailFile = new File(bookDir, Constants.DETAIL);
        String detailString = book.getBookDetail().toJsonString();
        StringUtil.saveStringToFile(detailString, detailFile);
    }

    public void writeTOC(TOC toc, File tocFile) {
        String tocString = toc.toJsonString();
        StringUtil.saveStringToFile(tocString, tocFile);
    }

    private void writeContent(XBook book, File bookDir) {
        File contentDir = new File(bookDir, Constants.CONTENT);

        BookContent bookContent = book.getBookContent();
        for (Chapter chapter : bookContent.getAllChapters()) {
            File chapterFile = new File(contentDir, String.valueOf(chapter.getChapterId()));
            writeChapter(chapter, chapterFile);
        }
    }

    public void writeChapter(Chapter chapter, File chapterFile) {
        String chapterString = chapter.toJsonString();
        StringUtil.saveStringToFile(chapterString, chapterFile);
    }

    private void writeStyle(XBook book, File bookDir) {
    }

    private void writeResource(XBook book, File bookDir) {
        File resourceDir = new File(bookDir, Constants.RESOURCE);
        if (!resourceDir.exists()) {
            resourceDir.mkdir();
        }
        File coverFile = new File(resourceDir, Constants.COVER);
        if (coverFile.exists()) {
            try {
                coverFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  
            }
        }
        BookResource bookResource = book.getBookResource();
        Bitmap cover = bookResource.getCover();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(coverFile);
            cover.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closestream(out);
        }
    }
    private void closestream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
