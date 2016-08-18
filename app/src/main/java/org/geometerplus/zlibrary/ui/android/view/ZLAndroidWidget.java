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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.*;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.engine.PagingReader;
import com.sanqiwan.reader.engine.ReaderSettings;
import com.sanqiwan.reader.engine.Scrollable;
import com.sanqiwan.reader.util.ToastUtil;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.view.ZLView;
import org.geometerplus.zlibrary.core.view.ZLViewWidget;

public class ZLAndroidWidget extends View implements ZLViewWidget, View.OnLongClickListener, Scrollable {
    private static final boolean ENABLE_LONG_PRESS = false;

    private final Paint myPaint = new Paint();
    private final BitmapManager myBitmapManager = new BitmapManager(this);
    private FBReaderApp mFBReaderApp;
    private Book mBook;
    private PagingReader.Indicator mIndicator;
    private boolean mCanShowIndicatorToast;

    public ZLAndroidWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ZLAndroidWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZLAndroidWidget(Context context) {
        super(context);
        init();
    }

    public void setIndicator(PagingReader.Indicator indicator) {
        mIndicator = indicator;
    }

    private void init() {
        mFBReaderApp = new FBReaderApp();
        mFBReaderApp.setViewWidget(this);
        // next line prevent ignoring first onKeyDown DPad event
        // after any dialog was closed
        setFocusableInTouchMode(true);
        setDrawingCacheEnabled(false);
        setOnLongClickListener(this);
    }

