/*
 * Copyright (C) 2007-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.ui.android.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import com.sanqiwan.reader.engine.ReaderSettings;
import org.geometerplus.zlibrary.core.view.ZLView;

class SlideAnimationProvider extends SimpleAnimationProvider {
    private final Paint myPaint = new Paint();
    SlideAnimationProvider(BitmapManager bitmapManager) {
        super(bitmapManager);
    }

    @Override
    protected void drawInternal(Canvas canvas) {
        ZLView.PageIndex pageIndex = getPageToScrollTo();

        myPaint.setColor(Color.rgb(127, 127, 127));
        if (myDirection.IsHorizontal) {
            final int dX = myEndX - myStartX;

            if (pageIndex == ZLView.PageIndex.previous) {
                canvas.drawBitmap(getBitmapFrom(), 0, 0, myPaint);
                canvas.drawBitmap(getBitmapTo(), dX - myWidth, 0, myPaint);
            } else {
                canvas.drawBitmap(getBitmapTo(), 0, 0, myPaint);
                canvas.drawBitmap(getBitmapFrom(), dX, 0, myPaint);
            }

            Drawable shadow = ReaderSettings.getInstance().getPageShadow();
            if (dX > 0 && dX < myWidth) {
                shadow.setBounds(dX, 0, dX + shadow.getIntrinsicWidth(), myHeight + 1);
                shadow.draw(canvas);
            } else if (dX < 0 && dX > -myWidth) {
                shadow.setBounds(dX + myWidth, 0, dX + myWidth + shadow.getIntrinsicWidth(), myHeight + 1);
                shadow.draw(canvas);
            }
        } else {
            final int dY = myEndY - myStartY;
            if (pageIndex == ZLView.PageIndex.previous) {
                canvas.drawBitmap(getBitmapFrom(), 0, 0, myPaint);
                canvas.drawBitmap(getBitmapTo(), 0, dY - myHeight, myPaint);
            } else {
                canvas.drawBitmap(getBitmapTo(), 0, 0, myPaint);
                canvas.drawBitmap(getBitmapFrom(), 0, dY, myPaint);
            }
            if (dY > 0 && dY < myHeight) {
                canvas.drawLine(0, dY, myWidth + 1, dY, myPaint);
            } else if (dY < 0 && dY > -myHeight) {
                canvas.drawLine(0, dY + myHeight, myWidth + 1, dY + myHeight, myPaint);
            }
        }
    }
}
