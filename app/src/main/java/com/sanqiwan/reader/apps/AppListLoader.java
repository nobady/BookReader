package com.sanqiwan.reader.apps;

import com.sanqiwan.reader.webservice.OperationWebService;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-10-15
 * Time: 下午2:52
 * To change this template use File | Settings | File Templates.
 */
public class AppListLoader extends DataLoader<AppInfo> {

    private OperationWebService mOperationWebService;

    public AppListLoader() {
        mOperationWebService = new OperationWebService();
    }

    @Override
    public List<AppInfo> getData(int since, int pageSize) {
        try {
            return mOperationWebService.getAppList(since, pageSize);
        } catch (Exception e) {
        }
        return null;
    }
}
