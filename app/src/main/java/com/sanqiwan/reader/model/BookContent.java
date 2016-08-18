package com.sanqiwan.reader.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BookContent {
    private Map<Long, Chapter> mChapters;

    public BookContent() {
        mChapters = new HashMap<Long, Chapter>();
    }

    public Collection<Chapter> getAllChapters() {
        return mChapters.values();
    }

    public Chapter getChapterById(long chapterId) {
        return mChapters.get(chapterId);
    }

    public void addChapter(Chapter chapter) {
        if (chapter != null) {
            mChapters.put(chapter.getChapterId(), chapter);
        }
    }
}