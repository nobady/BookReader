package org.geometerplus.fbreader.formats;

import org.geometerplus.zlibrary.core.encodings.EncodingConverter;
import org.geometerplus.zlibrary.core.encodings.JavaEncodingCollection;

public class EncodedTextReader {

    protected EncodedTextReader(final String encoding) {
        JavaEncodingCollection collection = JavaEncodingCollection.Instance();
        myConverter = collection.getEncoding(encoding).createConverter();
    }

    protected EncodingConverter myConverter;
}
