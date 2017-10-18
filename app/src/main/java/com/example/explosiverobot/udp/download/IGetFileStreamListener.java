package com.example.explosiverobot.udp.download;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Request;

/**
 * Created by zhangyuanyuan on 2017/10/18.
 */

public interface IGetFileStreamListener {

    void onFailure(Request request, IOException e);
    void onSuccess(InputStream inputStream);

}
