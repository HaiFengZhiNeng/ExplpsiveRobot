package com.example.explosiverobot.udp.net;

/**
 * Created by zhangyuanyuan on 2017/10/18.
 * Tcp发送文本文件监听
 */

public interface SendRequestListener<T extends NetMessage> {

    void onSending(T message, long total, long current);
    void onSuccess(T message, String result);
    void onFail(T message, int errorCode, String errorMessage);

}
