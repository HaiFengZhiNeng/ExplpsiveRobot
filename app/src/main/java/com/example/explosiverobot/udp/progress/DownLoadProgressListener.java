package com.example.explosiverobot.udp.progress;

import okhttp3.Request;

/**
 * Created by zhangyuanyuan on 2017/10/18.
 */

public abstract class DownLoadProgressListener extends UIProgressListener {

    public abstract void onSuccess(String savePath);

    public abstract void onFailure(Exception e, Request request);

}
