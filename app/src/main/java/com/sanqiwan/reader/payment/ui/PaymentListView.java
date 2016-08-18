package com.sanqiwan.reader.payment.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanqiwan.reader.R;
import com.sanqiwan.reader.payment.IPayment;
import com.sanqiwan.reader.util.DeviceUtil;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: IBM Date: 13-11-5 Time: 下午5:08 To change this template use File
 * | Settings | File Templates.
 */
public class PaymentListView extends LinearLayout {

    private static final int COLUMNS = 2;
    private static final int MARGIN_LEFT = 0;
    private static final int MARGIN_BOTTOM = 0;
    private static final int WEIGHT = 1;
    private float mTextSize;
    private int mMarginTop;
    private int mMarginRight;
    private int mWidth;
    private int mHeight;

    private Context mContext;
    private List<IPayment> mPaymentList;
    private OnClickListener mPaymentButtonListener;

    public PaymentListView (Context context) {
        this (context, null);
        mContext = context;
    }

    public PaymentListView (Context context, AttributeSet attrs) {
        super (context, attrs);
        mContext = context;
        mWidth = mContext.getResources ().getDrawable (R.drawable.gv_bg_press).getMinimumWidth ();
        mHeight = mContext.getResources ().getDrawable (R.drawable.gv_bg_press).getMinimumHeight ();
        mTextSize = 14 * DeviceUtil.getDensity ();
        mMarginTop = (int) mContext.getResources ().getDimension (R.dimen.payment_button_margin);
        mMarginRight = (int) mContext.getResources ().getDimension (R.dimen.payment_button_margin);
    }

    public void setOnClickListener (View.OnClickListener listener) {
        mPaymentButtonListener = listener;
    }

    public void setPaymentList (List<IPayment> payments) {
        mPaymentList = payments;
        int rows = (mPaymentList.size () + 1) / COLUMNS;
        for (int i = 0; i < rows; i++) {
            LinearLayout row = new LinearLayout (mContext);
            for (int j = 0; j < COLUMNS; j++) {

                LayoutParams params = new LayoutParams (mWidth, mHeight, 1);
                params.setMargins (MARGIN_LEFT, mMarginTop, mMarginRight, MARGIN_BOTTOM);
                TextView button = getPaymentButton (mPaymentList.get (2 * i + j));
                row.addView (button, params);
                if (i == 0 && j == 0) {
//                    LayoutParams params = new LayoutParams (mWidth, mHeight, 1);
//                    params.setMargins (MARGIN_LEFT, mMarginTop, mMarginRight, MARGIN_BOTTOM);
//                    TextView button = getPaymentButton (mPaymentList.get (0));
                    button.setSelected (true);
//                    row.addView (button, params);
                }
//                //添加微信支付
//                if (i == 0 && j == 1) {
//
//                }
//                if (i == 1 && j == 0) {
//                    LayoutParams params = new LayoutParams (mWidth, mHeight, 1);
//                    params.setMargins (MARGIN_LEFT, mMarginTop, mMarginRight, MARGIN_BOTTOM);
//                    TextView button = getPaymentButton (mPaymentList.get (1));
//                    row.addView (button, params);
//                }
//                if (i == 1 && j == 1) {
//                    LayoutParams params = new LayoutParams (mWidth, mHeight, 1);
//                    params.setMargins (MARGIN_LEFT, mMarginTop, mMarginRight, MARGIN_BOTTOM);
//                    TextView button = getPaymentButton (mPaymentList.get (2));
//                    row.addView (button, params);
//                }
            }
            addView (row);
        }
    }

    public void clearChildrenSelect () {
        int childCount = getChildCount ();
        for (int i = 0; i < childCount; i++) {
            LinearLayout row = (LinearLayout) getChildAt (i);
            int buttonCount = row.getChildCount ();
            for (int j = 0; j < buttonCount; j++) {
                row.getChildAt (j).setSelected (false);
            }
        }
    }

    private TextView getPaymentButton (IPayment payment) {

        TextView textView = (TextView) View.inflate (mContext, R.layout.textview, null);
        textView.setTag (payment);
        textView.setText (payment.getName ());
        textView.setHeight (mHeight);
        textView.setWidth (mWidth);
        textView.setOnClickListener (mPaymentListener);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            textView.setBackground (mContext.getResources ().getDrawable (R.drawable.gv_btn_bg));
        } else {
            Drawable drawable = getResources ().getDrawable (R.drawable.gv_btn_bg);
            textView.setBackgroundDrawable (drawable);
        }
        textView.setTextColor (mContext.getResources ().getColorStateList (R.color.btn_payment_color));
        //        textView.setTextSize(mTextSize);
        textView.setGravity (Gravity.CENTER);
        return textView;
    }

    private OnClickListener mPaymentListener = new OnClickListener () {
        @Override
        public void onClick (View v) {
            mPaymentButtonListener.onClick (v);
        }
    };
}
