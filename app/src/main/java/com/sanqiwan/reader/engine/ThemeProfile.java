package com.sanqiwan.reader.engine;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.R;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/30/13
 * Time: 6:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThemeProfile {
    public static final String DAY = "defaultLight";
    public static final String NIGHT = "defaultDark";
    public static final String DEFAULT = "default";


    private static final List<String> ourNames = new ArrayList<String>();
    private static final Map<String, ThemeProfile> ourProfiles = new HashMap<String, ThemeProfile>();

    public static List<String> names() {
        if (ourNames.isEmpty()) {
            ourNames.add(DAY);
            ourNames.add(NIGHT);
            ourNames.add(DEFAULT);
        }
        return Collections.unmodifiableList(ourNames);
    }

    public static ThemeProfile get(String name) {
        ThemeProfile profile = ourProfiles.get(name);
        if (profile == null) {
            profile = new ThemeProfile(name);
            ourProfiles.put(name, profile);
        }
        return profile;
    }

    private ThemeProfile(String name) {
        if (NIGHT.equals(name)) {
            setThemeName(AppContext.getInstance().getResources().getString(R.string.night_mode));
            setIconId(R.drawable.reader_day_night);
            setBackgroundDrawbale(new ColorDrawable(
                    AppContext.getInstance().getResources().
                            getColor(R.color.reader_bg_night)));
            setPageShadow(AppContext.getInstance().getResources().
                    getDrawable(R.drawable.page_shadow_night));
            setTextColor(AppContext.getInstance().getResources().getColor(R.color.reader_text_night));
            setTipTextColor(AppContext.getInstance().getResources().getColor(R.color.reader_tip_text_night));
            setTocLineDrawable(AppContext.getInstance().getResources().
                    getDrawable(R.drawable.night_diver_line));
            setTocTextColor(AppContext.getInstance().getResources().
                    getColorStateList(R.color.toc_night_color));
            mUsable = true;

        }
        if (DAY.equals(name)) {
            setThemeName(AppContext.getInstance().getResources().getString(R.string.day_mode));
            setIconId(R.drawable.reader_day);
            setBackgroundDrawbale( new ColorDrawable(
                    AppContext.getInstance().getResources().
                            getColor(R.color.reader_bg_day)));
            setPageShadow(AppContext.getInstance().getResources().
                    getDrawable(R.drawable.page_shadow));
            setTextColor(AppContext.getInstance().getResources().getColor(R.color.reader_text_day));
            setTipTextColor(AppContext.getInstance().getResources().getColor(R.color.reader_tip_text_day));
            setTocLineDrawable(AppContext.getInstance().getResources().
                    getDrawable(R.drawable.diver_line));
            setTocTextColor(AppContext.getInstance().getResources().
                    getColorStateList(R.color.toc_day_color));
            mUsable = true;
        }
        if (DEFAULT.equals(name)) {
            setThemeName(AppContext.getInstance().getResources().getString(R.string.default_mode));
            setIconId(R.drawable.reader_default);
            setBackgroundDrawbale( new ColorDrawable(
                    AppContext.getInstance().getResources().
                            getColor(R.color.reader_bg_default)));
            setPageShadow(AppContext.getInstance().getResources().
                    getDrawable(R.drawable.page_shadow));
            setTextColor(AppContext.getInstance().getResources().getColor(R.color.reader_text_default));
            setTipTextColor(AppContext.getInstance().getResources().getColor(R.color.reader_tip_text_default));
            setTocLineDrawable(AppContext.getInstance().getResources().
                    getDrawable(R.drawable.default_diver_line));
            setTocTextColor(AppContext.getInstance().getResources().
                    getColorStateList(R.color.toc_default_color));
            mUsable = true;
        }


    }

    private String mThemeName;
    private int mIconId;
    private int mTextColor;
    private int mTipTextColor;
    private Drawable mBackgroundDrawbale;
    private Drawable mPageShadow;
    private boolean mUsable;
    private ColorStateList mTocColor;
    private Drawable mTocLineDrawable;

    public boolean isUsable() {
        return mUsable;
    }
    public String getThemeName() {
        return mThemeName;
    }

    public void setThemeName(String themeName) {
        mThemeName = themeName;
    }

    public int getIconId() {
        return mIconId;
    }

    public void setIconId(int iconId) {
        mIconId = iconId;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public Drawable getBackgroundDrawbale() {
        return mBackgroundDrawbale;
    }

    public Drawable getPageShadow() {
        return mPageShadow;
    }

    public void setPageShadow(Drawable drawable) {
        mPageShadow = drawable;
    }

    public int getTipTextColor() {
        return mTipTextColor;
    }

    public void setTipTextColor(int color) {
        mTipTextColor = color;
    }

    public void setTocTextColor(ColorStateList color) {
        mTocColor = color;
    }

    public ColorStateList getTocTextColor() {
        return mTocColor;
    }

    public Drawable getTocLineDrawable() {
        return mTocLineDrawable;
    }

    public void setTocLineDrawable(Drawable drawableTextColor) {
        mTocLineDrawable = drawableTextColor;
    }

    public void setBackgroundDrawbale(Drawable backgroundDrawbale) {
        mBackgroundDrawbale = backgroundDrawbale;
    }
}
