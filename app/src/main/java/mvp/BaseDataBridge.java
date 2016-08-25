package mvp;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/25 15:55.
 */

public interface BaseDataBridge {
    /**
     * 网络访问出错时调用
     *
     * @param e
     */
    void onError (Throwable e);

    /**
     * 处理完成之后调用
     */
    void onCompleted ();
}