    public void openBook(Book book) {
        mBook = book;
        mFBReaderApp.openBook(book, null, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getAnimationProvider().terminate();
        if (myScreenIsTouched) {
            final ZLView view = mFBReaderApp.getCurrentView();
            myScreenIsTouched = false;
            view.onScrollingFinished(ZLView.PageIndex.current);
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (getAnimationProvider().inProgress()) {
            onDrawInScrolling(canvas);
        } else {
            onDrawStatic(canvas);
            mFBReaderApp.onRepaintFinished();
        }
    }

    private AnimationProvider myAnimationProvider;
    private ZLView.Animation myAnimationType;

    private AnimationProvider getAnimationProvider() {
        final ZLView.Animation type = mFBReaderApp.getCurrentView().getAnimationType();
        if (myAnimationProvider == null || myAnimationType != type) {
            myAnimationType = type;
            switch (type) {
                case none:
                    myAnimationProvider = new NoneAnimationProvider(myBitmapManager);
                    break;
                case curl:
                    myAnimationProvider = new CurlAnimationProvider(myBitmapManager);
                    break;
                case slide:
                    myAnimationProvider = new SlideAnimationProvider(myBitmapManager);
                    break;
                case shift:
                    myAnimationProvider = new ShiftAnimationProvider(myBitmapManager);
                    break;
            }
        }
        return myAnimationProvider;
    }

    private void onDrawInScrolling(Canvas canvas) {
        final ZLView view = mFBReaderApp.getCurrentView();

//		final int w = getWidth();
//		final int h = getMainAreaHeight();

        final AnimationProvider animator = getAnimationProvider();
        final AnimationProvider.Mode oldMode = animator.getMode();
        animator.doStep();
        if (animator.inProgress()) {
            animator.draw(canvas);
            if (animator.getMode().Auto) {
                postInvalidate();
            }
        } else {
            switch (oldMode) {
                case AnimatedScrollingForward: {
                    final ZLView.PageIndex index = animator.getPageToScrollTo();
                    myBitmapManager.shift(index == ZLView.PageIndex.next);
                    view.onScrollingFinished(index);
                    mFBReaderApp.onRepaintFinished();
                    break;
                }
                case AnimatedScrollingBackward:
                    view.onScrollingFinished(ZLView.PageIndex.current);
                    break;
            }
            onDrawStatic(canvas);
        }
    }

    public void reset() {
        myBitmapManager.reset();
    }

    public void repaint() {
        postInvalidate();
    }

    public void startManualScrolling(int x, int y, ZLView.Direction direction) {
        final AnimationProvider animator = getAnimationProvider();
        animator.setup(direction, getWidth(), getHeight());
        animator.startManualScrolling(x, y);
        mCanShowIndicatorToast = true;
    }

    public void scrollManuallyTo(int x, int y) {
        final ZLView view = mFBReaderApp.getCurrentView();
        final AnimationProvider animator = getAnimationProvider();
        ZLView.PageIndex pageIndex = animator.getPageToScrollTo(x, y);
        if (view.canScroll(pageIndex)) {
            getParent().requestDisallowInterceptTouchEvent(true);
            animator.scrollTo(x, y);
            postInvalidate();
        } else {
            getParent().requestDisallowInterceptTouchEvent(false);
            if (mCanShowIndicatorToast) {
                mCanShowIndicatorToast = false;
                if (mIndicator == PagingReader.Indicator.FistChapter
                        && pageIndex == ZLView.PageIndex.previous) {
                    ToastUtil.showToast(R.string.fist_page_tips);
                } else if (mIndicator == PagingReader.Indicator.LastChapter
                        && pageIndex == ZLView.PageIndex.next) {
                    ToastUtil.showToast(R.string.last_page_tips);
                }
            }
        }
    }

    public void startAnimatedScrolling(ZLView.PageIndex pageIndex, int x, int y, ZLView.Direction direction, int speed) {
        final ZLView view = mFBReaderApp.getCurrentView();
        if (pageIndex == ZLView.PageIndex.current || !view.canScroll(pageIndex)) {
            return;
        }
        final AnimationProvider animator = getAnimationProvider();
        animator.setup(direction, getWidth(), getHeight());
        animator.startAnimatedScrolling(pageIndex, x, y, speed);
        if (animator.getMode().Auto) {
            postInvalidate();
        }
    }

    public void startAnimatedScrolling(ZLView.PageIndex pageIndex, ZLView.Direction direction, int speed) {
        final ZLView view = mFBReaderApp.getCurrentView();
        if (pageIndex == ZLView.PageIndex.current || !view.canScroll(pageIndex)) {
            return;
        }
        final AnimationProvider animator = getAnimationProvider();
        animator.setup(direction, getWidth(), getHeight());
        animator.startAnimatedScrolling(pageIndex, null, null, speed);
        if (animator.getMode().Auto) {
            postInvalidate();
        }
    }

    public void startAnimatedScrolling(int x, int y, float xVelocity, float yVelocity, int speed) {
        final ZLView view = mFBReaderApp.getCurrentView();
        final AnimationProvider animator = getAnimationProvider();
        if (!view.canScroll(animator.getPageToScrollTo(x, y))) {
            animator.terminate();
            return;
        }
        animator.startAnimatedScrolling(x, y, xVelocity, yVelocity, speed);
        postInvalidate();
    }

    void drawOnBitmap(Bitmap bitmap, ZLView.PageIndex index) {
        final ZLView view = mFBReaderApp.getCurrentView();
        if (view == null) {
            return;
        }

        Canvas canvas = new Canvas(bitmap);

        int restore = canvas.save();
        drawWallpaper(canvas);
        canvas.restoreToCount(restore);

        drawHeader(canvas);

        restore = canvas.save();
        canvas.save();
        int headerHeight = getHeaderHeight(view);
        canvas.translate(0, headerHeight);
        final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
                canvas,
                getWidth(),
                getMainAreaHeight(),
                view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
        );
        view.paint(context, index);

        canvas.restoreToCount(restore);

        drawFooter(canvas, index);
    }

    private int getHeaderHeight(ZLView view) {
        final ZLView.DrawArea header = view.getHeader();
        int headerHeight = 0;
        if (header != null) {
            headerHeight = header.getHeight();
        }
        return headerHeight;
    }

    private void drawWallpaper(Canvas canvas) {
        Drawable wallpaper = ReaderSettings.getInstance().getWallpaper();
        wallpaper.setBounds(0, 0, getWidth(), getHeight());
        wallpaper.draw(canvas);
    }

    private void drawHeader(Canvas canvas) {
        final ZLView view = mFBReaderApp.getCurrentView();
        final ZLView.DrawArea header = view.getHeader();

        if (header == null) {
            return;
        }

        canvas.save();
        final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
                canvas,
                getWidth(),
                header.getHeight(),
                view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
        );
        header.paint(context);
        canvas.restore();
    }

