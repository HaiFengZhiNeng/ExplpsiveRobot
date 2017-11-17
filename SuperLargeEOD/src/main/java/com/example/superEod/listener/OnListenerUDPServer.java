package com.example.superEod.listener;

/**
 * Created by zhangyuanyuan on 2017/11/1.
 */

public interface OnListenerUDPServer {

    void receiver(String receiver);

    void acquireIp(boolean isAcquire);

}
