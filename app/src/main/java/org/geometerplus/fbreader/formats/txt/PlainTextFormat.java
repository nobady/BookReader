package org.geometerplus.fbreader.formats.txt;

import org.geometerplus.zlibrary.core.filesystem.ZLFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PlainTextFormat {

    public static final int BREAK_PARAGRAPH_AT_NEW_LINE = 1;
    public static final int BREAK_PARAGRAPH_AT_EMPTY_LINE = 2;
    public static final int BREAK_PARAGRAPH_AT_LINE_WITH_INDENT = 4;

    public static final String OPTION_Initialized = "Initialized";
    public static final String OPTION_BreakType = "BreakType";
    public static final String OPTION_IgnoredIndent = "IgnoredIndent";
    public static final String OPTION_EmptyLinesBeforeNewSection = "EmptyLinesBeforeNewSection";
    public static final String OPTION_CreateContentsTable = "CreateContentsTable";

    private static final int BUFFER_SIZE = 4096;

    private boolean myInitialized;
    private int myBreakType;
    private int myIgnoredIndent;
    private int myEmptyLinesBeforeNewSection;
    private boolean myCreateContentsTable;

    public PlainTextFormat(ZLFile file) {
        myInitialized = false;
        myBreakType = 1;
        myIgnoredIndent = 1;
        myEmptyLinesBeforeNewSection = 1;
        myCreateContentsTable = false;
    }

    public boolean initialized() {
        return myInitialized;
    }

    public int breakType() {
        return myBreakType;
    }

    public int ignoredIndent() {
        return myIgnoredIndent;
    }

    public int emptyLinesBeforeNewSection() {
        return myEmptyLinesBeforeNewSection;
    }

    public boolean createContentsTable() {
        return myCreateContentsTable;
    }


    public class PlainTextFormatDetector {

        public PlainTextFormatDetector() {
        }


        public void detect(InputStream stream, PlainTextFormat format) throws IOException {

            final int tableSize = 10;

            int lineCounter = 0;
            int emptyLineCounter = -1;
            int stringsWithLengthLessThan81Counter = 0;
            int[] stringIndentTable = new int[tableSize];
            int[] emptyLinesTable = new int[tableSize];
            int[] emptyLinesBeforeShortStringTable = new int[tableSize];

            boolean currentLineIsEmpty = true;
            int currentLineLength = 0;
            int currentLineIndent = 0;
            int currentNumberOfEmptyLines = -1;

            char[] buffer = new char[BUFFER_SIZE];
            int length;
            char previous = 0;
            do {
                InputStreamReader inputStreamReader = new InputStreamReader(stream);
                length = inputStreamReader.read(buffer);
                for (int i = 0; i < length; i++) {
                    char ptr = buffer[i];
                    ++currentLineLength;
                    if (ptr == '\n') {
                        ++lineCounter;
                        if (currentLineIsEmpty) {
                            ++emptyLineCounter;
                            ++currentNumberOfEmptyLines;
                        } else {
                            if (currentNumberOfEmptyLines >= 0) {
                                int index = Math.min(currentNumberOfEmptyLines, tableSize - 1);
                                emptyLinesTable[index]++;
                                if (currentLineLength < 51) {
                                    emptyLinesBeforeShortStringTable[index]++;
                                }
                            }
                            currentNumberOfEmptyLines = -1;
                        }
                        if (currentLineLength < 81) {
                            ++stringsWithLengthLessThan81Counter;
                        }
                        if (!currentLineIsEmpty) {
                            stringIndentTable[Math.min(currentLineIndent, tableSize - 1)]++;
                        }

                        currentLineIsEmpty = true;
                        currentLineLength = 0;
                        currentLineIndent = 0;
                    } else if (ptr == '\r') {
                        continue;
                    } else if (Character.isWhitespace(ptr)) {
                        if (currentLineIsEmpty) {
                            ++currentLineIndent;
                        }
                    } else {
                        currentLineIsEmpty = false;
                    }
                    previous = ptr;
                }
            } while (length == BUFFER_SIZE);

            int nonEmptyLineCounter = lineCounter - emptyLineCounter;

            {
                int indent = 0;
                int lineWithIndent = 0;
                for (; indent < tableSize; ++indent) {
                    lineWithIndent += stringIndentTable[indent];
                    if (lineWithIndent > 0.1 * nonEmptyLineCounter) {
                        break;
                    }
                }
                format.myIgnoredIndent = (indent + 1);
            }

            {
                int breakType = 0;
                breakType |= BREAK_PARAGRAPH_AT_EMPTY_LINE;
//                if (stringsWithLengthLessThan81Counter < 0.3 * nonEmptyLineCounter) {
                    breakType |= BREAK_PARAGRAPH_AT_NEW_LINE;
//                } else {
//                    breakType |= BREAK_PARAGRAPH_AT_LINE_WITH_INDENT;
//                }
                format.myBreakType = breakType;
            }

            {
                int max = 0;
                int index;
                int emptyLinesBeforeNewSection = -1;
                for (index = 2; index < tableSize; ++index) {
                    if (max < emptyLinesBeforeShortStringTable[index]) {
                        max = emptyLinesBeforeShortStringTable[index];
                        emptyLinesBeforeNewSection = index;
                    }
                }
                if (emptyLinesBeforeNewSection > 0) {
                    for (index = tableSize - 1; index > 0; --index) {
                        emptyLinesTable[index - 1] += emptyLinesTable[index];
                        emptyLinesBeforeShortStringTable[index - 1] += emptyLinesBeforeShortStringTable[index];
                    }
                    for (index = emptyLinesBeforeNewSection; index < tableSize; ++index) {
                        if ((emptyLinesBeforeShortStringTable[index] > 2) &&
                                (emptyLinesBeforeShortStringTable[index] > 0.7 * emptyLinesTable[index])) {
                            break;
                        }
                    }
                    emptyLinesBeforeNewSection = (index == tableSize) ? -1 : (int)index;
                }
                format.myEmptyLinesBeforeNewSection = (emptyLinesBeforeNewSection);
                format.myCreateContentsTable = (emptyLinesBeforeNewSection > 0);
            }

            format.myInitialized = (true);
        }

    }
}

