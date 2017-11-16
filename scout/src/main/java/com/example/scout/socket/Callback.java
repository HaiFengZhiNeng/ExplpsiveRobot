package com.example.scout.socket;

/**
 * Created by zhangyuanyuan on 2017/11/16.
 */

public abstract class Callback {

    public abstract void onError(Exception e);

    public abstract void onSuccess(String t);

    public abstract void onFail(String msg);
}
