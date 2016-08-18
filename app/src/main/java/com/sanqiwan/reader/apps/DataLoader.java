package com.sanqiwan.reader.apps;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-10-11
 * Time: 下午12:18
 * To change this template use File | Settings | File Templates.
 */
public abstract class DataLoader<T> {
    abstract public List<T> getData(int since, int pageSize);
}
