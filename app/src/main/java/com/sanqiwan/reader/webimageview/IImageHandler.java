package com.sanqiwan.reader.webimageview;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/22/13
 * Time: 9:07 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IImageHandler {

    public void onHandleImage(WebImageView imageView, Bitmap bitmap);
}
