package com.sanqiwan.reader.payment;

import android.content.Context;
import android.view.View;
import com.sanqiwan.reader.model.PaymentResponseInfo;
import com.sanqiwan.reader.view.CmccConfirmDialog;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IBM    充值方式的接口
 * Date: 13-11-4
 * Time: 下午9:14
 * To change this template use File | Settings | File Templates.
 */
public interface IPayment {
    public String getName();
    public View getView(Context context, CmccConfirmDialog.CallBack callBack);
    void bindView(View view);
    View newView();
    public PaymentResponseInfo pay() throws ZLNetworkException;
    int convertMoney(int value);
    String getRatioTipText(int value);
    public List<String> getText();
}
