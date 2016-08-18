package org.geometerplus.fbreader.formats.txt;

import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.FBTextKind;

public class TxtBookReader extends TxtReader {

    private final PlainTextFormat myFormat;

    private int myLineFeedCounter;
    private boolean myInsideContentsParagraph;
    private boolean myLastLineIsEmpty;
    private boolean myNewLine;
    private int mySpaceCounter;

    public TxtBookReader(BookModel model, PlainTextFormat format, final String encoding) {

        super(model, encoding);
        myFormat = format;
    }


    private void internalEndParagraph() {
        if (!myLastLineIsEmpty) {
            //myLineFeedCounter = 0;
            myLineFeedCounter = -1; /* Fixed by Hatred: zero value was break LINE INDENT formater -
		                           second line print with indent like new paragraf */
        }
        myLastLineIsEmpty = true;
        myBookReader.endParagraph();
    }

    protected boolean characterDataHandler(char[] str, int start, int length) {

        int i = start;
        int end = start + length;
        for (; i < end; ++i) {
            char ptr = str[i];
            if (Character.isWhitespace(ptr)) {
                if (ptr != '\t') {
                    ++mySpaceCounter;
                } else {
                    mySpaceCounter += myFormat.ignoredIndent() + 1; // TODO: implement single option in PlainTextFormat
                }
            } else {
                myLastLineIsEmpty = false;
                break;
            }
        }
        if (i != end) {
            if (((myFormat.breakType() & PlainTextFormat.BREAK_PARAGRAPH_AT_LINE_WITH_INDENT) != 0) &&
                    myNewLine && (mySpaceCounter > myFormat.ignoredIndent())) {
                internalEndParagraph();
                myBookReader.beginParagraph();
            }
            myBookReader.addData(str, start, length, false);
            if (myInsideContentsParagraph) {
                myBookReader.addContentsData(str, start, length);
            }
            myNewLine = false;
        }
        return true;
    }

    protected boolean newLineHandler() {
        if (!myLastLineIsEmpty) {
            myLineFeedCounter = -1;
        }
        myLastLineIsEmpty = true;
        ++myLineFeedCounter;
        myNewLine = true;
        mySpaceCounter = 0;
        boolean paragraphBreak =
                ((myFormat.breakType() & PlainTextFormat.BREAK_PARAGRAPH_AT_NEW_LINE) != 0) ||
                        (((myFormat.breakType() & PlainTextFormat.BREAK_PARAGRAPH_AT_EMPTY_LINE) != 0) && (myLineFeedCounter > 0));

        if (myFormat.createContentsTable()) {
//		if (!myInsideContentsParagraph && (myLineFeedCounter == myFormat.emptyLinesBeforeNewSection() + 1)) {
			/* Fixed by Hatred: remove '+ 1' for emptyLinesBeforeNewSection, it looks like very strange
				 when we should point count of empty string decrised by 1 in settings dialog */
            if (!myInsideContentsParagraph && (myLineFeedCounter == myFormat.emptyLinesBeforeNewSection())) {
                myInsideContentsParagraph = true;
                internalEndParagraph();
                myBookReader.insertEndOfSectionParagraph();
                myBookReader.beginContentsParagraph();
                myBookReader.enterTitle();
                myBookReader.pushKind(FBTextKind.SECTION_TITLE);
                myBookReader.beginParagraph();
                paragraphBreak = false;
            }
            if (myInsideContentsParagraph && (myLineFeedCounter == 1)) {
                myBookReader.exitTitle();
                myBookReader.endContentsParagraph();
                myBookReader.popKind();
                myInsideContentsParagraph = false;
                paragraphBreak = true;
            }
        }

        if (paragraphBreak) {
            internalEndParagraph();
            myBookReader.beginParagraph();
        }
        return true;
    }

    protected void startDocumentHandler() {
        myBookReader.setMainTextModel();
        myBookReader.pushKind(FBTextKind.REGULAR);
        myBookReader.beginParagraph();
        myLineFeedCounter = 0;
        myInsideContentsParagraph = false;
        myBookReader.enterTitle();
        myLastLineIsEmpty = true;
        myNewLine = true;
        mySpaceCounter = 0;
    }

    protected void endDocumentHandler() {
        internalEndParagraph();
    }
}










