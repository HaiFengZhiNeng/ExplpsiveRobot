package com.example.explosiverobot.udp.net;

import java.net.Socket;

/**
 * Created by zhangyuanyuan on 2017/10/18.
 * Tcp连接的请求监听
 */

public interface TcpConnRequestListener {

    void onSuccess(Socket mClient);
    void onFail(Exception e);

}
