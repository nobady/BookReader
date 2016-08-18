package com.sanqiwan.reader.engine;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/27/13
 * Time: 5:46 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Scrollable {

    /**
     * Indicate the object can scroll.
     * @param direction scroll to left if direction > 0, scroll to right if direction < 0
     * @return true if the object can be scrolled.
     */
    public boolean canScroll(int direction);
}
