package org.geometerplus.fbreader.formats.txt;

import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.BookReadingException;
import org.geometerplus.fbreader.formats.JavaFormatPlugin;
import org.geometerplus.zlibrary.core.encodings.AutoEncodingCollection;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.image.ZLImage;

import java.io.IOException;


public class TxtPlugin extends JavaFormatPlugin {

    public TxtPlugin() {
        super("plain text");
    }

    @Override
    public void readMetaInfo(Book book) throws BookReadingException {
    }

    @Override
    public void readUids(Book book) throws BookReadingException {
    }

    @Override
    public void readModel(BookModel model) throws BookReadingException {
        Book book = model.Book;
        ZLFile file = book.File;

        PlainTextFormat format = new PlainTextFormat(file);
        if (!format.initialized()) {
            PlainTextFormat.PlainTextFormatDetector detector = format.new PlainTextFormatDetector();
            try {
                detector.detect(file.getInputStream(), format);
            } catch (IOException e) {
                throw new BookReadingException(e, file);
            }
        }

        detectLanguageAndEncoding(book);
        TxtBookReader txtBookReader = new TxtBookReader(model, format, book.getEncoding());
        try {
            txtBookReader.readDocument(file);
        } catch (IOException e) {
            throw new BookReadingException(e, file);
        }
    }

    public void addFileToModel(BookModel model, ZLFile file) throws BookReadingException {
        Book book = model.Book;

        PlainTextFormat format = new PlainTextFormat(file);
        if (!format.initialized()) {
            PlainTextFormat.PlainTextFormatDetector detector = format.new PlainTextFormatDetector();
            try {
                detector.detect(file.getInputStream(), format);
            } catch (IOException e) {
                throw new BookReadingException(e, file);
            }
        }

        detectLanguageAndEncoding(book);
        TxtBookReader txtBookReader = new TxtBookReader(model, format, book.getEncoding());
        try {
            txtBookReader.readDocument(file);
        } catch (IOException e) {
            throw new BookReadingException(e, file);
        }
    }

    @Override
    public ZLImage readCover(ZLFile file) {
        return null;
    }

    @Override
    public String readAnnotation(ZLFile file) {
        return null;
    }

    @Override
    public AutoEncodingCollection supportedEncodings() {
        return new AutoEncodingCollection();
    }

    @Override
    public void detectLanguageAndEncoding(Book book) {

//        InputStream stream = book.File.getInputStream();
//        String language = book.getLanguage();
//        String encoding = book.getEncoding();
//
//        if (!TextUtils.isEmpty(encoding)) {
//            return;
//        }
//
//        boolean detected = false;
//        PluginCollection collection = PluginCollection.Instance();
//        if (TextUtils.isEmpty(encoding)) {
//            encoding = ZLEncodingConverter::UTF8;
//        }
//        if (collection.isLanguageAutoDetectEnabled() && stream.open()) {
//            static const int BUFSIZE = 65536;
//            char *buffer = new char[BUFSIZE];
//            const std::size_t size = stream.read(buffer, BUFSIZE);
//            stream.close();
//            shared_ptr<ZLLanguageDetector::LanguageInfo> info = ZLLanguageDetector().findInfo(buffer, size);
//            delete[] buffer;
//            if (!info.isNull()) {
//                detected = true;
//                if (!info->Language.empty()) {
//                    language = info->Language;
//                }
//                encoding = info->Encoding;
//                if (encoding == ZLEncodingConverter::ASCII || encoding == "iso-8859-1") {
//                    encoding = "windows-1252";
//                }
//            }
//        }
//        book.setEncoding(encoding);
//        book.setLanguage(language);
    }
}