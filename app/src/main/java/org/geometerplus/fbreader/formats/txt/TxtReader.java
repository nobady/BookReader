package org.geometerplus.fbreader.formats.txt;


import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.BookReader;
import org.geometerplus.fbreader.formats.EncodedTextReader;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class TxtReader extends EncodedTextReader {

    private TxtReaderCore myCore;
    protected BookReader myBookReader;

    protected TxtReader(BookModel model, final String encoding) {
        super(encoding);
        myBookReader = new BookReader(model);
//        if (ZLEncodingConverter::UTF16 == encoding) {
//            myCore = new TxtReaderCoreUtf16LE(this);
//        } else if (ZLEncodingConverter::UTF16BE == encoding) {
//            myCore = new TxtReaderCoreUtf16BE(this);
//        } else {
            myCore = new TxtReaderCore(this);
//        }
    }

    protected abstract void startDocumentHandler();
    protected abstract void endDocumentHandler();

    protected abstract boolean characterDataHandler(char[] str, int start, int length);
    protected abstract boolean newLineHandler();

    protected void readDocument(ZLFile file) throws IOException {
        InputStream stream = file.getInputStream();
        startDocumentHandler();
        myCore.readDocument(stream);
        endDocumentHandler();
        stream.close();
    }

    public class TxtReaderCore {
        protected TxtReader myReader;

        public TxtReaderCore(TxtReader myReader) {
            this.myReader = myReader;
        }

        protected void readDocument(InputStream stream) throws IOException {
            final int BUFSIZE = 2048;
            char[] buffer = new char[BUFSIZE];
            int length;
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            do {
                length = inputStreamReader.read(buffer);
                int start = 0;
                final int end = length;

                for (int i = start; i < end; ++i) {
                    char c = buffer[i];
                    if (c == '\n' || c == '\r') {
                        boolean skipNewLine = false;
                        if (c == '\r' && (i + 1) != end && buffer[i + 1] == '\n') {
                            skipNewLine = true;
                            buffer[i] = '\n';
                        }
                        if (start != i) {
                            myReader.characterDataHandler(buffer, start, i + 1 - start);
                        }
                        if (skipNewLine) {
                            ++i;
                        }
                        start = i + 1;
                        myReader.newLineHandler();
                    } else if (((c) & 0x80) == 0 && Character.isWhitespace(c)) {
                        if (c != '\t') {
                            buffer[i] = ' ';
                        }
                    } else {
                    }
                }
                if (start != end) {
                    myReader.characterDataHandler(buffer, start, end - start);
                }
            } while (length == BUFSIZE);
        }
    }

//    public abstract class TxtReaderCoreUtf16 extends TxtReaderCore {
//
//        public TxtReaderCoreUtf16(TxtReader reader) {
//            super(reader);
//        }
//
//        public void readDocument(InputStream stream) throws IOException {
//            final int BUFSIZE = 2048;
//            char[] buffer = new char[BUFSIZE];
//            int length;
//            InputStreamReader inputStreamReader = new InputStreamReader(stream);
//            do {
//                length = inputStreamReader.read(buffer);
//                int start = 0;
//                final int end = length;
//                for (int ptr = start; ptr < end; ptr += 2) {
//                    final char chr = getAscii(ptr);
//                    if (chr == '\n' || chr == '\r') {
//                        boolean skipNewLine = false;
//                        if (chr == '\r' && ptr + 2 != end && getAscii(ptr + 2) == '\n') {
//                            skipNewLine = true;
//                            setAscii(ptr, '\n');
//                        }
//                        if (start != ptr) {
//                            myReader.characterDataHandler(buffer, start, ptr + 2);
//                        }
//                        if (skipNewLine) {
//                            ptr += 2;
//                        }
//                        start = ptr + 2;
//                        myReader.newLineHandler();
//                    } else if (chr != 0 && ((buffer[ptr]) & 0x80) == 0 && Character.isWhitespace(chr)) {
//                        if (chr != '\t') {
//                            setAscii(ptr, ' ');
//                        }
//                    }
//                }
//                if (start != end) {
//                    myReader.characterDataHandler(buffer, start, end);
//                }
//            } while (length == BUFSIZE);
//        }
//
//        protected abstract char getAscii(const char *ptr);
//        protected abstract void setAscii(char *ptr, char ascii);
//    }
//
//    class TxtReaderCoreUtf16LE extends TxtReaderCoreUtf16 {
//
//        public TxtReaderCoreUtf16LE(TxtReader reader) {
//            super(reader);
//        }
//
//        protected char getAscii(const char *ptr) {
//            return *(ptr + 1) == '\0' ? *ptr : '\0';
//        }
//
//        protected void setAscii(char *ptr, char ascii) {
//            *ptr = ascii;
//        }
//    }
//
//    class TxtReaderCoreUtf16BE extends TxtReaderCoreUtf16 {
//
//        public TxtReaderCoreUtf16BE(TxtReader reader) {
//            super(reader);
//        }
//
//        protected char getAscii(const char *ptr) {
//            return *ptr == '\0' ? *(ptr + 1) : '\0';
//        }
//
//        protected void setAscii(char *ptr, char ascii) {
//            *(ptr + 1) = ascii;
//        }
//    }

}







