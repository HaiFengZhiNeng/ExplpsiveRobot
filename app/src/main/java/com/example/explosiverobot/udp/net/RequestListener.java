package com.example.explosiverobot.udp.net;

import java.io.IOException;

import okhttp3.Request;

/**
 * Created by zhangyuanyuan on 2017/10/18.
 */

public interface RequestListener {

    void onFailure(Request request, IOException e);
    void onSuccess(String result);

}
