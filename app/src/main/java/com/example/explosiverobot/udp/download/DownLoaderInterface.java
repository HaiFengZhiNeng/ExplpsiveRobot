package com.example.explosiverobot.udp.download;

import java.io.File;

import okhttp3.Response;

/**
 * Created by zhangyuanyuan on 2017/10/18.
 */

public interface DownLoaderInterface {

    void downloadPoint(File file, Response response, long startPoint);//下载的起始位置

}
