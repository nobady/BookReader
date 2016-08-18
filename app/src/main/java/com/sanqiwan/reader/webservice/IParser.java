package com.sanqiwan.reader.webservice;

import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/25/13
 * Time: 11:12 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IParser<T> {
    public T parse(InputStream inputStream);

    public List<T> parseList(InputStream inputStream);
}
