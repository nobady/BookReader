package com.sanqiwan.reader.util;

import android.graphics.Bitmap;
import org.geometerplus.fbreader.network.NetworkImage;
import org.geometerplus.zlibrary.core.image.ZLLoadableImage;
import org.geometerplus.zlibrary.core.util.MimeType;
import org.geometerplus.zlibrary.ui.android.image.ZLAndroidImageData;
import org.geometerplus.zlibrary.ui.android.image.ZLAndroidImageManager;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/16/13
 * Time: 2:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageLoadUtil {

    public interface Callback {
        public void onImageLoaded(String url, Bitmap bitmap);
        public void onImageLoadFailed(String url);
    }

    public static void loadImage(String url, Callback callback) {
        final ZLLoadableImage image = new NetworkImage(url, MimeType.IMAGE_JPEG);
        image.startSynchronization(new PostRunnable(image, callback));
    }

    public static class PostRunnable implements Runnable {
        private Callback mCallback;
        private ZLLoadableImage mImage;

        public PostRunnable(ZLLoadableImage image, Callback callback) {
            mImage = image;
            mCallback = callback;
        }

        @Override
        public void run() {
            ZLAndroidImageManager imageManager = (ZLAndroidImageManager)ZLAndroidImageManager.Instance();
            ZLAndroidImageData imageData = imageManager.getImageData(mImage);
            if (imageData != null) {
                final Bitmap bitmap = imageData.getFullSizeBitmap();
                if (bitmap != null && mCallback != null) {
                    mCallback.onImageLoaded(mImage.getId(), bitmap);
                    return;
                }
            }

            if (mCallback != null) {
                mCallback.onImageLoadFailed(mImage.getId());
            }
        }
    }
}