    private void drawFooter(Canvas canvas, ZLView.PageIndex pageIndex) {
        final ZLView view = mFBReaderApp.getCurrentView();
        final ZLView.DrawArea footer = view.getFooter();

        if (footer == null) {
            return;
        }
        footer.setPageIndex(pageIndex);

        canvas.save();
        canvas.translate(0, getHeight() - footer.getHeight());
        final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
                canvas,
                getWidth(),
                footer.getHeight(),
                view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
        );
        footer.paint(context);
        canvas.restore();
    }

    private void onDrawStatic(final Canvas canvas) {
        myBitmapManager.setSize(getWidth(), getHeight());
        canvas.drawBitmap(myBitmapManager.getBitmap(ZLView.PageIndex.current), 0, 0, myPaint);
        new Thread() {
            @Override
            public void run() {
                final ZLView view = mFBReaderApp.getCurrentView();
                final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
                        canvas,
                        getWidth(),
                        getMainAreaHeight(),
                        view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
                );
                view.preparePage(context, ZLView.PageIndex.next);
            }
        }.start();
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onKeyDown(KeyEvent.KEYCODE_DPAD_CENTER, null);
        } else {
            mFBReaderApp.getCurrentView().onTrackballRotated((int) (10 * event.getX()), (int) (10 * event.getY()));
        }
        return true;
    }


    private class LongClickRunnable implements Runnable {
        public void run() {
            if (performLongClick()) {
                myLongClickPerformed = true;
            }
        }
    }

    private volatile LongClickRunnable myPendingLongClickRunnable;
    private volatile boolean myLongClickPerformed;

    private void postLongClickRunnable() {
        myLongClickPerformed = false;
        myPendingPress = false;
        if (myPendingLongClickRunnable == null) {
            myPendingLongClickRunnable = new LongClickRunnable();
        }
        postDelayed(myPendingLongClickRunnable, 2 * ViewConfiguration.getLongPressTimeout());
    }

    private class ShortClickRunnable implements Runnable {
        public void run() {
            final ZLView view = mFBReaderApp.getCurrentView();
            view.onFingerSingleTap(myPressedX, myPressedY);
            myPendingPress = false;
            myPendingShortClickRunnable = null;
        }
    }

    private volatile ShortClickRunnable myPendingShortClickRunnable;

    private volatile boolean myPendingPress;
    private volatile boolean myPendingDoubleTap;
    private int myPressedX, myPressedY;
    private boolean myScreenIsTouched;

    private VelocityTracker mVelocityTracker;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);

        int x = (int) event.getX();
        int y = (int) event.getY();

        final ZLView view = mFBReaderApp.getCurrentView();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (myPendingDoubleTap) {
                    view.onFingerDoubleTap(x, y);
                }
                if (myLongClickPerformed) {
                    view.onFingerReleaseAfterLongPress(x, y);
                } else {
                    if (myPendingLongClickRunnable != null) {
                        removeCallbacks(myPendingLongClickRunnable);
                        myPendingLongClickRunnable = null;
                    }
                    if (myPendingPress) {
                        if (view.isDoubleTapSupported()) {
                            if (myPendingShortClickRunnable == null) {
                                myPendingShortClickRunnable = new ShortClickRunnable();
                            }
                            postDelayed(myPendingShortClickRunnable, ViewConfiguration.getDoubleTapTimeout());
                        } else {
                            view.onFingerSingleTap(x, y);
                        }
                    } else {
                        mVelocityTracker.computeCurrentVelocity(1000);
                        view.onFingerRelease(x, y, mVelocityTracker.getXVelocity(), mVelocityTracker.getYVelocity());
                    }
                }
                myPendingDoubleTap = false;
                myPendingPress = false;
                myScreenIsTouched = false;
                break;
            case MotionEvent.ACTION_DOWN:
                if (myPendingShortClickRunnable != null) {
                    removeCallbacks(myPendingShortClickRunnable);
                    myPendingShortClickRunnable = null;
                    myPendingDoubleTap = true;
                } else {
                    if (ENABLE_LONG_PRESS) {
                        postLongClickRunnable();
                    }
                    myPendingPress = true;
                }
                myScreenIsTouched = true;
                myPressedX = x;
                myPressedY = y;
                break;
            case MotionEvent.ACTION_MOVE: {
                final int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                final boolean isAMove =
                        Math.abs(myPressedX - x) > slop || Math.abs(myPressedY - y) > slop;
                if (isAMove) {
                    myPendingDoubleTap = false;
                }
                if (myLongClickPerformed) {
                    view.onFingerMoveAfterLongPress(x, y);
                } else {
                    if (myPendingPress) {
                        if (isAMove) {
                            if (myPendingShortClickRunnable != null) {
                                removeCallbacks(myPendingShortClickRunnable);
                                myPendingShortClickRunnable = null;
                            }
                            if (myPendingLongClickRunnable != null) {
                                removeCallbacks(myPendingLongClickRunnable);
                            }
                            view.onFingerPress(myPressedX, myPressedY);
                            myPendingPress = false;
                        }
                    }
                    if (!myPendingPress) {
                        view.onFingerMove(x, y);
                    }
                }
                break;
            }
        }
        return true;
    }

    public boolean onLongClick(View v) {
        if (ENABLE_LONG_PRESS) {
            final ZLView view = mFBReaderApp.getCurrentView();
            return view.onFingerLongPress(myPressedX, myPressedY);
        } else {
            return false;
        }
    }

    private int myKeyUnderTracking = -1;
    private long myTrackingStartTime;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final ZLApplication application = mFBReaderApp;

        if (application.hasActionForKey(keyCode, true) ||
                application.hasActionForKey(keyCode, false)) {
            if (myKeyUnderTracking != -1) {
                if (myKeyUnderTracking == keyCode) {
                    return true;
                } else {
                    myKeyUnderTracking = -1;
                }
            }
            if (application.hasActionForKey(keyCode, true)) {
                myKeyUnderTracking = keyCode;
                myTrackingStartTime = System.currentTimeMillis();
                return true;
            } else {
                return application.runActionByKey(keyCode, false);
            }
        } else {
            return false;
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (myKeyUnderTracking != -1) {
            if (myKeyUnderTracking == keyCode) {
                final boolean longPress = System.currentTimeMillis() >
                        myTrackingStartTime + ViewConfiguration.getLongPressTimeout();
                mFBReaderApp.runActionByKey(keyCode, longPress);
            }
            myKeyUnderTracking = -1;
            return true;
        } else {
            final ZLApplication application = mFBReaderApp;
            return
                    application.hasActionForKey(keyCode, false) ||
                            application.hasActionForKey(keyCode, true);
        }
    }

    protected int computeVerticalScrollExtent() {
        final ZLView view = mFBReaderApp.getCurrentView();
        if (!view.isScrollbarShown()) {
            return 0;
        }
        final AnimationProvider animator = getAnimationProvider();
        if (animator.inProgress()) {
            final int from = view.getScrollbarThumbLength(ZLView.PageIndex.current);
            final int to = view.getScrollbarThumbLength(animator.getPageToScrollTo());
            final int percent = animator.getScrolledPercent();
            return (from * (100 - percent) + to * percent) / 100;
        } else {
            return view.getScrollbarThumbLength(ZLView.PageIndex.current);
        }
    }

    protected int computeVerticalScrollOffset() {
        final ZLView view = mFBReaderApp.getCurrentView();
        if (!view.isScrollbarShown()) {
            return 0;
        }
        final AnimationProvider animator = getAnimationProvider();
        if (animator.inProgress()) {
            final int from = view.getScrollbarThumbPosition(ZLView.PageIndex.current);
            final int to = view.getScrollbarThumbPosition(animator.getPageToScrollTo());
            final int percent = animator.getScrolledPercent();
            return (from * (100 - percent) + to * percent) / 100;
        } else {
            return view.getScrollbarThumbPosition(ZLView.PageIndex.current);
        }
    }

    protected int computeVerticalScrollRange() {
        final ZLView view = mFBReaderApp.getCurrentView();
        if (!view.isScrollbarShown()) {
            return 0;
        }
        return view.getScrollbarFullSize();
    }

    private int getMainAreaHeight() {
        final ZLView.DrawArea header = mFBReaderApp.getCurrentView().getHeader();
        final ZLView.DrawArea footer = mFBReaderApp.getCurrentView().getFooter();
        int headerHeight = header != null ? header.getHeight() : 0;
        int footerHeight = footer != null ? footer.getHeight() : 0;
        return getHeight() - headerHeight - footerHeight;
    }

    public boolean canScrollToLeft() {
        return mFBReaderApp.getCurrentView().canScroll(ZLView.PageIndex.previous);
    }

    public boolean canScrollToRight() {
        return mFBReaderApp.getCurrentView().canScroll(ZLView.PageIndex.next);
    }

    @Override
    public boolean canScroll(int direction) {
        if (direction > 0) {
            return canScrollToLeft();
        } else if (direction < 0) {
            return canScrollToRight();
        } else {
            return false;
        }
    }

    public void nextPage() {
        mFBReaderApp.runAction(ActionCode.TURN_PAGE_FORWARD);
    }

    public void previousPage() {
        mFBReaderApp.runAction(ActionCode.TURN_PAGE_BACK);
    }

    public boolean isEmpty() {
        return mBook == null;
    }

    public void clearTextCaches() {
        mFBReaderApp.clearTextCaches();
    }
}
