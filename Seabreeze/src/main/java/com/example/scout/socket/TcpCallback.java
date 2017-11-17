package com.example.scout.socket;

import com.seabreeze.log.Print;

/**
 * Created by zhangyuanyuan on 2017/11/16.
 */

public abstract class TcpCallback extends Callback {


    @Override
    public void onError(Exception e) {
        Print.e("onError");
    }

    @Override
    public void onFail(String msg) {
        Print.e("onFail");
    }

}
