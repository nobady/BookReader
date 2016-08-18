package com.sanqiwan.reader.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sanqiwan.reader.R;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/14/13
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoadingProgressDialog extends Dialog {
    private ProgressBar mProgress;
    private TextView mMessageView;
    private String mMessage;

    public LoadingProgressDialog(Context context) {
        super(context, R.style.progress_dialog_style);
        //set window params
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        //set width,height by density and gravity
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = getLayoutInflater().inflate(R.layout.progress_dialog, null);
        mProgress = (ProgressBar) view.findViewById(R.id.progress);
        mMessageView = (TextView) view.findViewById(R.id.textview);
        if (mMessage != null) {
            mMessageView.setText(mMessage);
            mMessageView.setVisibility(View.VISIBLE);
        } else {
            mMessageView.setVisibility(View.GONE);
        }
        setContentView(view);

    }

    public void setMessage(String message) {
        mMessage = message;
    }
}
