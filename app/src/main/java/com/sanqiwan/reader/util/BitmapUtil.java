package com.sanqiwan.reader.util;

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import com.sanqiwan.reader.R;

public class BitmapUtil {
    private static TextView  sTitleView;
    private static int BOOK_TITLE_BACKGROUND_COLOR = 0xEF747474;
    private static int BOOK_TITLE_COLOR = 0xFFFFFFFF;
    public static Bitmap createReflectedBitmap(Bitmap srcBitmap) {
        if (null == srcBitmap) {
            return null;
        }

        // The gap between the reflection bitmap and original bitmap. 
        final int REFLECTION_GAP = 4;

        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        int reflectionWidth = srcBitmap.getWidth();
        int reflectionHeight = srcBitmap.getHeight() / 5;

        if (0 == srcWidth || srcHeight == 0) {
            return null;
        }

        // The matrix
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        try {
            // The reflection bitmap, width is same with original's, height is half of original's.
            Bitmap reflectionBitmap = Bitmap.createBitmap(
                    srcBitmap,
                    0,
                    (srcHeight*4)/ 5,
                    srcWidth,
                    srcHeight / 5,
                    matrix,
                    false);

            if (null == reflectionBitmap) {
                return null;
            }

            // Create the bitmap which contains original and reflection bitmap.
            Bitmap bitmapWithReflection = Bitmap.createBitmap(
                    reflectionWidth,
                    srcHeight + reflectionHeight + REFLECTION_GAP,
                    Config.ARGB_8888);

            if (null == bitmapWithReflection) {
                return null;
            }

            // Prepare the canvas to draw stuff.
            Canvas canvas = new Canvas(bitmapWithReflection);

            // Draw the original bitmap.
            canvas.drawBitmap(srcBitmap, 0, 0, null);

            // Draw the reflection bitmap.
            canvas.drawBitmap(reflectionBitmap, 0, srcHeight + REFLECTION_GAP, null);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            LinearGradient shader = new LinearGradient(
                    0,
                    srcHeight,
                    0,
                    bitmapWithReflection.getHeight() + REFLECTION_GAP,
                    0x70FFFFFF,
                    0x00FFFFFF,
                    TileMode.MIRROR);
            paint.setShader(shader);
            paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN));

            // Draw the linear shader.
            canvas.drawRect(
                    0,
                    srcHeight,
                    srcWidth,
                    bitmapWithReflection.getHeight() + REFLECTION_GAP,
                    paint);

            return bitmapWithReflection;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap ceratBitmapwithTips(Bitmap srcBitmap, Bitmap dstBitmap) {
        if (srcBitmap == null || dstBitmap == null) {
            return null;
        }
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        int dstWidth = dstBitmap.getWidth();
        try {
            Bitmap bitmapWithTips = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
            if (bitmapWithTips == null) {
                return null;
            }
            Canvas canvas = new Canvas(bitmapWithTips);
            canvas.drawBitmap(srcBitmap, 0, 0, null);
            canvas.drawBitmap(dstBitmap, srcWidth - dstWidth, 0, null);
            return bitmapWithTips;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Load bitmap file from sd card.
     *
     * @param strPath The bitmap file path.
     * @return The Bitmap object, the returned value may be null.
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (null == drawable) {
            return null;
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        return drawableToBitmap(drawable, width, height);
    }

    /**
     * Load bitmap file from sd card.
     *
     * @param strPath The bitmap file path.
     * @return The Bitmap object, the returned value may be null.
     */
    public static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        if (null == drawable || width <= 0 || height <= 0) {
            return null;
        }

        Config config = (drawable.getOpacity() != PixelFormat.OPAQUE) ? Config.ARGB_8888 : Config.RGB_565;
        Bitmap bitmap = null;

        try {
            bitmap = Bitmap.createBitmap(width, height, config);
            if (null != bitmap) {
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, width, height);
                drawable.draw(canvas);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (reqWidth == 0 || reqHeight == 0) {
            return inSampleSize;
        }
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String pathName,
                                                         int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public static Bitmap  drawBookCover(Context context, String titleName) {
        try {
            Bitmap origin = BitmapFactory.decodeStream(context.getResources().getAssets().open("default_cover.png"));
            int picWidth = origin.getWidth();
            int picHeight = origin.getHeight();
            Bitmap dest = Bitmap.createBitmap(picWidth, picHeight, Config.ARGB_8888);
            Canvas canvas = new Canvas(dest);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            canvas.drawBitmap(origin, 0, 0, paint);
            sTitleView = new TextView(context);
            sTitleView.setTextSize(context.getResources().getInteger(R.integer.cover_name_size));
            sTitleView.setPadding(5, 4, 4, 4);
            sTitleView.setBackgroundColor(BOOK_TITLE_BACKGROUND_COLOR);
            sTitleView.setTextColor(BOOK_TITLE_COLOR);
            sTitleView.setMaxLines(2);
            sTitleView.setText(titleName);
            sTitleView.measure(View.MeasureSpec.makeMeasureSpec(picWidth, View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(picHeight, View.MeasureSpec.AT_MOST));
            int left = 0;
            int top = picHeight - sTitleView.getMeasuredHeight() - 10;
            canvas.translate(left, top);
            canvas.clipRect(0, 0, picWidth, sTitleView.getMeasuredHeight());
            sTitleView.layout(0, 0, picWidth, sTitleView.getMeasuredHeight());
            sTitleView.draw(canvas);
            return dest;
        } catch (Exception e) {

        }
        return null;
    }

    public static BitmapDrawable getRepeatDrawable(Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(bitmap);
        drawable.setTileModeXY(Shader.TileMode.REPEAT , Shader.TileMode.REPEAT);
        drawable.setDither(true);
        return drawable;
    }
}
