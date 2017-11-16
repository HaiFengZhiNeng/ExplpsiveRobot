package com.example.scout.socket;

/**
 * Created by zhangyuanyuan on 2017/11/16.
 */

public interface TCPTextSendListener {

    void onReadFail(Exception e);

    void onReadSuccess(String read);

    void onOtherFail(Exception e);
}
