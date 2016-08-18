package com.sanqiwan.reader.menu;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.sanqiwan.reader.R;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/28/13
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderFontView extends Dialog implements View.OnClickListener {

    public interface Listener {
        public void onZoomIn();
        public void onZoomOut();
    }

    private Listener mListener;

    public ReaderFontView(Context context) {
        super(context, R.style.reader_menu_style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_font_layout);
        findViewById(android.R.id.content).setOnClickListener(this);
        findViewById(R.id.decrease).setOnClickListener(this);
        findViewById(R.id.increase).setOnClickListener(this);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.decrease:
                if (mListener != null) {
                    mListener.onZoomOut();
                }
                break;
            case R.id.increase:
                if (mListener != null) {
                    mListener.onZoomIn();
                }
                break;
            case android.R.id.content:
                dismiss();
                break;
        }
    }
}
