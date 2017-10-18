package com.example.explosiverobot.udp.net;

/**
 * Created by zhangyuanyuan on 2017/10/18.
 */

public interface SocketRequestListener {

    void onFail(Exception e);

    void onSuccess(String result);

}