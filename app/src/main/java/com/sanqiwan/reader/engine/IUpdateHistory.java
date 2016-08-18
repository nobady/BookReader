package com.sanqiwan.reader.engine;

import com.sanqiwan.reader.model.Chapter;
import org.geometerplus.zlibrary.text.view.ZLTextPosition;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/13/13
 * Time: 11:13 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IUpdateHistory {

    public void onUpdateHistory(Chapter chapter, ZLTextPosition position);
}
