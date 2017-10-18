//package com.example.explosiverobot.base;
//
//import android.os.Bundle;
//
//import com.ocean.mvp.library.presenter.BasePresenter;
//
//import butterknife.ButterKnife;
//
///**
// * Created by dell on 2017/10/17.
// */
//
//public abstract class BaseFragment<T extends BasePresenter> extends com.ocean.mvp.library.view.BaseFragment {
//
//    protected T mPresenter;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        mPresenter = createPresenter();
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public abstract int getContentViewResource();
//
//    public abstract T createPresenter();
//
//    @Override
//    protected void onViewCreateBefore() {
//        mPresenter.onViewCreateBefore();
//    }
//
//
//    @Override
//    protected void onViewCreated() {
//        mPresenter.onViewCreated();
//        ButterKnife.bind(getActivity());
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mPresenter.onResume();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        mPresenter.onStop();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mPresenter.onPause();
//
//    }
//
//    @Override
//    public void onDestroy() {
//        mPresenter.onDestroy();
//        super.onDestroy();
//    }
//
//}
