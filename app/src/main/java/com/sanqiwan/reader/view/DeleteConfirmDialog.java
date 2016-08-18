package com.sanqiwan.reader.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.sanqiwan.reader.R;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/14/13
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteConfirmDialog extends Dialog {
    private Button mCancelButton, mOkButton;
    private TextView mMessageView, mTitleView;
    private String mMessage, mTitle;
    private View.OnClickListener mOkClickListener,mCancelClickListener;

    public DeleteConfirmDialog(Context context) {
        super(context, R.style.delete_dialog_style);
        //set window params
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        //set width,height by density and gravity
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM|Gravity.FILL_HORIZONTAL;
        window.setAttributes(params);
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = getLayoutInflater().inflate(R.layout.delete_tip_dialog, null);
        mCancelButton = (Button) view.findViewById(R.id.cancel);
        mOkButton = (Button) view.findViewById(R.id.ok);
        mMessageView = (TextView) view.findViewById(R.id.message);
        mTitleView = (TextView) view.findViewById(R.id.title);
        if (mMessage != null) {
            mMessageView.setText(mMessage);
            mMessageView.setVisibility(View.VISIBLE);
        } else {
            mMessageView.setVisibility(View.INVISIBLE);
        }

        if (mTitle != null) {
            mTitleView.setText(mTitle);
        }

        setContentView(view);

        if (mOkClickListener != null) {
            mOkButton.setOnClickListener(mOkClickListener);
        }

        if (mCancelClickListener != null) {
            mCancelButton.setOnClickListener(mCancelClickListener);
        } else {
            mCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setOkClickListener(View.OnClickListener okClickListener) {
        mOkClickListener = okClickListener;
    }

    public void setCancelClickListener(View.OnClickListener cancelClickListener) {
        mCancelClickListener = cancelClickListener;
    }
}
