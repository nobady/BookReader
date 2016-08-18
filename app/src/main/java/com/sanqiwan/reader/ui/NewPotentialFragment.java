package com.sanqiwan.reader.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sanqiwan.reader.model.BookItem;
import com.sanqiwan.reader.model.BookRecommendItem;
import com.sanqiwan.reader.webservice.BookWebService;
import com.sanqiwan.reader.webservice.OperationWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-11-21
 * Time: 上午10:27
 * To change this template use File | Settings | File Templates.
 */
public class NewPotentialFragment extends ModelFragment {

    private static final int NEW_POTENTIAL = 3;

    public static NewPotentialFragment newFragment() {
        return new NewPotentialFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("FragmentTest", "fragment NewPotentialFragment onCreate");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i("FragmentTest", "fragment NewPotentialFragment  onAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("FragmentTest", "fragment  NewPotentialFragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("FragmentTest", "fragment NewPotentialFragment  onPause");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("FragmentTest", "fragment NewPotentialFragment  onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("FragmentTest", "fragment  NewPotentialFragment onStop");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("FragmentTest", "fragment NewPotentialFragment  onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("FragmentTest", "fragment  NewPotentialFragment onDestroyView");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("FragmentTest", "fragment  NewPotentialFragment onViewCreated");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("FragmentTest", "fragment  NewPotentialFragment onActivityCreated");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("FragmentTest", "fragment  NewPotentialFragment onDestroy");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("FragmentTest", "fragment  NewPotentialFragment onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public List<BookRecommendItem> getBookRecommendItemList(OperationWebService operationWebService) throws ZLNetworkException {
        return operationWebService.getBookRecommends(NEW_POTENTIAL);
    }

    @Override
    public List<BookItem> getBookItemList(BookWebService bookWebService, int pageNum) throws ZLNetworkException {
        return bookWebService.getNewPotentialBooks(pageNum);
    }
}
