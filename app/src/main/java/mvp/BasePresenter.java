package mvp;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import rx.subscriptions.CompositeSubscription;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/3 11:08.
 */

public abstract class BasePresenter<V extends BaseView> extends MvpBasePresenter<V> {


    public CompositeSubscription mCompositeSubscription;


    @Override
    public void attachView (V view) {
        super.attachView (view);
        mCompositeSubscription = new CompositeSubscription ();
    }

    @Override
    public void detachView (boolean retainInstance) {
        super.detachView (retainInstance);
        if(mCompositeSubscription!=null){
            mCompositeSubscription.unsubscribe ();
            mCompositeSubscription = null;
        }
    }
}
