package com.example.explosiverobot.udp.progress;

/**
 * Created by zhangyuanyuan on 2017/10/18.
 * 进度回调接口，比如用于文件上传与下载
 */

public interface ProgressListener {

    void onProgress(long currentBytes, long contentLength, boolean done);

}
